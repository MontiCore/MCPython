package de.monticore.sipython;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

public class PythonTest extends AbstractTest {

	@Before
	public void init() {
		Log.init();
		Log.enableFailQuick(false);
	}

//	---------------------------------------------------------------
//	Tests for single code snippets from strings.
//	---------------------------------------------------------------


	/*===========================Statements======================================*/

	//import valid statements
	@Test
	public void parseValidImportStatement() {
		parseModelFromStringAndExpectSuccess("from module import var\n");
		parseModelFromStringAndExpectSuccess("from module import var as alias\n");
	}

	@Test
	//import invalid statements
	public void parseInvalidImportStatement() {
		parseModelFromStringAndExpectFail("from import var\n");
		parseModelFromStringAndExpectFail("from module import\n");
		parseModelFromStringAndExpectFail("module import var\n");
		parseModelFromStringAndExpectFail("from module var\n");
		parseModelFromStringAndExpectFail("from import\n");

		parseModelFromStringAndExpectFail("from import var as\n");
		parseModelFromStringAndExpectFail("from module import as\n");
		parseModelFromStringAndExpectFail("module import var as\n");
		parseModelFromStringAndExpectFail("from module var as\n");
		parseModelFromStringAndExpectFail("from import as\n");

		parseModelFromStringAndExpectFail("from module import var as\n");
	}

	//valid if-else statements
	@Test
	public void parseValidIfStatement() {
		parseModelFromStringAndExpectSuccess(
				"if x == 1:\n" +
						"    print(\"one\")\n"
		);

		parseModelFromStringAndExpectSuccess(
				"if x == 1:\n" +
						"    print(\"one\")\n" +
						"elif x == 0:\n" +
						"    print(\"zero\")\n" +
						"else:\n" +
						"    print(\"not one or zero\")\n"
		);
	}

	//invalid if-else statements
	@Test
	public void parseInvalidIfStatement() {
		//in normal python it should be possible to write an if-statement in one line, however it is not yet implemented int this language.
		parseModelFromStringAndExpectFail("if x == 1: print(\"one\")\n");
		parseModelFromStringAndExpectFail("if x == 1:\nprint(\"one\")\n");

		//missing ":"
		parseModelFromStringAndExpectFail(
				"if x == 1\n" +
						"    print(\"one\")\n"
		);

		//missing condition
		parseModelFromStringAndExpectFail(
				"if :\n" +
						"    print(\"one\")\n"
		);

		//missing if
		parseModelFromStringAndExpectFail(
				"x == 1:\n" +
						"    print(\"one\")\n"
		);

		// missing condition at elif
		parseModelFromStringAndExpectFail(
				"if x == 1:\n" +
						"    print(\"one\")\n" +
						"elif:\n" +
						"    print(\"zero\")\n" +
						"else:\n" +
						"    print(\"not one or zero\"\n"
		);

		// empty statement block
		parseModelFromStringAndExpectFail(
				"if x == 1:\n" +
						"    \n"
		);
	}

	//valid assert statements
	@Test
	public void parseValidAssertStatements() {
		parseModelFromStringAndExpectSuccess(
				"assert 2 > 3, \"Two is not greater than three\"\n"
		);
		parseModelFromStringAndExpectSuccess(
				"x = 1\n" +
						"assert x > 0, \"x is too low \"\n"
		);
	}

	//invalid assert statements
	@Test
	public void parseInvalidAssertStatements() {
		// missing error string
		parseModelFromStringAndExpectFail(
				"assert 2 > 3,\n"
		);

		// missing condition expression
		parseModelFromStringAndExpectFail(
				"assert , \"missing condition \"\n"
		);

		// missing comma
		parseModelFromStringAndExpectFail(
				"assert 2 > 3 \"Two is not greater than three\"\n"
		);
	}

	//valid for statements
	@Test
	public void parseValidForLoopStatement() {
		parseModelFromStringAndExpectSuccess(
				"for x in [0,1,2]:\n" +
						"    print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x,y in [1]:\n" +
						"    print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in range(0, 3):\n" +
						"    print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in range(3):\n" +
						"    print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    if (x == 1):\n" +
						"        break\n" +
						"		 print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    if (x == 1):\n" +
						"        continue\n" +
						"		 print(x)\n"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    print(x)\n" +
						"else:\n" +
						"    print(y)\n"
		);
	}

	//invalid for statements
	@Test
	public void parseInvalidForLoopStatement() {
		//missing ":"
		parseModelFromStringAndExpectFail(
				"for x in [0,1,2]\n" +
						"    print(x)\n"
		);
		//missing "in"
		parseModelFromStringAndExpectFail(
				"for x [0,1,2]:\n" +
						"    print(x)\n"
		);
		//missing "for"
		parseModelFromStringAndExpectFail(
				"x in [0,1,2]:\n" +
						"    print(x)\n"
		);
	}

	//valid while statements
	@Test
	public void parseValidWhileLoopStatement() {
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    print(i)\n" +
						"    i += 1\n"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    break\n"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    print(i)\n" +
						"    if i == 3:\n" +
						"        break\n" +
						"    i += 1\n"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    i += 1\n" +
						"    if i == 3:\n" +
						"        continue\n" +
						"    print(i)\n"
		);

		//else statements for while loops
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    i += 1\n" +
						" else:\n" +
						"    print(x)\n"
		);
	}

	//invalid while statements
	@Test
	public void parseInvalidWhileLoopStatement() {
		//missing "while"
		parseModelFromStringAndExpectFail(
				" i < 6:\n" +
						"    print(i)\n" +
						"    i += 1\n"
		);
		//missing condition
		parseModelFromStringAndExpectFail(
				"while :\n" +
						"    print(i)\n" +
						"    i += 1\n"
		);
		//missing ":"
		parseModelFromStringAndExpectFail(
				"while i < 6\n" +
						"    print(i)\n" +
						"    i += 1\n"
		);
	}

	//valid try-except-finally statements
	@Test
	public void parseValidTryExceptStatements() {
		parseModelFromStringAndExpectSuccess(
				"try:\n" +
						"    i = 1//0\n" +
						"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n"
		);
		parseModelFromStringAndExpectSuccess(
				"try:\n" +
						"    i = 1//0\n" +
						"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n" +
						"finally:\n" +
						"    print(\"Done\")\n"
		);
		parseModelFromStringAndExpectSuccess(
				"try:\n" +
						"    i = 1//0\n" +
						"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n" +
						"else:\n" +
						"    print(\"Success\")\n"
		);
		parseModelFromStringAndExpectSuccess(
				"try:\n" +
						"    i = 1//0\n" +
						"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n" +
						"else:\n" +
						"    print(\"Success\")\n" +
						"finally:\n" +
						"    print(\"Done\")\n"
		);

	}

	//invalid try-except-finally statements
	@Test
	public void parseInvalidTryExceptStatements() {
		// missing except
		parseModelFromStringAndExpectFail(
				"try:\n" +
						"    i = 1//0\n" +
						"else:\n" +
						"    print(\"Success\")\n" +
						"finally:\n" +
						"    print(\"Done\")\n"
		);

		// missing try
		parseModelFromStringAndExpectFail(
				"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n" +
						"else:\n" +
						"    print(\"Success\")\n"
		);

		// duplicate finally
		parseModelFromStringAndExpectFail(
				"try:\n" +
						"    i = 1//0\n" +
						"except ZeroDivisionError:\n" +
						"    print(\"Can not divide by zero\")\n" +
						"else:\n" +
						"    print(\"Success\")\n" +
						"finally:\n" +
						"    print(\"Done\")\n" +
						"finally:\n" +
						"    print(\"Done\")\n"
		);
	}

	//valid with open file statements
	@Test
	public void parseValidWithOpenFileStatements() {
		parseModelFromStringAndExpectSuccess("with open(newfile, 'w') as outfile:\n" +
				"    print(\"Hello World\")\n");
		parseModelFromStringAndExpectSuccess("with open(newfile, 'w') as outfile:\n" +
				"    with open(oldfile, 'r', encoding='utf-8') as infile:\n" +
				"        print(\"Hello World\")\n");
		parseModelFromStringAndExpectSuccess("with open(newfile, 'w') as outfile, open(oldfile, 'r', encoding='utf-8') as infile:\n" +
				"	print(\"Hello World\")\n");

	}

	//invalid with open file statements
	@Test
	public void parseInvalidWithOpenFileStatements() {
		//missing with
		parseModelFromStringAndExpectFail(" open(newfile, 'w') as outfile:\n" +
				"    print(\"Hello World\")\n");
		//missing file name
		parseModelFromStringAndExpectFail("with open(newfile, 'w') as :\n" +
				"    print(\"Hello World\")\n");
		//missing open function
		parseModelFromStringAndExpectFail("with as outfile:\n" +
				"    print(\"Hello World\")\n");

	}

	//valid local variable declaration
	@Test
	public void parseValidLocalVariableDeclarationStatement() {
		parseModelFromStringAndExpectSuccess("var = 1\n");
		parseModelFromStringAndExpectSuccess("var = 1.5\n");

		parseModelFromStringAndExpectSuccess("v = \"sdsdf\"\n");

		parseModelFromStringAndExpectSuccess("var = []\n");
		parseModelFromStringAndExpectSuccess("var = [1]\n");
		parseModelFromStringAndExpectSuccess("var = [1,4]\n");
		parseModelFromStringAndExpectSuccess("var = [\"ab\",\"cd\"]\n");
		parseModelFromStringAndExpectSuccess("var = [\"ab\",5,5.6]\n");
		parseModelFromStringAndExpectSuccess("var = (1,4)\n");
		parseModelFromStringAndExpectSuccess("v = 'sdsdf'\n");
	}

	//invalid local variable declaration
	@Test
	public void parseInvalidLocalVariableDeclarationStatement() {
		parseModelFromStringAndExpectFail("var =\n");
		parseModelFromStringAndExpectFail("var 5\n");
		parseModelFromStringAndExpectFail("= 5\n");
		parseModelFromStringAndExpectFail("var = [5\n");
		parseModelFromStringAndExpectFail("var = [5,\n");
		parseModelFromStringAndExpectFail("var = [5,5,\n");
		parseModelFromStringAndExpectFail("var = ]\n");
		parseModelFromStringAndExpectFail("var = 5,5]\n");
		parseModelFromStringAndExpectFail("var = [,]\n");

		//tuples should be allowed as variable declaration: must be fixed in the grammar
		parseModelFromStringAndExpectFail("var = 1,4\n");
	}

	// valid function declaration
	@Test
	public void parseValidFunctionDeclaration() {
		parseModelFromStringAndExpectSuccess(
				"def function_name(x):\n" +
						"    print(x)\n"
		);
		parseModelFromStringAndExpectSuccess(
				"def function_name(x,y,z):\n" +
						"    print(x,y,z)\n"
		);
		parseModelFromStringAndExpectSuccess(
				"def function_name(x,y,z=1):\n" +
						"    print(x,y,z)\n"
		);
		parseModelFromStringAndExpectSuccess(
				"def absolute_value(num):\n" +
						"    if num >= 0:\n" +
						"        return num\n"
		);

		parseModelFromStringAndExpectSuccess(
				"def absolute_value():\n" +
						"    return\n"
		);
	}

	// invalid function declaration
	@Test
	public void parseInvalidFunctionDeclaration() {
		//missing "def"
		parseModelFromStringAndExpectFail(
				" function_name(x):\n" +
						"    print(x)\n"
		);
		// missing default value
		parseModelFromStringAndExpectFail(
				" function_name(x=):\n" +
						"    print(x)\n"
		);
		//missing function name
		parseModelFromStringAndExpectFail(
				"def (x):\n" +
						"    print(x)\n"
		);
		//missing function parameters
		parseModelFromStringAndExpectFail(
				"def function_name:\n" +
						"    print(x)\n"
		);
		//missing ":"
		parseModelFromStringAndExpectFail(
				"def function_name(x)\n" +
						"    print(x)\n"
		);
	}

	// valid lambda statement
	@Test
	public void parseValidLambdaStatement() {
		parseModelFromStringAndExpectSuccess("lambda: 1\n");
		parseModelFromStringAndExpectSuccess("lambda x: x\n");
		parseModelFromStringAndExpectSuccess("lambda x, y: x + y\n");
	}

	//invalid lambda statement
	@Test
	public void parseInvalidLambdaStatement() {
		parseModelFromStringAndExpectFail("lambda:\n");
		parseModelFromStringAndExpectFail("lambda x: \n");
		parseModelFromStringAndExpectFail("lambda x, y z\n");
	}

	// valid lambda statement
	@Test
	public void parseValidRaiseStatement() {
		parseModelFromStringAndExpectSuccess("raise RuntimeError('Error')\n");
		parseModelFromStringAndExpectSuccess("raise\n");
	}

	//invalid lambda statement
	@Test
	public void parseInvalidRaiseStatement() {
		parseModelFromStringAndExpectFail("raise RuntimeError('Error'), ArithmeticError('Error')\n");
		parseModelFromStringAndExpectFail("raise RuntimeError('Error') ArithmeticError('Error')\n");
	}

	/*===========================Literals======================================*/

	// valid string literals python
	@Test
	public void parseValidStringPython() {
		parseModelFromStringAndExpectSuccess("helloworld = \"Hello World\"\n");
		parseModelFromStringAndExpectSuccess("helloworld = 'Hello World'\n");
	}

	// invalid string literals python
	@Test
	public void parseInvalidStringPython() {
		parseModelFromStringAndExpectFail("helloworld = \"Hello World\n");
		parseModelFromStringAndExpectFail("helloworld = 'Hello World\n");
		parseModelFromStringAndExpectFail("helloworld = Hello World\n");
	}

	// boolean literals for python
	@Test
	public void parseValidBooleanPython() {
		parseModelFromStringAndExpectSuccess("true = True\n");
		parseModelFromStringAndExpectSuccess("falseStatement = False\n");
		parseModelFromStringAndExpectSuccess("trueStatement = True\n");

	}

	/*======================Expressions===========================================*/

	// valid ternary-operator expression
	@Test
	public void parseValidTernaryOperator() {
		parseModelFromStringAndExpectSuccess("print(\"Both a and b are equal\" if a == b else \"a is greater than b\" " +
				"if a > b else \"b is greater than a\")\n");
		parseModelFromStringAndExpectSuccess("min = a if a < b else b\n");
		parseModelFromStringAndExpectSuccess("min = a if a < b else b if y == c else c\n");
		parseModelFromStringAndExpectSuccess("x = \"a\" if a < b else \"b\"\n");
		parseModelFromStringAndExpectSuccess("print(a,\"is greater\") if (a>b) else print(b,\"is Greater\")\n");
		parseModelFromStringAndExpectSuccess("x = 1 if True else 0\n");
	}

	// invalid ternary-operator expression
	@Test
	public void parseInvalidTernaryOperator() {
		//trenary operator without if condition
		parseModelFromStringAndExpectFail("x = a if else b\n");
		//trenary operator without then statement
		parseModelFromStringAndExpectFail("x = if a==b else u\n");
		//trenary operator without else condition
		parseModelFromStringAndExpectFail("x = u if a==b else\n");
	}

	// valid logical expressions
	@Test
	public void parseValidLogicalExpressions() {
		parseModelFromStringAndExpectSuccess("(x == 3) and y\n");
		parseModelFromStringAndExpectSuccess("(x == 3) or y\n");
		parseModelFromStringAndExpectSuccess("not True\n");
		parseModelFromStringAndExpectSuccess("not (x==3)\n");
		parseModelFromStringAndExpectSuccess("not (x or y)\n");
		parseModelFromStringAndExpectSuccess("(x == 3) and (z or y)\n");
	}

	// valid mathematical expressions
	@Test
	public void parseValidMathematicalExpressions() {
		parseModelFromStringAndExpectSuccess("x**3\n");
		parseModelFromStringAndExpectSuccess("x ** (8-9)\n");
		parseModelFromStringAndExpectSuccess("3 // 4\n");
		parseModelFromStringAndExpectSuccess("3 // (3+4)\n");
	}

	// valid in and not in expressions
	@Test
	public void parseValidInExpression() {
		parseModelFromStringAndExpectSuccess("1 in x\n");
		parseModelFromStringAndExpectSuccess("1 not in x\n");
	}

	// invalid in and not in expressions
	@Test
	public void parseInValidInExpression() {
		parseModelFromStringAndExpectFail("1 in\n");
		parseModelFromStringAndExpectFail("in x\n");
	}

	/*===========================Classes======================================*/

	// valid class statements
	@Test
	public void parseValidClassFunction() {
		parseModelFromStringAndExpectSuccess(
				"class class_name:\n" +
						"    def function_name(x,y):\n" +
						"        print(x,y)\n"
		);
		parseModelFromStringAndExpectSuccess(
				"class class_name:\n" +
						"    def __init__(self,i=0):\n" +
						"        self.id = i\n"
		);
		parseModelFromStringAndExpectSuccess(
				"class myClass(MySuperClass):\n" +
						"    id=\"\"\n" +
						"    def function_name(x,y):\n" +
						"        print(x,y)\n" +
						"        self.id=id\n" +
						"    def function_name1(x,y):\n" +
						"        print(x,y)\n"
		);

	}

	// invalid class statements
	@Test
	public void parseInvalidClassDeclaration() {
		//missing class name
		parseModelFromStringAndExpectFail(
				"class:\n" +
						"    count = 0\n" +
						"    list_x = []\n" +
						"    def__init__(self, i):\n" +
						"        self.id=i\n" +
						"    	   e.count+=1\n" +
						"    	   self.list_x.append(i)\n"
		);
		//missing parameter in funtion that is in class (normally it's mentioned as the self parameter)
		parseModelFromStringAndExpectFail(
				"class ST:\n" +
						"    eleven = 11\n" +
						"    def oneLiner():\n" +
						"        print(eleven)\n"
		);
		//for loop in class
		parseModelFromStringAndExpectFail(
				"class myClass:\n" +
						"    def function_name(x,y):\n" +
						"        print(x,y)\n" +
						"    for i in range(4):\n" +
						"        print(i)\n"
		);

	}

	/*===========================Other======================================*/
	@Test
	public void parseValidSingleLineComments(){
		parseModelFromStringAndExpectSuccess("#hello world\n");
		parseModelFromStringAndExpectSuccess("#hello world\n" +
				"#this is another comment");
	}

	@Test
	public void parseValidMultiLineStringLiterals(){
		parseModelFromStringAndExpectSuccess("'''Hello world'''\n");
		parseModelFromStringAndExpectSuccess("'''This is another comment\n" +
				"This is the second line of the comment'''\n");
		parseModelFromStringAndExpectSuccess("\"\"\"Double quote multi line \n comment \"\"\"\n");
	}

	@Test
	public void parseInvalidMultiLineStringLiterals(){
		parseModelFromStringAndExpectFail("\"\"\"Hello world\n");
		parseModelFromStringAndExpectFail("'''Hello world\n");
		parseModelFromStringAndExpectFail("'''Hello world\n\"\"\"\n");
		parseModelFromStringAndExpectFail("\" \"\"Double quote multi line \n comment \"\"\"\n");
		parseModelFromStringAndExpectFail("\"\"\"Double quote multi line \n comment \" \"\"\n");
		parseModelFromStringAndExpectFail("\"\"\"This is another comment\n" +
				"This is the second line of the comment\n");
	}


//	---------------------------------------------------------------
//	Tests for whole scripts from files.
//	---------------------------------------------------------------

	@Test
	public void parseSimplePython() {
		String model = "tests/simple_python.sipy";
		parseModelFromFileAndExpectSuccess(model);
	}

	/**
	 * In some cases the parser only recognises one indentation error at once (although the file contains multiple).
	 * However, if the recognised error is fixed, the next error is detected.
	 * Thus, the test cases at least for indentation errors must include only one error.
	 */
	@Test
	public void parsePythonWithIndentError() {
		String model = "tests/python_IndentError.sipy";
		parseModelFromFileAndExpectErrors(model, 1);
	}

	@Test
	public void parseLiteralsAsLine(){
		// Python allows the only element of a line to be a literal like "Test" or 5
		parseModelFromStringAndExpectSuccess("5\n");
		parseModelFromStringAndExpectSuccess("'string'\n");
		parseModelFromStringAndExpectSuccess("\"string\"\n");
		parseModelFromStringAndExpectSuccess("\"\"\"multiline\nstring\"\"\"\n");
	}
}
