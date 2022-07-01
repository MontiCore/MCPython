package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sipython.types.check.DeriveSymTypeOfSIPython;
import de.monticore.siunits.utility.Converter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsArtifactScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import java.util.Optional;

public class SIPythonAssignmentExpressionsPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

	public SIPythonAssignmentExpressionsPrettyPrinter(IndentPrinter printer) {
		super(printer);
	}

	@Override
	public void handle(ASTAssignmentExpression node) {
		CommentPrettyPrinter.printPreComments(node, this.getPrinter());
		if (!(node.getLeft() instanceof ASTNameExpression)) {
			Log.error("0xE725687 Left side of AssignmentExpression is not a NameExpression");
		}

		String nameToResolve = ((ASTNameExpression) node.getLeft()).getName();
		IBasicSymbolsArtifactScope enclosingScope = (IBasicSymbolsArtifactScope) node.getEnclosingScope();
		Optional<VariableSymbol> variable = enclosingScope.resolveVariable(nameToResolve);
		Optional<SymTypeExpression> type = Optional.empty();
		if (variable.isPresent())
			type = Optional.of(variable.get().getType());

		this.getPrinter().print(((ASTNameExpression) node.getLeft()).getName());

		this.getPrinter().print(" ");
		switch (node.getOperator()) {
			case 1:
				this.getPrinter().print("&=");
				break;
			case 2:
				this.getPrinter().print("=");
				break;
			case 3:
				this.getPrinter().print(">>=");
				break;
			case 4:
				this.getPrinter().print(">>>=");
				break;
			case 5:
				this.getPrinter().print("<<=");
				break;
			case 6:
				this.getPrinter().print("-=");
				break;
			case 7:
				this.getPrinter().print("%=");
				break;
			case 8:
				this.getPrinter().print("|=");
				break;
			case 9:
				this.getPrinter().print("+=");
				break;
			case 10:
				this.getPrinter().print("^=");
				break;
			case 11:
				this.getPrinter().print("/=");
				break;
			case 12:
				this.getPrinter().print("*=");
				break;
			default:
				Log.error("0xA0114 Missing implementation for RegularAssignmentExpression");
		}

		TypeCalculator tc = new TypeCalculator(null, new DeriveSymTypeOfSIPython());

		UnitConverter converter = UnitConverter.IDENTITY;
		if (type.isPresent() && type.get() instanceof SymTypeOfNumericWithSIUnit) {
			Unit unit = ((SymTypeOfNumericWithSIUnit) type.get()).getUnit();
			SymTypeExpression rightType = tc.typeOf(node.getRight());
			if (rightType instanceof SymTypeOfNumericWithSIUnit)
				converter = Converter.getConverter(((SymTypeOfNumericWithSIUnit) rightType).getUnit(), unit);
		}

		this.getPrinter().print(SIPythonPrettyPrinter.factorStartSimple(converter));

		node.getRight().accept(this.getTraverser());

		this.getPrinter().print(SIPythonPrettyPrinter.factorEndSimple(converter));
		this.getPrinter().print("\"" + type.get().print() + "\"");

		CommentPrettyPrinter.printPostComments(node, this.getPrinter());
	}
}
