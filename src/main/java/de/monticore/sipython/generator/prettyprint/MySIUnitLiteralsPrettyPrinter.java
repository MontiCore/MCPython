/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals.SIUnitLiteralsMill;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteralsNode;
import de.monticore.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunitliterals._visitor.SIUnitLiteralsTraverser;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;

public class MySIUnitLiteralsPrettyPrinter extends SIUnitLiteralsPrettyPrinter {

    public MySIUnitLiteralsPrettyPrinter(IndentPrinter printer) {
        super(printer);
    }

    @Override
    public void traverse(ASTSIUnitLiteral node) {
        printer.print("(");
        printer.print(((ASTNatLiteral)node.getNumericLiteral()).getDigits() + ", ");
        if(node.getSIUnit().isPresentNumerator()) {
            if(node.getSIUnit().getNumerator().isPresentSIUnitWithPrefix()) {
                printer.print(node.getSIUnit().getNumerator().getSIUnitWithPrefix().getName());
            }
        }
        if(node.getSIUnit().isPresentDenominator()) {
            if(node.getSIUnit().getDenominator().isPresentSIUnitWithPrefix()) {
                printer.print("/");
                printer.print(node.getSIUnit().getDenominator().getSIUnitWithPrefix().getName());
            }
        }
        printer.print(")");
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
        MySIUnitLiteralsPrettyPrinter mySIUnitLiteralsPrettyPrinter = new MySIUnitLiteralsPrettyPrinter(printer);
        MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);

        traverser.setSIUnitsHandler(siUnitsPrettyPrinter);
        traverser.add4SIUnits(siUnitsPrettyPrinter);
        traverser.setSIUnitLiteralsHandler(mySIUnitLiteralsPrettyPrinter);
        traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);

        node.accept(traverser);
        return printer.getContent();
    }
}
