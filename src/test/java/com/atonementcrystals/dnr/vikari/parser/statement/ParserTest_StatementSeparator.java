package com.atonementcrystals.dnr.vikari.parser.statement;

import com.atonementcrystals.dnr.vikari.TestUtils;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.AddOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.LeftDivideOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.MultiplyOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.SubtractOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.Expression;
import com.atonementcrystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonementcrystals.dnr.vikari.core.statement.Statement;
import com.atonementcrystals.dnr.vikari.error.SyntaxError;
import com.atonementcrystals.dnr.vikari.error.SyntaxErrorReporter;
import com.atonementcrystals.dnr.vikari.interpreter.Lexer;
import com.atonementcrystals.dnr.vikari.interpreter.Parser;
import com.atonementcrystals.dnr.vikari.parser.ParserTest_Utils;
import com.atonementcrystals.dnr.vikari.util.CoordinatePair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParserTest_StatementSeparator {

    private List<Statement> lexAndParse(String sourceString) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        TestUtils.assertNoSyntaxErrors(syntaxErrorReporter);

        return parsedStatements;
    }

    private List<SyntaxError> lexAndParse_ErrorCase(String sourceString) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        parser.parse(null, lexedStatements);

        return syntaxErrorReporter.getSyntaxErrors();
    }

    // The following 21 tests demonstrate Vikari code statements that would not be
    // normally be written in production code (a series of sequential arithmetic
    // binary expression statements with no side effects.) But at this point in
    // development, testing a sequence of arithmetic expressions is the most easily
    // verifiable method of testing the output of the Parser. So these tests simply
    // demonstrate that the Parser is correctly accepting statement separators
    // between a series of statements in a variety of combinations and scenarios.

    @Test
    @Order(1)
    public void testSingleTerminatedStatement() {
        String sourceString = "5 + 2,";
        List<Statement> parsedStatements = lexAndParse(sourceString);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, AddOperatorCrystal.class, 5, 2);
    }

    @Test
    @Order(2)
    public void testSingleTerminatedStatement_LeadingSpace() {
        String sourceString = "3 - 9 ,";
        List<Statement> parsedStatements = lexAndParse(sourceString);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, SubtractOperatorCrystal.class, 3, 9);
    }

    @Test
    @Order(3)
    public void testSingleTerminatedStatement_TrailingSpace() {
        String sourceString = "2 * 8, ";
        List<Statement> parsedStatements = lexAndParse(sourceString);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, MultiplyOperatorCrystal.class, 2, 8);
    }

    @Test
    @Order(4)
    public void testSingleTerminatedStatement_LeadingAndTrailingSpace() {
        String sourceString = "22 / 7 , ";
        List<Statement> parsedStatements = lexAndParse(sourceString);

        int expectedSize = 1;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, LeftDivideOperatorCrystal.class, 22, 7);
    }

    private void checkTwoArithmeticExpressionStatements(List<Statement> parsedStatements) {
        int expectedSize = 2;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, SubtractOperatorCrystal.class, 3, 9);

        // statement 2
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, LeftDivideOperatorCrystal.class, 22, 7);
    }

    @Test
    @Order(5)
    public void testTwoStatements_NoSpaces() {
        String sourceString = "3 - 9,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkTwoArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(6)
    public void testTwoStatements_LeadingSpace() {
        String sourceString = "3 - 9 ,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkTwoArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(7)
    public void testTwoStatements_TrailingSpace() {
        String sourceString = "3 - 9, 22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkTwoArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(8)
    public void testTwoStatements_LeadingAndTrailingSpaces() {
        String sourceString = "3 - 9 , 22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkTwoArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(9)
    public void testMultipleLiteralExpressionStatements() {
        String sourceString = "5,22,7";
        List<Statement> parsedStatements = lexAndParse(sourceString);

        int expectedSize = 3;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testIntegerLiteralExpression(innerExpression, 5);

        // statement 2
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testIntegerLiteralExpression(innerExpression, 22);

        // statement 3
        statement = parsedStatements.get(2);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testIntegerLiteralExpression(innerExpression, 7);
    }

    private void checkFourArithmeticExpressionStatements(List<Statement> parsedStatements) {
        int expectedSize = 4;
        int actualSize = parsedStatements.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of statements.");

        // statement 1
        Statement statement = parsedStatements.get(0);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        Expression innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, AddOperatorCrystal.class, 5, 2);

        // statement 2
        statement = parsedStatements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, SubtractOperatorCrystal.class, 3, 9);

        // statement 3
        statement = parsedStatements.get(2);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, MultiplyOperatorCrystal.class, 2, 8);

        // statement 4
        statement = parsedStatements.get(3);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");

        innerExpression = ((ExpressionStatement) statement).getExpression();
        ParserTest_Utils.testBinaryExpression(innerExpression, LeftDivideOperatorCrystal.class, 22, 7);
    }

    @Test
    @Order(10)
    public void testMultipleExpressionStatements() {
        String sourceString = "5 + 2,3 - 9,2 * 8,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(11)
    public void testMultipleExpressionStatements_SeparateLines() {
        String sourceString = "5 + 2,\n" +
                "3 - 9,\n" +
                "2 * 8,\n" +
                "22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(12)
    public void testMultipleExpressionStatements_SeparateLines_LeadingSpaces() {
        String sourceString = "5 + 2 ,\n" +
                "3 - 9 ,\n" +
                "2 * 8 ,\n" +
                "22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(13)
    public void testMultipleExpressionStatements_SeparateLines_TrailingSpaces() {
        String sourceString = "5 + 2, \n" +
                              "3 - 9, \n" +
                              "2 * 8, \n" +
                              "22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(14)
    public void testMultipleExpressionStatements_SeparateLines_LeadingAndTrailingSpaces() {
        String sourceString = "5 + 2 , \n" +
                              "3 - 9 , \n" +
                              "2 * 8 , \n" +
                              "22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(15)
    public void testMultipleExpressionStatements_TwoPerSeparateLine_NoSpaces() {
        String sourceString = "5 + 2,3 - 9\n" +
                              "2 * 8,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(16)
    public void testMultipleExpressionStatements_TwoPerSeparateLine_LeadingSpaces() {
        String sourceString = "5 + 2 ,3 - 9\n" +
                              "2 * 8 ,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(17)
    public void testMultipleExpressionStatements_TwoPerSeparateLine_TrailingSpaces() {
        String sourceString = "5 + 2, 3 - 9\n" +
                              "2 * 8, 22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(18)
    public void testMultipleExpressionStatements_TwoPerSeparateLine_LeadingAndTrailingSpaces() {
        String sourceString = "5 + 2,3 - 9\n" +
                "2 * 8,22 / 7";
        List<Statement> parsedStatements = lexAndParse(sourceString);
        checkFourArithmeticExpressionStatements(parsedStatements);
    }

    @Test
    @Order(19)
    public void syntaxError_SingleStatement() {
        String sourceString = "5 +,";
        List<SyntaxError> syntaxErrors = lexAndParse_ErrorCase(sourceString);

        int expectedSize = 1;
        int actualSize = syntaxErrors.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of syntax errors.");

        SyntaxError error1 = syntaxErrors.get(0);
        CoordinatePair expectedLocation = new CoordinatePair(0, 2);
        String expectedLine = sourceString;
        TestUtils.testSyntaxError(error1, expectedLocation, expectedLine, "Expected expression.");
    }

    @Test
    @Order(20)
    public void syntaxError_TwoStatements_SingleLine() {
        String sourceString = "5 +,* 7,";
        List<SyntaxError> syntaxErrors = lexAndParse_ErrorCase(sourceString);

        int expectedSize = 2;
        int actualSize = syntaxErrors.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of syntax errors.");

        SyntaxError error1 = syntaxErrors.get(0);
        CoordinatePair expectedLocation = new CoordinatePair(0, 2);
        String expectedLine = sourceString;
        TestUtils.testSyntaxError(error1, expectedLocation, expectedLine, "Expected expression.");

        SyntaxError error2 = syntaxErrors.get(1);
        expectedLocation = new CoordinatePair(0, 4);
        expectedLine = sourceString;
        TestUtils.testSyntaxError(error2, expectedLocation, expectedLine, "Expected expression.");
    }

    @Test
    @Order(21)
    public void syntaxError_FourStatements_MultipleLines() {
        String sourceString = "5 ++, * 7 *,\n"+
                              "22 -, / 3";
        List<SyntaxError> syntaxErrors = lexAndParse_ErrorCase(sourceString);

        int expectedSize = 4;
        int actualSize = syntaxErrors.size();
        assertEquals(expectedSize, actualSize, "Unexpected number of syntax errors.");

        SyntaxError error1 = syntaxErrors.get(0);
        CoordinatePair expectedLocation = new CoordinatePair(0, 2);
        String expectedLine = "5 ++, * 7 *,";
        TestUtils.testSyntaxError(error1, expectedLocation, expectedLine, "Expected expression.");

        SyntaxError error2 = syntaxErrors.get(1);
        expectedLocation = new CoordinatePair(0, 6);
        expectedLine = "5 ++, * 7 *,";
        TestUtils.testSyntaxError(error2, expectedLocation, expectedLine, "Expected expression.");

        SyntaxError error3 = syntaxErrors.get(2);
        expectedLocation = new CoordinatePair(1, 3);
        expectedLine = "22 -, / 3";
        TestUtils.testSyntaxError(error3, expectedLocation, expectedLine, "Expected expression.");

        SyntaxError error4 = syntaxErrors.get(3);
        expectedLocation = new CoordinatePair(1, 6);
        expectedLine = "22 -, / 3";
        TestUtils.testSyntaxError(error4, expectedLocation, expectedLine, "Expected expression.");
    }
}
