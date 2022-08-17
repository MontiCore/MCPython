package de.monticore.python._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanAndOpExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanNotExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTBooleanOrOpExpressionCoCo;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTLogicalNotExpressionCoCo;
import de.se_rwth.commons.logging.Log;

public class JavaBooleanExpressionCoco implements CommonExpressionsASTBooleanOrOpExpressionCoCo,
		CommonExpressionsASTBooleanAndOpExpressionCoCo, CommonExpressionsASTBooleanNotExpressionCoCo,
		CommonExpressionsASTLogicalNotExpressionCoCo {

	@Override
	public void check(ASTBooleanAndOpExpression node) {
		Log.error("Invalid boolean expression with '&&' " + node.get_SourcePositionStart());
	}

	@Override
	public void check(ASTBooleanNotExpression node) {
		Log.error("Invalid boolean expression with '!' " + node.get_SourcePositionStart());
	}

	@Override
	public void check(ASTLogicalNotExpression node) {
		Log.error("Invalid boolean expression with '!' " + node.get_SourcePositionStart());
	}

	@Override
	public void check(ASTBooleanOrOpExpression node) {
		Log.error("Invalid boolean expression with '||' " + node.get_SourcePositionStart());
	}
}
