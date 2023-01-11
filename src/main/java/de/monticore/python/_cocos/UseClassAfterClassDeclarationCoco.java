package de.monticore.python._cocos;


import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.python._symboltable.PythonClassSymbol;
import de.monticore.python._symboltable.IPythonScope;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;


import java.util.Optional;

public class UseClassAfterClassDeclarationCoco implements CommonExpressionsASTCallExpressionCoCo {


    @Override
    public void check(ASTCallExpression node) {
        Optional<PythonClassSymbol> optionalPythonClassSymbol = getPythonClassSymbol(node);

        if (optionalPythonClassSymbol.isPresent()) {
            PythonClassSymbol symbol = optionalPythonClassSymbol.get();

            SourcePosition symbolPosition = symbol.getAstNode().get_SourcePositionStart();
            SourcePosition nodePosition = node.get_SourcePositionStart();

            if (symbolPosition.getFileName().isPresent() && nodePosition.getFileName().isPresent()){
                if (!symbolPosition.getFileName().get().equals(nodePosition.getFileName().get())) {
                    return;
                }
            }

            if (symbolPosition.compareTo(nodePosition) >= 0) {
                Log.error("Use of class '" + symbol.getName() + "' before declaration " + nodePosition);
            }
        }
    }

    private Optional<PythonClassSymbol> getPythonClassSymbol(ASTCallExpression node) {
        if(node.getExpression() instanceof ASTNameExpression){
            return ((IPythonScope) node.getEnclosingScope()).resolvePythonClass(((ASTNameExpression) node.getExpression()).getName());
        }
        return Optional.empty();
    }
}
