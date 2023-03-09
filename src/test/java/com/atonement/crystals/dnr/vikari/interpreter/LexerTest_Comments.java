package com.atonement.crystals.dnr.vikari.interpreter;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test all necessary functionality of lexical analysis of DNR source files
 * into individual string tokens. Specifically regarding the handling of
 * comment enclosure collapsions.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LexerTest_Comments {

    @Test
    @Order(1)
    public void testLexer_StringAnalysis_CommentEnclosure_BasicOneLineComment() {
        String sourceString = "~:This is a comment.:~";

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
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }

    @Test
    @Order(2)
    public void testLexer_StringAnalysis_CommentEnclosure_BasicTwoLineComment() {
        String sourceString = "~:This is a comment\n" +
                "across two lines.:~";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 2;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // Test the first statement's tokens
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "~:This is a comment";
        String actualToken = firstStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");

        // Test the second statement's tokens
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "across two lines.:~";
        actualToken = secondStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }

    @Test
    @Order(3)
    public void testLexer_StringAnalysis_CommentEnclosure_BasicThreeLineComment() {
        String sourceString = "~:This is a comment\n" +
                "across three lines\n" +
                "to test enclosures.:~";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 3;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // Test the first statement's tokens
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "~:This is a comment";
        String actualToken = firstStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");

        // Test the second statement's tokens
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "across three lines";
        actualToken = secondStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");


        // Test the third statement's tokens
        List<String> thirdStatementTokens = listOfStatementTokens.get(2);

        expectedTokenCount = 1;
        actualTokenCount = thirdStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "to test enclosures.:~";
        actualToken = thirdStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }

    @Test
    @Order(4)
    public void testLexer_StringAnalysis_CommentEnclosure_EndOfLineComment() {
        String sourceString = "a << 314 ~:`a` is approximately: [pi * 100].:~";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 1;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        List<String> statementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 7;
        int actualTokenCount = statementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "~:`a` is approximately: [pi * 100].:~";
        String actualToken = statementTokens.get(6);
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }

    @Test
    @Order(5)
    public void testLexer_StringAnalysis_CommentEnclosure_TwoLineComment_AfterStatement_OnFirstLine() {
        String sourceString = "a << 314 ~:`a` is approximately: [pi * 100].\n" +
                "But sometimes, we prefer to use tau instead!:~";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 2;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // first statement
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 7;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "~:`a` is approximately: [pi * 100].";
        String actualToken = firstStatementTokens.get(6);
        assertEquals(expectedToken, actualToken, "Malformed comment.");

        // second statement
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 1;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "But sometimes, we prefer to use tau instead!:~";
        actualToken = secondStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }

    @Test
    @Order(6)
    public void testLexer_StringAnalysis_CommentEnclosure_TwoLineComment_BeforeStatement_OnSecondLine() {
        String sourceString = "~:`a` is approximately: [pi * 100].\n" +
                "But sometimes, we prefer to use tau instead!:~ a << 314";

        Lexer lexer = new Lexer();
        List<List<String>> listOfStatementTokens = lexer.readStringAsBasicStringTokens(sourceString);
        listOfStatementTokens = lexer.collapseEnclosuresOfStringTokens(listOfStatementTokens);

        int expectedStatementCount = 2;
        int actualStatementCount = listOfStatementTokens.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected number of statements.");

        // first statement
        List<String> firstStatementTokens = listOfStatementTokens.get(0);

        int expectedTokenCount = 1;
        int actualTokenCount = firstStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        String expectedToken = "~:`a` is approximately: [pi * 100].";
        String actualToken = firstStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");

        // second statement
        List<String> secondStatementTokens = listOfStatementTokens.get(1);

        expectedTokenCount = 7;
        actualTokenCount = secondStatementTokens.size();
        assertEquals(expectedTokenCount, actualTokenCount, "Unexpected number of tokens.");

        expectedToken = "But sometimes, we prefer to use tau instead!:~";
        actualToken = secondStatementTokens.get(0);
        assertEquals(expectedToken, actualToken, "Malformed comment.");
    }
}
