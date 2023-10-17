package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.generator.PrintAsSIPythonScript;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PrintAsSIPythonScriptTest extends AbstractSIPythonTest {

    @Before
    public void init() {
        Log.init();
        Log.enableFailQuick(false);
        SIPythonMill.reset();
        SIPythonMill.init();
        SIUnitsMill.initializeSIUnits();
    }

    // --------------------------------------------------------------
    //  Test cases for PythonPrettyPrinter
    // --------------------------------------------------------------

    @Test
    public void printPintImportScriptPrefix() {
       String printedPythonCode = parseAndReturnPrintedPythonCode(
                       ""
       );
       assertEquals(
               "from pint import UnitRegistry\n" +
                       "ureg = UnitRegistry()\n",
               printedPythonCode);
    }

    @Test
    public void printImportStatement() {
        parsePrintAndAssertEqualityOfOutputCode("from some_module import some_package\n");
    }

    @Test
    public void printLocalVariableDeclaration() {
        parsePrintAndAssertEqualityOfOutputCode("var = 5\n");
        parsePrintAndAssertEqualityOfOutputCode("var = [5, 6, 7]\n");
    }

    @Test
    public void printIfStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "if x == 1:\n" +
                "    print(\"one\")\n"
        );
    }

    @Test
    public void printForStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in [0, 1, 2]:\n" +
                        "    x += 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "some_collection = [0, 1, 2]\n" +
                    "for x in some_collection:\n" +
                        "    x += 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in range(0, 3):\n" +
                        "    x += 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in range(3):\n" +
                        "    x += 1\n"
        );
    }

    @Test
    public void printWhileStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    i += 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    break\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    print(i)\n" +
                        "    if i == 3:\n" +
                        "        break\n" +
                        "    i += 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    i += 1\n" +
                        "    if i == 3:\n" +
                        "        continue\n" +
                        "    print(i)\n"
        );
    }

    @Test
    public void printFunctionDeclaration() {
        parsePrintAndAssertEqualityOfOutputCode(
                "def function_name(x):\n" +
                        "    print(x)\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "def function_name(x, y, z):\n" +
                        "    print(x, y, z)\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "def absolute_value(num):\n" +
                        "    if num >= 0:\n" +
                        "        return num\n"
        );

        parsePrintAndAssertEqualityOfOutputCode(
                "def absolute_value():\n" +
                        "    return\n"
        );
    }

    @Test
    public void printAssertStatements(){
        parsePrintAndAssertEqualityOfOutputCode(
            "assert 2 > 3, \"Two is not greater than three\"\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
            "x = 1\n" +
                "assert x > 0, \"x is too low \"\n"
        );
    }

    @Test
    public void printLambdaStatements() {
        parsePrintAndAssertEqualityOfOutputCode(
            "z = lambda: 1\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
            "z = lambda x: x\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
            "z = lambda x, y: x + y\n"
        );
    }

    @Test
    public void printTryExceptStatements() {
        parsePrintAndAssertEqualityOfOutputCode(
            "try:\n" +
                "    i = 1 // 0\n" +
                "except ZeroDivisionError:\n" +
                "    print(\"Can not divide by zero\")\n"
        );

        parsePrintAndAssertEqualityOfOutputCode(
            "try:\n" +
                "    i = 1 // 0\n" +
                "except ZeroDivisionError:\n" +
                "    print(\"Can not divide by zero\")\n" +
                "finally:\n" +
                "    print(\"Done\")\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
            "try:\n" +
                "    i = 1 // 0\n" +
                "except ZeroDivisionError:\n" +
                "    print(\"Can not divide by zero\")\n" +
                "else:\n" +
                "    print(\"Success\")\n"
        );
        parsePrintAndAssertEqualityOfOutputCode(
            "try:\n" +
                "    i = 1 // 0\n" +
                "except ZeroDivisionError:\n" +
                "    print(\"Can not divide by zero\")\n" +
                "else:\n" +
                "    print(\"Success\")\n" +
                "finally:\n" +
                "    print(\"Done\")\n"
        );
    }

    // --------------------------------------------------------------
    //  Test cases for PythonPrettyPrinter
    // --------------------------------------------------------------

    @Test
    @Ignore
    public void printSIUnitConversion() {
        parsePrintAndExpect("var = km/h(5 dm/h)\n","var = 5 * ureg('km/h')\n");
    }

    /**
     * This method parses the given inputCodeString, prints the parsed ast tree as string and asserts that the result is
     * equal to inputCodeString.
     * Note: The static import statement for the pint library is added to the output code.
     */
    private void parsePrintAndAssertEqualityOfOutputCode(String inputCodeString) {
        parsePrintAndExpect(inputCodeString, inputCodeString);
    }

    private void parsePrintAndExpect(String inputCodeString, String expectedOutputCodeString) {
        String printedPythonCode = parseAndReturnPrintedPythonCode(inputCodeString);
        assertEquals(
                addPintLibImportCodeLines(expectedOutputCodeString),
                printedPythonCode);
    }

    private String parseAndReturnPrintedPythonCode(String siPythonCodeString) {
        Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromStringAndReturnASTPythonScript(siPythonCodeString);
        if(astPythonScriptOptional.isEmpty()) {
            fail("Failed to parse the given siPythonCodeString: " + siPythonCodeString);
        }
        return PrintAsSIPythonScript.printAsSIPythonScript(astPythonScriptOptional.get());
    }

    private static String addPintLibImportCodeLines(String codeString) {
        return "from pint import UnitRegistry\n" +
                "ureg = UnitRegistry()\n" +
                codeString;
    }
}
