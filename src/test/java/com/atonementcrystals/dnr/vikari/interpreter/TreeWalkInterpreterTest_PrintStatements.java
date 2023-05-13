package com.atonementcrystals.dnr.vikari.interpreter;

import com.atonementcrystals.dnr.vikari.core.statement.Statement;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.error.SyntaxErrorReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static com.atonementcrystals.dnr.vikari.TestUtils.assertNoSyntaxErrors;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeWalkInterpreterTest_PrintStatements {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream testOut = new ByteArrayOutputStream();

    @BeforeEach
    public void setupPrintStream() {
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void restorePrintStream() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to efficiently test Vikari print statements.
     * @param sourceString The Vikari source code to execute.
     * @param expectedOutput The expected output for the print statements.
     */
    public void testPrintStatement(String sourceString, String expectedOutput) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        TreeWalkInterpreter interpreter = new TreeWalkInterpreter();

        SyntaxErrorReporter syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lexVikariSourceCode(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        assertNoSyntaxErrors(syntaxErrorReporter);

        for (Statement statement : parsedStatements) {
            interpreter.execute(statement);
        }
        String actualOutput = testOut.toString();
        assertEquals(expectedOutput, actualOutput, "Unexpected output of print statement.");
    }

    @Test
    @Order(1)
    public void testPrintExpression_Empty() {
        testPrintStatement(":", "\n");
    }

    @Test
    @Order(2)
    public void testPrintExpression_Simple() {
        testPrintStatement(":5", "5");
    }

    @Test
    @Order(3)
    public void testPrintExpression_Chained() {
        testPrintStatement(":5:3:7", "537");
    }

    @Test
    @Order(4)
    public void testPrintlnExpression_Simple() {
        testPrintStatement(":5:", "5\n");
    }

    @Test
    @Order(5)
    public void testPrintlnExpression_Chained() {
        testPrintStatement(":5:3:7:", "537\n");
    }

    @Test
    @Order(6)
    public void testPrintlnExpression() {
        testPrintStatement(":5:", "5\n");
    }

    @Test
    @Order(7)
    public void testPrintExpression_BinaryExpression() {
        testPrintStatement(":5 + 3", "8");
    }

    @Test
    @Order(8)
    public void testPrintlnExpression_BinaryExpression() {
        testPrintStatement(":5 + 3:", "8\n");
    }

    @Test
    @Order(9)
    public void testPrintlnExpression_BinaryExpression_Chained() {
        testPrintStatement(":5 + 3:7 - 2:", "85\n");
    }

    @Test
    @Order(10)
    public void testPrintExpression_MultiLine() {
        testPrintStatement(":5 + 3\n:7 - 2", "85");
    }

    @Test
    @Order(11)
    public void testPrintlnExpression_MultiLine() {
        testPrintStatement(":5 + 3:\n:7 - 2:", "8\n5\n");
    }

    @Test
    @Order(12)
    public void testPrintExpression_Chained_MultiLine() {
        testPrintStatement(":5 + 3\n:7 - 2\n:\n:22:7", "85\n227");
    }

    @Test
    @Order(13)
    public void testPrintlnExpression_Chained_MultiLine() {
        testPrintStatement(":5 + 3:\n:7 - 2:\n:\n:22:7:", "8\n5\n\n227\n");
    }
}