package com.atonement.crystals.dnr.vikari.interpreter.parser.statement;

import com.atonement.crystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.BinaryOperatorCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.literal.number.LongLiteralCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.operator.math.AddOperatorCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.operator.math.LeftDivideOperatorCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.operator.math.MultiplyOperatorCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.operator.math.SubtractOperatorCrystal;
import com.atonement.crystals.dnr.vikari.core.expression.BinaryExpression;
import com.atonement.crystals.dnr.vikari.core.expression.Expression;
import com.atonement.crystals.dnr.vikari.core.expression.GroupingExpression;
import com.atonement.crystals.dnr.vikari.core.expression.LiteralExpression;
import com.atonement.crystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonement.crystals.dnr.vikari.core.statement.Statement;
import com.atonement.crystals.dnr.vikari.error.SyntaxErrorReporter;
import com.atonement.crystals.dnr.vikari.interpreter.Lexer;
import com.atonement.crystals.dnr.vikari.interpreter.Parser;
import com.atonement.crystals.dnr.vikari.interpreter.parser.ParserTest_Utils;
import com.atonement.crystals.dnr.vikari.util.CoordinatePair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static com.atonement.crystals.dnr.vikari.interpreter.parser.ParserTest_Utils.assertNoSyntaxErrors;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParserTest_ExpressionStatement {
    private static final CoordinatePair COORDINATE_PAIR_ZERO_ZERO = new CoordinatePair(0, 0);

    @Test
    @Order(1)
    public void testMultipleStatements_Simple() {
        String sourceString = "22\n7";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lexVikariSourceCode(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 2;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // -----------
        // statement 1
        // -----------
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        CoordinatePair expectedLocation = COORDINATE_PAIR_ZERO_ZERO;
        CoordinatePair actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(LiteralExpression.class, expression.getClass(), "Unexpected expression type.");

        LiteralExpression literalExpression = (LiteralExpression) expression;
        AtonementCrystal value = literalExpression.getValue();
        assertEquals(LongLiteralCrystal.class, value.getClass(), "Unexpected literal type.");

        LongLiteralCrystal number = (LongLiteralCrystal) value;
        assertEquals(22, number.getValue(), "Unexpected literal value.");

        // -----------
        // statement 2
        // -----------
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement2 = (ExpressionStatement) statement;

        expectedLocation = new CoordinatePair(1, 0);
        actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression
        Expression expression2 = expressionStatement2.getExpression();
        assertEquals(LiteralExpression.class, expression2.getClass(), "Unexpected expression type.");

        LiteralExpression literalExpression2 = (LiteralExpression) expression2;
        AtonementCrystal value2 = literalExpression2.getValue();
        assertEquals(LongLiteralCrystal.class, value2.getClass(), "Unexpected literal type.");

        LongLiteralCrystal number2 = (LongLiteralCrystal) value2;
        assertEquals(7, number2.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(2)
    public void testMultipleStatements_Arithmetic() {
        String sourceString = "3 - 7\n" +
                              "12 * 24";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lexVikariSourceCode(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 2;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // -----------
        // statement 1
        // -----------
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        CoordinatePair expectedLocation = COORDINATE_PAIR_ZERO_ZERO;
        CoordinatePair actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        // left operand
        Expression leftOperand = binaryExpression.getLeft();
        assertEquals(LiteralExpression.class, leftOperand.getClass(), "Unexpected expression type.");

        AtonementCrystal leftValue = ((LiteralExpression) leftOperand).getValue();
        assertEquals(LongLiteralCrystal.class, leftValue.getClass(), "Unexpected literal type.");

        LongLiteralCrystal number = (LongLiteralCrystal) leftValue;
        assertEquals(3, number.getValue(), "Unexpected literal value.");

        // operator
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        assertEquals(SubtractOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");

        // right operand
        Expression rightOperand = binaryExpression.getRight();
        assertEquals(LiteralExpression.class, rightOperand.getClass(), "Unexpected expression type.");

        AtonementCrystal rightValue = ((LiteralExpression) rightOperand).getValue();
        assertEquals(LongLiteralCrystal.class, rightValue.getClass(), "Unexpected literal type.");

        number = (LongLiteralCrystal) rightValue;
        assertEquals(7, number.getValue(), "Unexpected literal value.");

        // -----------
        // statement 2
        // -----------
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        expressionStatement = (ExpressionStatement) statement;

        expectedLocation = new CoordinatePair(1, 0);
        actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression
        expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");
        binaryExpression = (BinaryExpression) expression;

        // left operand
        leftOperand = binaryExpression.getLeft();
        assertEquals(LiteralExpression.class, leftOperand.getClass(), "Unexpected expression type.");

        leftValue = ((LiteralExpression) leftOperand).getValue();
        assertEquals(LongLiteralCrystal.class, leftValue.getClass(), "Unexpected literal type.");

        number = (LongLiteralCrystal) leftValue;
        assertEquals(12, number.getValue(), "Unexpected literal value.");

        // operator
        operator = binaryExpression.getOperator();
        assertEquals(MultiplyOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");

        // right operand
        rightOperand = binaryExpression.getRight();
        assertEquals(LiteralExpression.class, rightOperand.getClass(), "Unexpected expression type.");

        rightValue = ((LiteralExpression) rightOperand).getValue();
        assertEquals(LongLiteralCrystal.class, rightValue.getClass(), "Unexpected literal type.");

        number = (LongLiteralCrystal) rightValue;
        assertEquals(24, number.getValue(), "Unexpected literal value.");
    }

    @Test
    @Order(3)
    public void testMultipleStatements_MixOfExpressionTypes() {
        String sourceString = "2 - 7\n" +
                              "5\n" +
                              "[22 / 3] + 2\n" +
                              "9";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lexVikariSourceCode(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        int expectedSize = 4;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // -----------
        // statement 1
        // -----------
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        CoordinatePair expectedLocation = COORDINATE_PAIR_ZERO_ZERO;
        CoordinatePair actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression (subtract: "2 - 7")
        Expression expression = expressionStatement.getExpression();
        ParserTest_Utils.testBinaryExpression(expression, SubtractOperatorCrystal.class, 2L, 7L);

        // -----------
        // statement 2
        // -----------
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        expressionStatement = (ExpressionStatement) statement;

        expectedLocation = new CoordinatePair(1, 0);
        actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression (literal: "5")
        expression = expressionStatement.getExpression();
        ParserTest_Utils.testIntegerLiteralExpression(expression, 5L);

        // -----------
        // statement 3
        // -----------
        statement = parsedStatements.get(2);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        expressionStatement = (ExpressionStatement) statement;

        expectedLocation = new CoordinatePair(2, 0);
        actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // first expression (add: "[22 / 3] + 2")
        expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(GroupingExpression.class, left.getClass(), "Unexpected expression type.");
        GroupingExpression groupingExpression = (GroupingExpression) left;
        Expression innerExpression = groupingExpression.getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, LeftDivideOperatorCrystal.class, 22L, 3L);

        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        ParserTest_Utils.testIntegerLiteralExpression(right, 2L);

        // -----------
        // statement 4
        // -----------
        statement = parsedStatements.get(3);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        expressionStatement = (ExpressionStatement) statement;

        expectedLocation = new CoordinatePair(3, 0);
        actualLocation = statement.getLocation();
        assertEquals(expectedLocation, actualLocation, "Unexpected coordinates for statement.");

        // expression 1 (literal: "9")
        expression = expressionStatement.getExpression();
        ParserTest_Utils.testIntegerLiteralExpression(expression, 9L);
    }
}