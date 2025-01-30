package de.monticore.sipython.generator.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class SIPythonAssignmentExpressionsPrettyPrinter extends AssignmentExpressionsPrettyPrinter {

	public SIPythonAssignmentExpressionsPrettyPrinter(IndentPrinter printer) {
		super(printer, true);
	}

	@Override
	public void handle(ASTAssignmentExpression node) {
		CommentPrettyPrinter.printPreComments(node, this.getPrinter());

		node.getLeft().accept(getTraverser());

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

		getPrinter().print(" ");

		node.getRight().accept(this.getTraverser());

		CommentPrettyPrinter.printPostComments(node, this.getPrinter());
	}
}
