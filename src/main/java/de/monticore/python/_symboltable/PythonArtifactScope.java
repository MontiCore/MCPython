package de.monticore.python._symboltable;

public class PythonArtifactScope extends PythonArtifactScopeTOP {
    public String getName(){
      if(isPresentAstNode()
          && getAstNode().isPresent_SourcePositionStart()
          && getAstNode().get_SourcePositionStart().getFileName().isPresent()
      ){
        return getAstNode().get_SourcePositionStart().getFileName().get();
      }else{
        return super.getName().replaceAll("[.]py$","");
      }
    }
}