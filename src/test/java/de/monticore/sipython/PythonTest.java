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
		parseModelFromStringAndExpectSuccess(
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
