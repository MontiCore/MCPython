package de.monticore.python._cocos;

import de.monticore.python._ast.*;
import de.monticore.python._visitor.PythonVisitor2;

public class CaseStatementVisitor implements PythonVisitor2 {
    private boolean inCaseStatement = false;
    private int numberOfViolations = 0;

    @Override
    public void visit(de.monticore.python._ast.ASTCaseStatement node){
        this.inCaseStatement = true;
    }

    @Override
    public void endVisit(de.monticore.python._ast.ASTCaseStatement node){
        this.inCaseStatement = false;
    }

    @Override
    public void visit(de.monticore.python._ast.ASTAliasedExpression node){
        if(!inCaseStatement){
            numberOfViolations++;
        }
    }
    public int getNumberOfViolations (){
        return numberOfViolations;
    }
    public boolean violationsFound(){
        if(numberOfViolations>0) {
            return true;
        } else {
            return false;
        }
    }
}