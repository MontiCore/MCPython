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
		parseModelFromStringAndExpectSuccess("var = (1,4)");
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

		parseModelFromStringAndExpectSuccess(
				"if x == 1:\n" +
						"    print(\"one\")\n" +
						"elif x == 0:\n" +
						"    print(\"zero\")\n" +
						"else:\n" +
						"    print(\"not one or zero\")"
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

		// missing condition at elif
		parseModelFromStringAndExpectFail(
				"if x == 1:\n" +
						"    print(\"one\")" +
						"elif:\n" +
						"    print(\"zero\")" +
						"else:\n" +
						"    print(\"not one or zero\""
		);

		// empty statement block
		parseModelFromStringAndExpectFail(
				"if x == 1:\n" +
						"    "
		);
	}

	@Test
	public void parseValidTrenaryOperator(){
		parseModelFromStringAndExpectSuccess("print(\"Both a and b are equal\" if a == b else \"a is greater than b\" " +
				"if a > b else \"b is greater than a\")");
		parseModelFromStringAndExpectSuccess("min = a if a < b else b");
		parseModelFromStringAndExpectSuccess("min = a if a < b else b if y == c else c");
		parseModelFromStringAndExpectSuccess("x = \"a\" if a < b else \"b\"");
		parseModelFromStringAndExpectSuccess("print(a,\"is greater\") if (a>b) else print(b,\"is Greater\")");
		parseModelFromStringAndExpectSuccess("x = 1 if True else 0");
	}
	@Test
	public void parseInvalidTrenaryOperator(){
		//trenary operator without if condition
		parseModelFromStringAndExpectFail("x = a if else b");
		//trenary operator without then statement
		parseModelFromStringAndExpectFail("x = if a==b else u");
		//trenary operator without else condition
		parseModelFromStringAndExpectFail("x = u if a==b else");

	}

	@Test
	public void parseValidForLoopStatement() {
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
	public void parseInvalidForLoopStatement() {
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
	public void parseValidWhileLoopStatement() {
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
	public void parseInvalidWhileLoopStatement() {
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
				"def function_name(x,y,z=1):\n" +
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
		// missing default value
		parseModelFromStringAndExpectFail(
				" function_name(x=):\n" +
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

	@Test
	public void parseValidClassFunction() {
		parseModelFromStringAndExpectSuccess(
				"class class_name:\n" +
						"    def function_name(x,y):\n" +
						"        print(x,y)"
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

		@Test
		public void parseInvalidClassDeclaration(){
			//missing class name
			parseModelFromStringAndExpectFail(
					"class:\n"+
							"    count = 0\n" +
							"    list_x = []\n" +
							"    def__init__(self, i):\n" +
							"        self.id=i\n" +
							"    	   e.count+=1\n" +
							"    	   self.list_x.append(i)"
			);
			//missing parameter in funtion that is in class (normally it's mentioned as the self parameter)
			parseModelFromStringAndExpectFail(
					"class ST:\n"+
							"    eleven = 11\n" +
							"    def oneLiner():\n" +
							"        print(eleven)"
			);
			//for loop in class
			parseModelFromStringAndExpectFail(
					"class myClass:\n" +
							"    def function_name(x,y):\n" +
							"        print(x,y)" +
							"    for i in range(4):" +
							"        print(i)"
			);

		}
		//



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
