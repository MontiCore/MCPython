package de.monticore.sipython;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.generator.PrintAsSIPythonScript;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PrintAsSIPythonScriptTest extends AbstractTest{

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
        parsePrintAndAssertEqualityOfOutputCode("from some_module import some_package");
    }

    @Test
    public void printLocalVariableDeclaration() {
        parsePrintAndAssertEqualityOfOutputCode("var = 5");
        parsePrintAndAssertEqualityOfOutputCode("var = [5, 6, 7]");
    }

    @Test
    public void printIfStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "if x == 1:\n" +
                "    print(\"one\")"
        );
    }

    @Test
    public void printForStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in [0, 1, 2]:\n" +
                        "    x++"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in some_collection:\n" +
                        "    x++"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in range(0, 3):\n" +
                        "    x++"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "for x in range(3):\n" +
                        "    x++"
        );
    }

    @Test
    public void printWhileStatement() {
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    i += 1"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    break"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    print(i)\n" +
                        "    if i == 3:\n" +
                        "        break\n" +
                        "    i += 1"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "while i < 6:\n" +
                        "    i += 1\n" +
                        "    if i == 3:\n" +
                        "        continue\n" +
                        "    print(i)"
        );
    }

    @Test
    public void printFunctionDeclaration() {
        parsePrintAndAssertEqualityOfOutputCode(
                "def function_name(x):\n" +
                        "    print(x)"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "def function_name(x, y, z):\n" +
                        "    print(x, y, z)"
        );
        parsePrintAndAssertEqualityOfOutputCode(
                "def absolute_value(num):\n" +
                        "    if num >= 0:\n" +
                        "        return num"
        );

        parsePrintAndAssertEqualityOfOutputCode(
                "def absolute_value():\n" +
                        "    return "
        );
    }

    // --------------------------------------------------------------
    //  Test cases for PythonPrettyPrinter
    // --------------------------------------------------------------

    @Test
    public void printSIUnitConversion() {
        parsePrintAndExpect("var = km/h(5 dm/h)","var = 5 * ureg('km/h')");
    }

    /**
     * This method parses the given inputCodeString, prints the parsed ast tree as string and asserts that the result is
     * equal to inputCodeString.
     * Note: The static import statment for the pint library is added to the output code.
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
                codeString + "\n";
    }
}
