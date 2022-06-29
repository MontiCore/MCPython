package de.monticore.sipython.generator;

import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.python._ast.ASTPythonScript;
import de.monticore.sipython.SIPythonMill;
import de.monticore.sipython._visitor.SIPythonTraverser;
import de.monticore.sipython.generator.prettyprint.*;

public class PrintAsSIPythonScript {

    private SIPythonTraverser traverser;

    public PrintAsSIPythonScript(IndentPrinter indentPrinter) {
        traverser = SIPythonMill.traverser();

        SIPythonPrettyPrinter siPythonPrettyPrinter = new SIPythonPrettyPrinter(indentPrinter);
        traverser.add4SIPython(siPythonPrettyPrinter);
        traverser.setSIPythonHandler(siPythonPrettyPrinter);

        PythonPrettyPrinter pythonPrettyPrinter = new PythonPrettyPrinter(indentPrinter);
        traverser.add4Python(pythonPrettyPrinter);
        traverser.setPythonHandler(pythonPrettyPrinter);

        SIPythonCommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new SIPythonCommonExpressionsPrettyPrinter(indentPrinter);
        traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
        traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);

        SIPythonAssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new SIPythonAssignmentExpressionsPrettyPrinter(indentPrinter);
        traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
        traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);

        SIPythonExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new SIPythonExpressionsBasisPrettyPrinter(indentPrinter);
        traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
        traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);

        MySIUnitLiteralsPrettyPrinter siUnitLiteralsPrettyPrinter = new MySIUnitLiteralsPrettyPrinter(indentPrinter);
        traverser.setSIUnitLiteralsHandler(siUnitLiteralsPrettyPrinter);

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
