package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;

public class SIPythonExpressionsBasisPrettyPrinter extends ExpressionsBasisPrettyPrinter {

	public SIPythonExpressionsBasisPrettyPrinter(IndentPrinter printer) {
		super(printer);
	}

	@Override
	public void handle(ASTNameExpression node) {
		CommentPrettyPrinter.printPreComments(node, getPrinter());
		if (node.getDefiningSymbol().isPresent()) {
			ISymbol symbol = node.getDefiningSymbol().get();
			if (symbol instanceof VariableSymbol) {
				VariableSymbol varSymbol = ((VariableSymbol) symbol);

				getPrinter().print(node.getName());

				if (varSymbol.getType() instanceof SymTypeOfNumericWithSIUnit) {
					getPrinter().print("[0]");
				}
			}
		} else {
			getPrinter().print(node.getName());
		}

		CommentPrettyPrinter.printPostComments(node, getPrinter());
	}

	@Override
	public void handle(ASTLiteralExpression node) {
		node.getLiteral().accept(getTraverser());
	}

}
