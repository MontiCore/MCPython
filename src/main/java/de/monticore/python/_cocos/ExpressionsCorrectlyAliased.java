package de.monticore.python._cocos;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python.PythonMill;
import de.se_rwth.commons.logging.Log;

public class ExpressionsCorrectlyAliased implements PythonASTPythonScriptCoCo {
    CaseStatementVisitor visitor = new CaseStatementVisitor();

    @Override
    public void check(de.monticore.python._ast.ASTPythonScript node){
        CaseStatementVisitor visitor = new CaseStatementVisitor();
        de.monticore.python._visitor.PythonTraverser traverser = PythonMill.traverser();
        traverser.add4Python(visitor);
        node.accept(traverser);
        if(visitor.violationsFound()){
            Log.error("In "+visitor.getNumberOfViolations()+" cases an alias was used in an unintended way, in example:  var1 = var2 as var 3 ");
        }
    }
}