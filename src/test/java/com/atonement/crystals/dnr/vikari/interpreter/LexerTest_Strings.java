package com.atonement.crystals.dnr.vikari.interpreter;

import com.atonement.crystals.dnr.vikari.error.Vikari_LexerException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test that String literal identifiers (i.e. ``foo``) are properly
 * collapsed down into a singular string after the lexical analysis step.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LexerTest_Strings {

    @Test
    @Order(1)
    public void testLexer_StringAnalysis_CaptureQuotations_BasicStringLiteral() {
        String sourceString = "``a:Integer << 2, :a + 5, _``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 1;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = sourceString;
        String actualToken = statementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");
    }

    @Test
    @Order(2)
    public void testLexer_StringAnalysis_CaptureQuotations_BasicStringLiteralAssignment() {
        String sourceString = "a << ``b:Integer << 2, :b + 5, _``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 1;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 5;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String actualStringToken = statementTokens.get(4);
        String expectedStringToken = "``b:Integer << 2, :b + 5, _``";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");
    }

    @Test
    @Order(3)
    public void testLexer_StringAnalysis_CaptureQuotations_BasicTwoLineString() {
        String sourceString = "``This is a string which spans \n" +
                                "across two individual lines.``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 2;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // test line 1
        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String actualStringToken = statementTokens.get(0);
        String expectedStringToken = "``This is a string which spans ";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");

        // test line 2
        statementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        actualStringToken = statementTokens.get(0);
        expectedStringToken = "across two individual lines.``";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");
    }

    @Test
    @Order(4)
    public void testLexer_StringAnalysis_CaptureQuotations_BasicThreeLineString() {
        String sourceString = "``This string needs to span \n" +
                "across 3 lines. So I'm going to \n" +
                "ensure that it most certainly does.``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 3;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // test line 1
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String actualStringToken = firstStatementTokens.get(0);
        String expectedStringToken = "``This string needs to span ";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");

        // test line 2
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        actualStringToken = secondStatementTokens.get(0);
        expectedStringToken = "across 3 lines. So I'm going to ";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");

        // test line 3
        List<String> thirdStatementTokens = listOfStatementTokens.get(2);

        expectedTokenCount = 1;
        actualTokenCount = thirdStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        actualStringToken = thirdStatementTokens.get(0);
        expectedStringToken = "ensure that it most certainly does.``";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");
    }

    @Test
    @Order(5)
    public void testLexer_StringAnalysis_CaptureQuotations_MultiLineStringAfterCodeStatement() {
        String sourceString = "foo << ``This is a string being assigned \n" +
                "to an identifier named: `foo`.``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 2;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // test line 1
        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 5;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String actualStringToken = statementTokens.get(4);
        String expectedStringToken = "``This is a string being assigned ";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");

        // test line 2
        statementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        actualStringToken = statementTokens.get(0);
        expectedStringToken = "to an identifier named: `foo`.``";
        assertEquals(expectedStringToken, actualStringToken, "Malformed string literal.");
    }

    @Test
    @Order(7)
    public void testLexer_StringAnalysis_CaptureQuotations_StringLiteralContainingCodeWithOtherEnclosures() {
        String sourceString = "foo << ``}bar{ << 2, :{foo}.`baz`, buzz << _``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 1;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 5;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "``}bar{ << 2, :{foo}.`baz`, buzz << _``";
        String actualToken = statementTokens.get(4);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");
    }

    @Test
    @Order(8)
    public void testLexer_StringAnalysis_CaptureQuotations_StringLiteralContainingCodeAcrossMultipleLines() {
        String sourceString = "function << () :: ``{foo} << *\n" +
                "}bar{ << 2\n" +
                ":foo.`baz`\n" +
                "buzz << _``";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 4;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // Test line 1
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 10;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "``{foo} << *";
        String actualToken = firstStatementTokens.get(9);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");

        // Test line 2
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "}bar{ << 2";
        actualToken = secondStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");

        // Test line 3
        List<String> thirdStatementTokens = listOfStatementTokens.get(2);

        expectedTokenCount = 1;
        actualTokenCount = thirdStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = ":foo.`baz`";
        actualToken = thirdStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");

        // Test line 4
        List<String> fourthStatementTokens = listOfStatementTokens.get(3);

        expectedTokenCount = 1;
        actualTokenCount = fourthStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "buzz << _``";
        actualToken = fourthStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed string literal.");
    }

    @Test
    @Order(9)
    public void testLexer_StringAnalysis_CaptureQuotations_ErrorHandlingForUnclosedString_SingleLine() {
        String sourceString = "``This is a malformed string literal.";
        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        try {
            lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);
        } catch (Vikari_LexerException e) {
            // success
            return;
        }
        fail("Missing exception for malformed string literal missing a capture quotation `` at the end.");
    }

    @Test
    @Order(10)
    public void testLexer_StringAnalysis_CaptureQuotations_ErrorHandlingForUnclosedString_MultiLine() {
        String sourceString = "``This is a malformed string literal \n" +
                "because it has no ending capture quotation!";
        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        try {
            lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);
        } catch (Vikari_LexerException e) {
            // success
            return;
        }
        fail("Missing exception for malformed string literal missing a capture quotation `` at the end.");
    }
}
