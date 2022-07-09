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

	@Test
	public void parseValidImportStatement() {
		parseModelFromStringAndExpectSuccess("from module import var");
	}

	@Test
	public void parseInvalidImportStatement() {
		parseModelFromStringAndExpectFail("from import var");
		parseModelFromStringAndExpectFail("from module import");
		parseModelFromStringAndExpectFail("module import var");
		parseModelFromStringAndExpectFail("from module var");
		parseModelFromStringAndExpectFail("from import");
	}

	@Test
	public void parseValidLocalVariableDeclarationStatement() {
		parseModelFromStringAndExpectSuccess("var = 1");
		parseModelFromStringAndExpectSuccess("var = 1.5");

		parseModelFromStringAndExpectSuccess("v = \"sdsdf\"");

		parseModelFromStringAndExpectSuccess("var = []");
		parseModelFromStringAndExpectSuccess("var = [1]");
		parseModelFromStringAndExpectSuccess("var = [1,4]");
		parseModelFromStringAndExpectSuccess("var = [\"ab\",\"cd\"]");
		parseModelFromStringAndExpectSuccess("var = [\"ab\",5,5.6]");
	}

	@Test
	public void parseInvalidLocalVariableDeclarationStatement() {
		parseModelFromStringAndExpectFail("var =");
		parseModelFromStringAndExpectFail("var 5");
		parseModelFromStringAndExpectFail("= 5");
		parseModelFromStringAndExpectFail("var = [5");
		parseModelFromStringAndExpectFail("var = [5,");
		parseModelFromStringAndExpectFail("var = [5,5,");
		parseModelFromStringAndExpectFail("var = ]");
		parseModelFromStringAndExpectFail("var = 5,5]");
		parseModelFromStringAndExpectFail("var = [,]");

		//tuples should be allowed as variable declaration: must be fixed in the grammar
		parseModelFromStringAndExpectFail("var = (1,4)");
		parseModelFromStringAndExpectFail("var = 1,4");

		//to define strings '' should also be allowed: must be fixed in the grammar
		parseModelFromStringAndExpectFail("v = 'sdsdf'");
	}

	@Test
	public void parseValidIfStatement() {
		parseModelFromStringAndExpectSuccess(
				"if x == 1:\n" +
						"    print(\"one\")"
		);

		//this test case should not be possible: use of empty body in this case has to be fixed in the grammar
		parseModelFromStringAndExpectFail(
				"if x == 1:\n" +
						"    "
		);
	}

	@Test
	public void parseInvalidIfStatement() {
		//in normal python it should be possible to write an if-statement in one line, however it is not yet implemented int this language.
		parseModelFromStringAndExpectFail("if x == 1: print(\"one\")");
		parseModelFromStringAndExpectFail("if x == 1:\nprint(\"one\")");

		//missing ":"
		parseModelFromStringAndExpectFail(
				"if x == 1\n" +
						"    print(\"one\")"
		);

		//missing condition
		parseModelFromStringAndExpectFail(
				"if :\n" +
						"    print(\"one\")"
		);

		//missing if
		parseModelFromStringAndExpectFail(
				"x == 1:\n" +
						"    print(\"one\")"
		);
	}

	@Test
	public void parseValidForLoop() {
		parseModelFromStringAndExpectSuccess(
				"for x in [0,1,2]:\n" +
				"    print(x)"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in range(0, 3):\n" +
						"    print(x)"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in range(3):\n" +
						"    print(x)"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    print(x)"
		);

		parseModelFromStringAndExpectSuccess(
				"for x in collection_var:\n" +
						"    print(x)\n" +
						"else:\n" +
						"    print(y)"
		);
	}

	@Test
	public void parseInvalidForLoop() {
		//missing ":"
		parseModelFromStringAndExpectFail(
				"for x in [0,1,2]\n" +
						"    print(x)"
		);
		//missing "in"
		parseModelFromStringAndExpectFail(
				"for x [0,1,2]:\n" +
						"    print(x)"
		);
		//missing "for"
		parseModelFromStringAndExpectFail(
				"x in [0,1,2]:\n" +
						"    print(x)"
		);
	}

	@Test
	public void parseValidWhileLoop() {
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
				"    print(i)\n" +
				"    i += 1"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
				"    break"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    print(i)\n" +
						"    if i == 3:\n" +
						"        break\n" +
						"    i += 1"
		);
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    i += 1\n" +
						"    if i == 3:\n" +
						"        continue\n" +
						"    print(i)"
		);

		//else statements for while loops
		parseModelFromStringAndExpectSuccess(
				"while i < 6:\n" +
						"    i += 1\n" +
						" else:" +
						"    print(x)"
		);
	}

	@Test
	public void parseInvalidWhileLoop() {
		//missing "while"
		parseModelFromStringAndExpectFail(
				" i < 6:\n" +
						"    print(i)\n" +
						"    i += 1"
		);
		//missing condition
		parseModelFromStringAndExpectFail(
				"while :\n" +
						"    print(i)\n" +
						"    i += 1"
		);
		//missing ":"
		parseModelFromStringAndExpectFail(
				"while i < 6\n" +
						"    print(i)\n" +
						"    i += 1"
		);
	}

	@Test
	public void parseValidFunctionDeclaration() {
		parseModelFromStringAndExpectSuccess(
				"def function_name(x):\n" +
				"    print(x)"
		);
		parseModelFromStringAndExpectSuccess(
				"def function_name(x,y,z):\n" +
				"    print(x,y,z)"
		);
		parseModelFromStringAndExpectSuccess(
				"def absolute_value(num):\n" +
						"    if num >= 0:\n" +
						"        return num"
		);

		parseModelFromStringAndExpectSuccess(
				"def absolute_value():\n" +
						"    return"
		);
	}

	@Test
	public void parseInvalidFunctionDeclaration() {
		//missing "def"
		parseModelFromStringAndExpectFail(
				" function_name(x):\n" +
						"    print(x)"
		);
		//missing function name
		parseModelFromStringAndExpectFail(
				"def (x):\n" +
						"    print(x)"
		);
		//missing function parameters
		parseModelFromStringAndExpectFail(
				"def function_name:\n" +
						"    print(x)"
		);
		//missing ":"
		parseModelFromStringAndExpectFail(
				"def function_name(x)\n" +
						"    print(x)"
		);
	}

//	---------------------------------------------------------------
//	Tests for whole scripts from files.
//	---------------------------------------------------------------

	@Test
	public void parseSimplePython() {
		String model = "python/simple_python.sipy";
		parseModelFromFileAndExpectSuccess(model);
	}

	/**
	 * In some cases the parser only recognises one indentation error at once (although the file contains multiple).
	 * However, if the recognised error is fixed, the next error is detected.
	 * Thus, the test cases at least for indentation errors must include only one error.
	 */
	@Test
	public void parsePythonWithIndentError() {
		String model = "python/python_IndentError.sipy";
		parseModelFromFileAndExpectErrors(model, 1);
	}

}
