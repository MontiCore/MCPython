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

    @Test
    public void test() {
       String printedPythonCode = parseAndReturnPrintedPythonCode("var = 5 km/h");
       assertEquals(
               addPintLibImportCodeLines("var = 5 * ureg('km/h')\n"),
               printedPythonCode);
    }

    private String parseAndReturnPrintedPythonCode(String siPythonCodeString) {
        Optional<ASTPythonScript> astPythonScriptOptional = parseModelFromStringAndReturnASTPythonScript("var = 5 km/h");
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
