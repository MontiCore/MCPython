/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sipythonunits;

import de.monticore.sipythonunits._ast.ASTSIPythonUnit;
import de.monticore.sipythonunits._parser.SIPythonUnitsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class SIPythonUnitTest {

    SIPythonUnitsParser parser = new SIPythonUnitsParser();

    @BeforeClass
    public static void init() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    private void checkSIPythonUnit(String s, String unitAsString, String baseUnitAsString) throws IOException {
        ASTSIPythonUnit lit = parseSIPythonUnit(s);
        //String printFromLit = UnitPrettyPrinter.printUnit(lit);
        //String printStandardFromLit = UnitPrettyPrinter.printBaseUnit(lit);
        //assertEquals(unitAsString, printFromLit);
        //assertEquals(baseUnitAsString, printStandardFromLit);
    }

    private ASTSIPythonUnit parseSIPythonUnit(String input) throws IOException {
        Optional<ASTSIPythonUnit> res = parser.parseSIPythonUnit(new StringReader(input));
        assertTrue(res.isPresent());
        return res.get();
    }

    private void checkInvalid(String input) throws IOException {
        Optional<ASTSIPythonUnit> res = parser.parseSIPythonUnit(new StringReader(input));
        assertFalse(res.isPresent());
    }

    @Test
    public void simpleTest() {
        try {
            parseSIPythonUnit("kmh");
            checkInvalid("khA sdf");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void testUnitGroup() {
        try {
            parseSIPythonUnit("kV^2A^3/m^2");
            parseSIPythonUnit("s^-1");
            parseSIPythonUnit("kVA");
            parseSIPythonUnit("kVAh");
            parseSIPythonUnit("kVAh/°C");
            parseSIPythonUnit("kV^2A^3h");

            checkInvalid("khA");
            checkInvalid("khA");
            checkInvalid("kvA");
            checkInvalid("s^0.1");
            checkInvalid("s^a");
            checkInvalid("kV°C");

            checkSIPythonUnit("s^-1","1/s","1/s");
            checkSIPythonUnit("kVA","A*kV","kg*m^2/s^3");
            checkSIPythonUnit("kV^2A^3","A^3*kV^2","A*kg^2*m^4/s^6");
            checkSIPythonUnit("kVA^3","A^3*kV","A^2*kg*m^2/s^3");
            checkSIPythonUnit("kVAh","A*h*kV","kg*m^2/s^2");
            checkSIPythonUnit("kVAh/°C","A*h*kV/°C","kg*m^2/(K*s^2)");
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void testSIUnit() {
        try {
            checkSIPythonUnit("°C", "°C", "K");
            checkSIPythonUnit("kg", "kg", "kg");
            checkSIPythonUnit("cd", "cd", "cd");
            checkSIPythonUnit("m", "m", "m");
            checkSIPythonUnit("s^2", "s^2", "s^2");
            checkSIPythonUnit("s^2/kg", "s^2/kg", "s^2/kg");
            checkSIPythonUnit("s^2/min", "s^2/min", "s");
            checkSIPythonUnit("kgs^2/minm", "kg*s^2/(m*min)", "kg*s/m");
            checkSIPythonUnit("deg", "deg", "1");
            checkSIPythonUnit("s^-1", "1/s", "1/s");
            checkSIPythonUnit("1/s", "1/s", "1/s");
            checkSIPythonUnit("°F", "°F", "K");
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    //@Test
    public void testOhmAndMu() throws IOException {
        checkSIPythonUnit("Ω", "Ω", "kg*m^2/(A^2*s^3)");
        checkSIPythonUnit("kΩ", "kΩ", "kg*m^2/(A^2*s^3)");
        checkSIPythonUnit("µg", "µg", "kg");
        checkSIPythonUnit("µΩ", "µΩ", "kg*m^2/(A^2*s^3)");

        checkInvalid("k Ω");
        checkInvalid("µ g");
        checkInvalid("µ Ω");
    }
}
