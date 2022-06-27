package de.monticore.sipython;

import de.monticore.sipython._ast.ASTSIPythonScript;
import de.monticore.sipython._cocos.SIPythonCoCoChecker;
import de.monticore.sipython._cocos.SIPythonTypeCheckCoco;

public class SIPythonTool extends SIPythonToolTOP {

	@Override
	public void runDefaultCoCos(ASTSIPythonScript ast) {
		SIPythonCoCoChecker checker = new SIPythonCoCoChecker();

		checker.addCoCo(SIPythonTypeCheckCoco.getCoCo());

		checker.checkAll(ast);
	}
}
