package de.monticore.sipython.generator;

import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.generator.prettyprint.MyAssignmentExpressionsPrettyPrinter;
import de.monticore.sipython.generator.prettyprint.SIPythonPrettyPrinter;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._visitor.SIPythonTraverser;

public class PrintAsSIPythonScript {

    private SIPythonTraverser traverser;

    public PrintAsSIPythonScript(IndentPrinter indentPrinter) {
        traverser = SIPythonMill.traverser();

        MyAssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new MyAssignmentExpressionsPrettyPrinter(indentPrinter);
        traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
        traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);

        SIPythonPrettyPrinter siPythonPrettyPrinter = new SIPythonPrettyPrinter(indentPrinter);
        traverser.add4SIPython(siPythonPrettyPrinter);
        traverser.add4Python(siPythonPrettyPrinter);
        traverser.setSIPythonHandler(siPythonPrettyPrinter);
        traverser.setPythonHandler(siPythonPrettyPrinter);

//        MyCommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new MyCommonExpressionsPrettyPrinter(indentPrinter);
//        traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
//        traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
//
//        MyExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new MyExpressionsBasisPrettyPrinter(indentPrinter);
//        traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
//        traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);

//        TestSIJavaPrettyPrinter testSIJavaPrettyPrinter = new TestSIJavaPrettyPrinter(indentPrinter);
//        traverser.add4TestSIJava(testSIJavaPrettyPrinter);
//        traverser.setTestSIJavaHandler(testSIJavaPrettyPrinter);
//
//        MySIUnitLiteralsPrettyPrinter siUnitLiteralsPrettyPrinter = new MySIUnitLiteralsPrettyPrinter(indentPrinter);
//        traverser.setSIUnitLiteralsHandler(siUnitLiteralsPrettyPrinter);

        MCCommonLiteralsPrettyPrinter mcCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(indentPrinter);
        traverser.add4MCCommonLiterals(mcCommonLiteralsPrettyPrinter);
        traverser.setMCCommonLiteralsHandler(mcCommonLiteralsPrettyPrinter);
    }

    public static String printAsSIPythonScript(ASTPythonScript astPythonScript) {
        IndentPrinter printer = new IndentPrinter();
        printer.setIndentLength(4);
        PrintAsSIPythonScript printAsSIPythonScript = new PrintAsSIPythonScript(printer);
        astPythonScript.accept(printAsSIPythonScript.traverser);
        return printer.getContent();
    }
}
