/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sipython.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeOfNull;

public class DeriveSymTypeOfSIPythonExpression extends DeriveSymTypeOfExpression {

	@Override
	protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr){
		Optional<VariableSymbol> optVar = getScope(expr.getEnclosingScope()).resolveVariable(expr.getName());
		//Optional<TypeVarSymbol> optTypeVar = getScope(expr.getEnclosingScope()).resolveTypeVar(expr.getName());
		//Optional<TypeSymbol> optType = getScope(expr.getEnclosingScope()).resolveType(expr.getName());
		if("null".equals(expr.getName())){
			SymTypeExpression res = createTypeOfNull();
			expr.setDefiningSymbol(res.getTypeInfo());
			return Optional.of(res);
		}else if (optVar.isPresent()) {
			VariableSymbol var = optVar.get();
			expr.setDefiningSymbol(var);
			SymTypeExpression res = var.getType().deepClone();
			getTypeCheckResult().setField();
			return Optional.of(res);
		}
		return Optional.empty();
	}

}
