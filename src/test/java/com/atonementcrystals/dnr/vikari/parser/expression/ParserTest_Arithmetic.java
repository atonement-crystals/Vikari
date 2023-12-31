package com.atonementcrystals.dnr.vikari.parser.expression;

import com.atonementcrystals.dnr.vikari.core.crystal.identifier.ReferenceCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.literal.BooleanCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.BinaryOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.BinaryExpression;
import com.atonementcrystals.dnr.vikari.core.expression.Expression;
import com.atonementcrystals.dnr.vikari.core.expression.VariableExpression;
import com.atonementcrystals.dnr.vikari.core.statement.Statement;
import com.atonementcrystals.dnr.vikari.core.statement.VariableDeclarationStatement;
import com.atonementcrystals.dnr.vikari.interpreter.Lexer;
import com.atonementcrystals.dnr.vikari.interpreter.Parser;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.number.IntegerCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.AddOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.LeftDivideOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.MultiplyOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.RightDivideOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.SubtractOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.GroupingExpression;
import com.atonementcrystals.dnr.vikari.core.expression.LiteralExpression;
import com.atonementcrystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonementcrystals.dnr.vikari.error.VikariError;
import com.atonementcrystals.dnr.vikari.error.SyntaxErrorReporter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static com.atonementcrystals.dnr.vikari.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParserTest_Arithmetic {

    @Test
    @Order(1)
    public void testParser_Expression_ArithmeticOperators_Add() {
        String sourceString = "2 + 7";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        IntegerCrystal rightNumber = (IntegerCrystal) rightOperand;

        assertEquals(2, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(7, rightNumber.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(2)
    public void testParser_Expression_ArithmeticOperators_Subtract() {
        String sourceString = "3 - 8";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(SubtractOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        IntegerCrystal rightNumber = (IntegerCrystal) rightOperand;

        assertEquals(3, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(8, rightNumber.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(3)
    public void testParser_Expression_ArithmeticOperators_Multiply() {
        String sourceString = "4 * 9";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(MultiplyOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        IntegerCrystal rightNumber = (IntegerCrystal) rightOperand;

        assertEquals(4, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(9, rightNumber.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(4)
    public void testParser_Expression_ArithmeticOperators_LeftDivide() {
        String sourceString = "5 / 10";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(LeftDivideOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        IntegerCrystal rightNumber = (IntegerCrystal) rightOperand;

        assertEquals(5, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(10, rightNumber.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(5)
    public void testParser_Expression_ArithmeticOperators_RightDivide() {
        String sourceString = "6 \\ 11";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(RightDivideOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        IntegerCrystal rightNumber = (IntegerCrystal) rightOperand;

        assertEquals(6, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(11, rightNumber.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(6)
    public void testParser_Expression_ArithmeticOperators_AndGrouping_SingleLine() {
        String sourceString = "2 + [7 - [22 / 3] * 8]";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // 1. first expression (add expression: "2 + [7 - [22 / 3] * 8]")
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left1 = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right1 = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left1.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(GroupingExpression.class, right1.getClass(), "Unexpected expression type.");

        // 2. first left operand (literal expression: "2")
        AtonementCrystal leftOperand = ((LiteralExpression) left1).getValue();
        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        assertEquals(2, leftNumber.getValue(), "Unexpected literal value.");

        // 3. first right operand (grouping expression: "[7 - [22 / 3] * 8]")
        GroupingExpression groupingExpression1 = (GroupingExpression) right1;

        // 4. inner expression (subtract expression: "7 - [22 / 3] * 8")
        Expression innerExpression1 = groupingExpression1.getExpression();
        assertEquals(BinaryExpression.class, innerExpression1.getClass(), "Unexpected expression type.");
        BinaryExpression binaryExpression2 = (BinaryExpression) innerExpression1;

        Expression left2 = binaryExpression2.getLeft();
        BinaryOperatorCrystal operator2 = binaryExpression2.getOperator();
        Expression right2 = binaryExpression2.getRight();

        assertEquals(LiteralExpression.class, left2.getClass(), "Unexpected expression type.");
        assertEquals(SubtractOperatorCrystal.class, operator2.getClass(), "Unexpected operator type.");
        assertEquals(BinaryExpression.class, right2.getClass(), "Unexpected expression type.");

        // 5. second left operand (literal expression: "7")
        AtonementCrystal leftOperand2 = ((LiteralExpression) left2).getValue();
        assertEquals(IntegerCrystal.class, leftOperand2.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber2 = (IntegerCrystal) leftOperand2;
        assertEquals(7, leftNumber2.getValue(), "Unexpected literal value.");

        // 7. second right operand (multiply expression: "[22 / 3] * 8")
        BinaryExpression binaryExpression3 = (BinaryExpression) right2;

        Expression left3 = binaryExpression3.getLeft();
        BinaryOperatorCrystal operator3 = binaryExpression3.getOperator();
        Expression right3 = binaryExpression3.getRight();

        assertEquals(GroupingExpression.class, left3.getClass(), "Unexpected expression type.");
        assertEquals(MultiplyOperatorCrystal.class, operator3.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right3.getClass(), "Unexpected expression type.");

        // 8. third left operand (grouping expression: "[22 / 3]")
        GroupingExpression groupingExpression2 = (GroupingExpression) left3;
        Expression innerExpression2 = groupingExpression2.getExpression();
        assertEquals(BinaryExpression.class, innerExpression2.getClass(), "Unexpected expression type.");

        // 9. third right operand (literal expression: "8")
        AtonementCrystal rightOperand3 = ((LiteralExpression) right3).getValue();
        assertEquals(IntegerCrystal.class, rightOperand3.getClass(), "Unexpected literal type.");

        IntegerCrystal rightNumber3 = (IntegerCrystal) rightOperand3;
        assertEquals(8, rightNumber3.getValue(), "Unexpected literal value.");

        // 10. inner expression (divide expression: "22 / 3")
        BinaryExpression binaryExpression4 = (BinaryExpression) innerExpression2;

        Expression left4 = binaryExpression4.getLeft();
        BinaryOperatorCrystal operator4 = binaryExpression4.getOperator();
        Expression right4 = binaryExpression4.getRight();

        assertEquals(LiteralExpression.class, left4.getClass(), "Unexpected expression type.");
        assertEquals(LeftDivideOperatorCrystal.class, operator4.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right4.getClass(), "Unexpected expression type.");

        // 11. fourth left operand (literal expression: "22")
        AtonementCrystal leftOperand4 = ((LiteralExpression) left4).getValue();
        assertEquals(IntegerCrystal.class, leftOperand4.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber4 = (IntegerCrystal) leftOperand4;
        assertEquals(22, leftNumber4.getValue(), "Unexpected literal value.");

        // 12. fourth right operand (literal expression: "3")
        AtonementCrystal rightOperand4 = ((LiteralExpression) right4).getValue();
        assertEquals(IntegerCrystal.class, rightOperand4.getClass(), "Unexpected literal type.");

        IntegerCrystal rightNumber4 = (IntegerCrystal) rightOperand4;
        assertEquals(3, rightNumber4.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(7)
    public void testParser_Expression_ArithmeticOperators_InvalidOperator() {
        String sourceString = "2 // 7";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        parser.parse(null, lexedStatements);

        assertTrue(syntaxErrorReporter.hasErrors(), "Expected a syntax error for invalid operator.");
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        assertEquals(1, syntaxErrors.size(), "Unexpected number of syntax errors.");

        testSyntaxError(syntaxErrors.get(0), location(0, 2), sourceString, "Expected expression.");
    }

    @Test
    @Order(8)
    public void testParser_Expression_ArithmeticOperators_NoOperands() {
        String sourceString = "+";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        parser.parse(null, lexedStatements);

        assertTrue(syntaxErrorReporter.hasErrors(), "Expected a syntax error for missing operands.");
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        assertEquals(1, syntaxErrors.size(), "Unexpected number of syntax errors.");

        testSyntaxError(syntaxErrors.get(0), location(0, 0), sourceString, "Expected expression.");
    }

    @Test
    @Order(9)
    public void testParser_Expression_ArithmeticOperators_MissingLeftOperand() {
        String sourceString = "* 5";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        parser.parse(null, lexedStatements);

        assertTrue(syntaxErrorReporter.hasErrors(), "Expected a syntax error for missing left operand.");
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        assertEquals(1, syntaxErrors.size(), "Unexpected number of syntax errors.");

        testSyntaxError(syntaxErrors.get(0), location(0, 0), sourceString, "Expected expression.");
    }

    @Test
    @Order(10)
    public void testParser_Expression_ArithmeticOperators_MissingRightOperand() {
        String sourceString = "2 +";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        parser.parse(null, lexedStatements);

        assertTrue(syntaxErrorReporter.hasErrors(), "Expected a syntax error for missing right operand.");
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        assertEquals(1, syntaxErrors.size(), "Unexpected number of syntax errors.");

        testSyntaxError(syntaxErrors.get(0), location(0, 2), sourceString, "Expected expression.");
    }

    @Test
    @Order(11)
    public void testParser_Expression_ArithmeticOperators_InvalidOperand() {
        String sourceString = "5 + true";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(BooleanCrystal.class, rightOperand.getClass(), "Unexpected literal type.");

        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        BooleanCrystal rightBoolean = (BooleanCrystal) rightOperand;

        assertEquals(5, leftNumber.getValue(), "Unexpected literal value.");
        assertEquals(true, rightBoolean.getValue(), "Unexpected literal value.");

        // syntax errors
        assertSyntaxErrors(syntaxErrorReporter, 1);
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();

        testSyntaxError(syntaxErrors.get(0), location(0, 4), sourceString, "Arithmetic expression expects a Number for operands.");
    }

    @Test
    @Order(12)
    public void testParser_Expression_ArithmeticOperators_InvalidOperand_FromVariable() {
        String sourceString = "bool << true, 5 + bool";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        int expectedSize = 2;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(VariableDeclarationStatement.class, statement.getClass(), "Unexpected statement type.");

        // statement 2
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(VariableExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((LiteralExpression) left).getValue();
        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        IntegerCrystal leftNumber = (IntegerCrystal) leftOperand;
        assertEquals(5, leftNumber.getValue(), "Unexpected literal value.");

        AtonementCrystal rightOperand = ((VariableExpression) right).getReference();
        assertEquals(ReferenceCrystal.class, rightOperand.getClass(), "Unexpected crystal type.");

        // syntax errors
        assertSyntaxErrors(syntaxErrorReporter, 1);
        List<VikariError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();

        testSyntaxError(syntaxErrors.get(0), location(0, 18), sourceString, "Arithmetic expression expects a Number for operands.");
    }
}
