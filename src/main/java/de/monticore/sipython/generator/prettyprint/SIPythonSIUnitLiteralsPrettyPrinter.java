/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.generator.prettyprint;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits._ast.ASTSIUnitKindGroupWithExponent;
import de.monticore.siunits._ast.ASTSIUnitPrimitive;
import de.monticore.siunits._ast.ASTSIUnitWithPrefix;

public class SIPythonSIUnitLiteralsPrettyPrinter extends SIUnitLiteralsPrettyPrinter {

    public SIPythonSIUnitLiteralsPrettyPrinter(IndentPrinter printer) {
        super(printer);
    }

    @Override
    public void traverse(ASTSIUnitLiteral node) {
        traverse(node.getNumericLiteral());
        printer.print(" * ureg('");
        if(node.getSIUnit().isPresentNumerator()) {
            traverse(node.getSIUnit().getNumerator());
        }
        if(node.getSIUnit().isPresentDenominator()) {
            printer.print("/");
            traverse(node.getSIUnit().getDenominator());
        }
        if(node.getSIUnit().isPresentSIUnitPrimitive()) {
            traverse(node.getSIUnit().getSIUnitPrimitive());
        }
        printer.print("')");
    }

    private void traverse(ASTNumericLiteral node) {
        if(node instanceof ASTNatLiteral) {
            printer.print(((ASTNatLiteral)node).getDigits());
        }
        if(node instanceof ASTBasicDoubleLiteral) {
            printer.print(((ASTBasicDoubleLiteral)node).getValue());
        }
    }

    private void traverse(ASTSIUnitPrimitive node) {
        if(node.isPresentSIUnitKindGroupWithExponent()) {
            traverse(node.getSIUnitKindGroupWithExponent());
        }
        if(node.isPresentSIUnitWithPrefix()) {
            traverse(node.getSIUnitWithPrefix());
        }
        if(node.isPresentSIUnitDimensionless()) {
            printer.print(node.getSIUnitDimensionless().getUnit());
        }
        if(node.isPresentCelsiusFahrenheit()) {
            printer.print("°");
            printer.print(node.getCelsiusFahrenheit().getUnit());
        }
    }

    private void traverse(ASTSIUnitWithPrefix node) {
        if(node.isPresentName()) {
            printer.print(node.getName());
        }
        if(node.isPresentNonNameUnit()) {
            printer.print(node.getNonNameUnit());
        }
    }

    private void traverse(ASTSIUnitKindGroupWithExponent node) {
        printer.print(node.getSIUnitGroupPrimitive(0).getSIUnitWithPrefix().getName());
        printer.print("^");
        printer.print(node.getExponent(0).getDigits());
        for(int i = 1; i < node.getSIUnitGroupPrimitiveList().size(); i++) {
            printer.print(node.getSIUnitGroupPrimitive(i).getSIUnitWithPrefix().getName());
        }
    }

    @Override
    public void traverse(ASTSignedSIUnitLiteral node) {
        printer.print(SIUnitLiteralDecoder.doubleOf(node));
    }

}
