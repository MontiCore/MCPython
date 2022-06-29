package de.monticore.sipythonunits;

import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython._cocos.SIPythonCoCoChecker;
import de.monticore.sipython._cocos.SIPythonCommonExpressionTypeCheckCoco;
import de.monticore.sipython._cocos.SIPythonSIUnitConversionTypeCheckCoco;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class SyntaxTest extends AbstractTest {

    @Before
    public void init() {
        Log.init();
        Log.enableFailQuick(false);
        SIPythonMill.reset();
        SIPythonMill.init();
        SIUnitsMill.initializeSIUnits();
        SIPythonMill.globalScope().add(SIPythonMill.typeSymbolBuilder()
                .setName("si")
                .setEnclosingScope(SIPythonMill.globalScope())
                .setSpannedScope(SIPythonMill.scope())
                .build()
        );
    }

    private void typeCheckCoCo(String input, boolean expectedError) {
        Log.getFindings().clear();
        ASTPythonScript model = parseModel(input);
        SIPythonMill.scopesGenitorDelegator().createFromAST(model);
        SIPythonCommonExpressionTypeCheckCoco checker = SIPythonCommonExpressionTypeCheckCoco.getCoCo();

        // checker.addCoCo(SIPythonCommonExpressionTypeCheckCoco.getCoCo());

        try {
            checker.checkAll(model);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(expectedError, Log.getErrorCount() == 0);
    }

    @Test
    public void parseErrorFile() {
        String model = "tests/textSyntaxErrors.sipy";
        typeCheckCoCo(model, false);
    }

    @Test
    public void parseNoErrorFile() {
        String model = "tests/textSyntaxNOErrors.sipy";
        typeCheckCoCo(model, true);
    }

    @Test
    public void parseMain() {
        String model = "tests/main.sipy";
        typeCheckCoCo(model, true);
    }
    @Test
    public void parseFuntions() {
        String model = "tests/funct.sipy";
        typeCheckCoCo(model, true);
    }

    @Test
    public void parsePrints() {
        String model = "tests/prints.sipy";
        typeCheckCoCo(model, true);
    }

    @Test
    public void myMethod() {
        String model = "tests/method.sipy";
        typeCheckCoCo(model, true);
    }

}
