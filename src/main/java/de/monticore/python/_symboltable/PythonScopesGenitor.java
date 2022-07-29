package de.monticore.python._symboltable;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.python._ast.*;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._parser.SIPythonParser;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class PythonScopesGenitor extends PythonScopesGenitorTOP {

	private final List<String> pythonKeywords = new LinkedList<>();


	public PythonScopesGenitor() {
		super();
		this.initPythonKeywords();
	}

	private void initPythonKeywords() {
		try {
			File keywordsScript = new File("keywords.py");

			if (!keywordsScript.exists()) {
				keywordsScript.createNewFile();
			}

			FileWriter writer = new FileWriter(keywordsScript);
			writer.write(
					"import keyword\n" +
							"\n" +
							"for k in keyword.kwlist:\n" +
							"    print(k)\n");
			writer.close();

			// run the generator script
			Process process = Runtime.getRuntime().exec("python3 keywords.py");

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String s;

			while ((s = stdInput.readLine()) != null) {
				pythonKeywords.add(s);
			}

			keywordsScript.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.error("Unable to create keyword list");
		}
	}

	public void createBuiltinPythonFunctionsScope() {
		try {
			File builtinFunctionsReolverScript = new File("builtin_functions_resolver.py");

			if (!builtinFunctionsReolverScript.exists()) {
				builtinFunctionsReolverScript.createNewFile();
			}

			// python script to generate a python script which declares all builtin functions
			FileWriter writer = new FileWriter(builtinFunctionsReolverScript);
			writer.write(
					"import types\n" +
							"import builtins\n" +
							"\n" +
							"\n" +
							"def describe_builtin(obj):\n" +
							"    result = \"def \"\n" +
							"    result += obj.__name__\n" +
							"    docstr = obj.__doc__\n" +
							"    args = ''\n" +
							"\n" +
							"    if docstr:\n" +
							"        items = docstr.split('\\n')\n" +
							"        if items:\n" +
							"            func_descr = items[0]\n" +
							"            s = func_descr.replace(obj.__name__, '')\n" +
							"            idx1 = s.find('(')\n" +
							"            idx2 = s.find(')', idx1)\n" +
							"            if idx1 != -1 and idx2 != -1 and (idx2 > idx1 + 1):\n" +
							"                args = s[idx1 + 1:idx2]\n" +
							"\n" +
							"    result += \"(\"\n" +
							"\n" +
							"    # result += args\n" +
							"\n" +
							"    result += \"):\\n    null\\n\"\n" +
							"\n" +
							"    return result\n" +
							"\n" +
							"\n" +
							"output = \"\"\n" +
							"\n" +
							"for name, obj in vars(builtins).items():\n" +
							"    if isinstance(obj, types.BuiltinFunctionType):\n" +
							"        output += describe_builtin(obj)\n" +
							"\n" +
							"print(output)\n");
			writer.close();

			// run the generator script
			Process process = Runtime.getRuntime().exec("python3 builtin_functions_resolver.py");

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			StringBuilder result = new StringBuilder();

			String s;

			while ((s = stdInput.readLine()) != null) {
				result.append(s).append("\n");
			}

			builtinFunctionsReolverScript.delete();

			// parse generated script with builtin function declarations
			Optional<ASTPythonScript> ast = new SIPythonParser().parse_String(result.toString());

			if (ast.isEmpty()) {
				Log.error("Unable to parse builtin python functions");
				return;
			}

			// create symbol table
			IPythonArtifactScope scope = SIPythonMill.scopesGenitorDelegator().createFromAST(ast.get());

			// set scope name to exclude from parameter size check coco
			scope.setName("__builtin__");

			SIPythonMill.globalScope().addSubScope(scope);

		} catch (IOException e) {
			e.printStackTrace();
			Log.error("Unable to create builtin python function scope");
		}
	}

	public void createImportScopes(ASTPythonScript ast) {
		try {
			File functionsResolverScript = new File("import_function_resolver.py");

			if (!functionsResolverScript.exists()) {
				functionsResolverScript.createNewFile();
			}

			// create symbol table for each import
			for (ASTStatement statement : ast.getStatementList()) {
				if (statement instanceof ASTImportStatement) {
					String moduleName = ((ASTImportStatement) statement).isPresentModule() ? ((ASTImportStatement) statement).getModule() : null;
					for (String name : ((ASTImportStatement) statement).getNameList()) {
						this.createImportScope(functionsResolverScript, moduleName, name);
					}
				}
			}

			functionsResolverScript.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.error("Unable to create symbol table for imports");
		}
	}

	private void createImportScope(File functionsResolverScript, String moduleName, String name) throws IOException {

		// python script to generate python script, which declares all functions and classes of the imported file
		FileWriter writer = new FileWriter(functionsResolverScript);
		writer.write(
				"from inspect import getmembers, isfunction, signature, isclass\n" +
						"\n" +
						(moduleName != null ? ("from " + moduleName + " ") : "") + "import " + name + "\n" +
						"\n" +
						"for name, obj in getmembers(" + name + ", isfunction):\n" +
						"    print(\"def \" + name + str(signature(obj)) + \":\\n    null\\n\")\n" +
						"for name, obj in getmembers(" + name + ", isclass):\n" +
						"    print(\"class \" + name + \":\")\n" +
						"    for name, obj in getmembers(obj, isfunction):\n" +
						"        print(\"    def \" + name + str(signature(obj)) + \":\\n        null\")\n" +
						"    else:\n" +
						"        print(\"    def __init__(self):\\n        null\")\n" +
						"print()\n");
		writer.close();

		// run the generator script
		Process process = Runtime.getRuntime().exec("python3 import_function_resolver.py");

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuilder result = new StringBuilder();

		String s;

		while ((s = stdInput.readLine()) != null) {
			result.append(s.replace("'", "\"")).append("\n");
		}

		// parse generated file
		Optional<ASTPythonScript> importAst = new SIPythonParser().parse_String(result.toString());

		if (importAst.isEmpty()) {
			Log.error("Unable to create ast for import " + name);
			return;
		}

		// build symbol table of the imported file
		IPythonArtifactScope scope = SIPythonMill.scopesGenitorDelegator().createFromAST(importAst.get());

		// add to global scope
		SIPythonMill.globalScope().addSubScope(scope);
	}

	private void checkNameIsKeyword(ISymbol symbol) {
		this.checkNameIsKeyword(symbol, symbol.getSourcePosition());
	}

	private void checkNameIsKeyword(ISymbol symbol, SourcePosition position) {
		if (this.pythonKeywords.contains(symbol.getName())) {
			Log.error("Symbol '" + symbol.getName() + "' must not be a keyword " + position);
		}
	}

	@Override
	public void visit(de.monticore.python._ast.ASTVariableDeclaration node) {
		if (getCurrentScope().isPresent()) {
			Optional<VariableSymbol> variableSymbolOptional = getCurrentScope().get().resolveVariable(node.getName());
			if (variableSymbolOptional.isPresent()) {
				node.setSymbol(variableSymbolOptional.get());
				node.setEnclosingScope(variableSymbolOptional.get().getEnclosingScope());
				initVariableHP1(node.getSymbol());
			} else {
				super.visit(node);
			}
		} else {
			Log.warn("0xA5021x71517 Symbol cannot be added to current scope, since no scope exists.");
		}
	}

	@Override
	public void endVisit(ASTVariableDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	@Override
	public void endVisit(ASTSimpleFunctionDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	@Override
	public void endVisit(ASTClassFunctionDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

	@Override
	public void visit(ASTClassDeclaration node) {
		super.visit(node);

		// create class field symbols via the __init__ method
		for (ASTClassStatement classStatement : node.getClassStatementBlock().getClassStatementBlockBody().getClassStatementList()) {
			if (classStatement instanceof ASTClassFunctionDeclaration) {
				ASTClassFunctionDeclaration functionDeclaration = ((ASTClassFunctionDeclaration) classStatement);

				if (functionDeclaration.getName().equals("__init__")) {
					for (ASTStatement functionStatement : functionDeclaration.getStatementBlock().getStatementBlockBody().getStatementList()) {
						if (functionStatement instanceof ASTExpressionStatement) {
							ASTExpressionStatement expressionStatement = ((ASTExpressionStatement) functionStatement);
							if (expressionStatement.getExpression() instanceof ASTAssignmentExpression) {
								ASTAssignmentExpression assignmentExpression = (ASTAssignmentExpression) expressionStatement.getExpression();

								if (assignmentExpression.getLeft() instanceof ASTFieldAccessExpression) {
									String name = ((ASTFieldAccessExpression) assignmentExpression.getLeft()).getName();
									FieldSymbol symbol = new FieldSymbol(name);
									this.checkNameIsKeyword(symbol, assignmentExpression.get_SourcePositionStart());
									node.getSpannedScope().add(symbol);
								}

							}
						}
					}
				}
			}
		}
	}

	@Override
	public void endVisit(ASTClassDeclaration node) {
		super.endVisit(node);
		this.checkNameIsKeyword(node.getSymbol());
	}

}
