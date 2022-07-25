package de.monticore.sipython._symboltable;

public class SIPythonScopesGenitor extends SIPythonScopesGenitorTOP {

	public SIPythonScopesGenitor() {
		super();
	}

	@Override
	public void initArtifactScopeHP1(ISIPythonArtifactScope scope) {
		scope.setName(scope.getAstNode().get_SourcePositionStart().getFileName().get());
	}

}
