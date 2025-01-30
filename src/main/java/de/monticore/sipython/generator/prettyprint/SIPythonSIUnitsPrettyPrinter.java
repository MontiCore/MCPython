package de.monticore.sipython.generator.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunits._ast.ASTSIUnitKindGroupWithExponent;
import de.monticore.siunit.siunits._ast.ASTSIUnitPrimitive;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithPrefix;
import de.monticore.siunit.siunits._prettyprint.SIUnitsPrettyPrinter;

public class SIPythonSIUnitsPrettyPrinter extends SIUnitsPrettyPrinter {

	public SIPythonSIUnitsPrettyPrinter(IndentPrinter printer) {
		super(printer, true);
	}

	@Override
	public void traverse(ASTSIUnitPrimitive node) {
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

	@Override
	public void traverse(ASTSIUnitWithPrefix node) {
		if(node.isPresentName()) {
			printer.print(node.getName());
		}
		if(node.isPresentNonNameUnit()) {
			printer.print(node.getNonNameUnit());
		}
	}

	@Override
	public void traverse(ASTSIUnitKindGroupWithExponent node) {
		printer.print(node.getSIUnitGroupPrimitive(0).getSIUnitWithPrefix().getName());
		printer.print("^");
		printer.print(node.getExponent(0).getDigits());
		for(int i = 1; i < node.getSIUnitGroupPrimitiveList().size(); i++) {
			printer.print(node.getSIUnitGroupPrimitive(i).getSIUnitWithPrefix().getName());
		}
	}
}
