package de.monticore.sipython.generator.prettyprint;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteralsNode;
import de.monticore.testsiunitliterals._parser.TestSIUnitLiteralsParser;
import org.junit.Test;


import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class MySIUnitLiteralsPrettyPrinterTest {
    @Test
    public void printLiteralWithDivisionTest() {
        parsePrintAndAssert("3 m/h");
    }

    @Test
    public void printLiteralWithExponentTest() {
        parsePrintAndAssert("4 m/ns^2");
    }

    @Test
    public void printLiteralWithKomplexExponentTest() {
        parsePrintAndAssert("4 mm^2/kVA^2h");
    }

    @Test
    public void printLiteralsTest() {
        parsePrintAndAssert("30.4 s^3/kgm^2");
        parsePrintAndAssert("30 kg");
        parsePrintAndAssert("30 km");
        parsePrintAndAssert("30 g");
        parsePrintAndAssert("30 kg");
        parsePrintAndAssert("30.4 kg");
        parsePrintAndAssert("30.4 kg");
        parsePrintAndAssert("30 kg/m");
        parsePrintAndAssert("30.4 kg^2");
        parsePrintAndAssert("30 g^2");
        parsePrintAndAssert("30 km^2");
        parsePrintAndAssert("30.4 kg^2");
        parsePrintAndAssert("30.4 s^3/kgm^2");
        parsePrintAndAssert("1 h/min");
        parsePrintAndAssert("30.4 rad");
        parsePrintAndAssert("5 °C");
        parsePrintAndAssert("5 °F");
    }

    /**
     * Parses the given string to ast node, prints the ast node and asserts if the print and the input string are equal.
     * @param sitUnitLiteralString string to parsed and printed
     */
    private static void parsePrintAndAssert(String sitUnitLiteralString) {
        String[] splitString = sitUnitLiteralString.split(" ");
        String literal = splitString[0];
        String unit = splitString[1];
        String expectedPrintString = "(" + literal + ", " + unit + ")";
        ASTSIUnitLiteralsNode astsiUnitLiteralsNode = parseSIUnitLiteralsNode(sitUnitLiteralString);
        String printerResultSkript = MySIUnitLiteralsPrettyPrinter.prettyprint(astsiUnitLiteralsNode);
        assertEquals(expectedPrintString, printerResultSkript);
    }

    private static ASTSIUnitLiteralsNode parseSIUnitLiteralsNode(String siUnitLiteralString) {
        TestSIUnitLiteralsParser parser = new TestSIUnitLiteralsParser();
        Optional<ASTLiteral> res = Optional.empty();
        try {
            res = parser.parseLiteral(new StringReader(siUnitLiteralString));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertTrue(res.isPresent());
        return (ASTSIUnitLiteralsNode) res.get();
    }
}
