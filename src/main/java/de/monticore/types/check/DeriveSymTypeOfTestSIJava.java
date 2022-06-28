/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._visitor.SIPythonTraverser;

public class DeriveSymTypeOfTestSIJava implements IDerive {

    private SIPythonTraverser traverser;

    public SIPythonTraverser getTraverser() {
        return traverser;
    }

    private DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes deriveSymTypeOfAssignmentExpressions;

    private DeriveSymTypeOfCommonExpressionsWithSIUnitTypes deriveSymTypeOfCommonExpressions;

    private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

    private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

    private DeriveSymTypeOfSIUnitLiterals deriveSymTypeOfSIUnitLiterals;

    private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

    private TypeCheckResult typeCheckResult = new TypeCheckResult();


    public DeriveSymTypeOfTestSIJava() {
        traverser = SIPythonMill.traverser();
        init();
    }


    /**
     * set the last typeCheckResult of all calculators to the same object
     */
    public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
        deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
        deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
        deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
        deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
        deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
        deriveSymTypeOfSIUnitLiterals.setTypeCheckResult(typeCheckResult);
    }

    /**
     * initialize the typescalculator
     */
    public void init() {
        this.traverser = SIPythonMill.traverser();

        deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressionsWithSIUnitTypes();
        traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
        traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);

        deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes();
        traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
        traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);

        deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
        traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
        traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);

        deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
        traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

        deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
        traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);

        deriveSymTypeOfSIUnitLiterals = new DeriveSymTypeOfSIUnitLiterals();
        traverser.setSIUnitLiteralsHandler(deriveSymTypeOfSIUnitLiterals);

        setTypeCheckResult(typeCheckResult);
    }

    @Override
    public TypeCheckResult deriveType(ASTExpression expr) {
        typeCheckResult.reset();
        expr.accept(getTraverser());
        return typeCheckResult;
    }

    @Override
    public TypeCheckResult deriveType(ASTLiteral lit) {
        typeCheckResult.reset();
        lit.accept(getTraverser());
        return typeCheckResult;
    }
}
