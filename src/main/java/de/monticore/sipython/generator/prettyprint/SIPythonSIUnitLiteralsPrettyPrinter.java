/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals.SIUnitLiteralsMill;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteralsNode;
import de.monticore.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunitliterals._visitor.SIUnitLiteralsTraverser;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits._ast.ASTSIUnitKindGroupWithExponent;
import de.monticore.siunits._ast.ASTSIUnitPrimitive;
import de.monticore.siunits._ast.ASTSIUnitWithPrefix;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;

public class SIPythonSIUnitLiteralsPrettyPrinter extends SIUnitLiteralsPrettyPrinter {

    public SIPythonSIUnitLiteralsPrettyPrinter(IndentPrinter printer) {
        super(printer);
    }

    @Override
    public void traverse(ASTSIUnitLiteral node) {
        node.getNumericLiteral().accept(getTraverser());
    }


    @Override
    public void traverse(ASTSignedSIUnitLiteral node) {
        printer.print(SIUnitLiteralDecoder.doubleOf(node));
    }

    /**
     * Only used for test.
     */
    public static String prettyprint(ASTSIUnitLiteralsNode node) {
        SIUnitLiteralsTraverser traverser = SIUnitLiteralsMill.traverser();

        IndentPrinter printer = new IndentPrinter();
        SIUnitsPrettyPrinter siUnitsPrettyPrinter = new SIUnitsPrettyPrinter(printer);
        SIPythonSIUnitLiteralsPrettyPrinter SIPythonSIUnitLiteralsPrettyPrinter = new SIPythonSIUnitLiteralsPrettyPrinter(printer);
        MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);

        traverser.setSIUnitsHandler(siUnitsPrettyPrinter);
        traverser.add4SIUnits(siUnitsPrettyPrinter);
        traverser.setSIUnitLiteralsHandler(SIPythonSIUnitLiteralsPrettyPrinter);
        traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);

        node.accept(traverser);
        return printer.getContent();
    }
}
