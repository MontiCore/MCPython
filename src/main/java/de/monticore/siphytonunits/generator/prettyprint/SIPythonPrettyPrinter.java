/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siphytonunits.generator.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.ASTExpressionStatement;
import de.monticore.python._ast.ASTForStatement;
import de.monticore.python._ast.ASTFunctionCall;
import de.monticore.python._ast.ASTFunctionDeclaration;
import de.monticore.python._ast.ASTIfStatement;
import de.monticore.python._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.python._ast.ASTReturnStatement;
import de.monticore.python._ast.ASTStatement;
import de.monticore.python._ast.ASTWhileStatement;
import de.monticore.python._visitor.PythonHandler;
import de.monticore.python._visitor.PythonTraverser;
import de.monticore.python._visitor.PythonVisitor2;
import de.monticore.sipython._visitor.SIPythonHandler;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython._visitor.SIPythonVisitor2;
import javax.measure.converter.UnitConverter;

public class SIPythonPrettyPrinter implements SIPythonHandler, SIPythonVisitor2, PythonHandler, PythonVisitor2 {

    protected SIPythonTraverser siPythonTraverser;
    protected PythonTraverser pythonTraverser;

    @Override
    public SIPythonTraverser getTraverser() {
        return siPythonTraverser;
    }

    @Override
    public void setTraverser(SIPythonTraverser siPythonTraverser) {
        this.siPythonTraverser = siPythonTraverser;
    }

    @Override
    public void setTraverser(PythonTraverser traverser) {
        this.pythonTraverser = traverser;
    }

    IndentPrinter printer;

    public SIPythonPrettyPrinter(IndentPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void traverse(ASTPythonScript node) {
        CommentPrettyPrinter.printPreComments(node, printer);

        for( ASTStatement astStatement: node.getStatementList()) {
            if(astStatement instanceof ASTLocalVariableDeclarationStatement) {
                printer.println("ASTLocalVariableDeclarationStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTIfStatement) {
                printer.println("ASTIfStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTForStatement) {
                printer.println("ASTForStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTWhileStatement) {
                printer.println("ASTWhileStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTFunctionDeclaration) {
                printer.println("ASTFunctionDeclaration detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTReturnStatement) {
                printer.println("ASTReturnStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTFunctionCall) {
                printer.println("ASTFunctionCall detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            } else if(astStatement instanceof ASTExpressionStatement) {
                printer.println("ASTExpressionStatement detected");
                astStatement.accept(getTraverser());
                printer.println(";");
            }
        }
        CommentPrettyPrinter.printPostComments(node, printer);
    }

    public static String factorStartSimple(UnitConverter converter) {
        if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0)
            return "(";
        else return "";
    }

    public static String factorEndSimple(UnitConverter converter) {
        if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0) {
            String factor;
            if (converter.convert(1) > 1)
                factor = " * " + converter.convert(1);
            else
                factor = " / " + converter.inverse().convert(1);
            return ")" + factor;
        } else
            return "";
    }
}
