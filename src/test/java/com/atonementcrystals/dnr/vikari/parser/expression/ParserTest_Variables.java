package com.atonementcrystals.dnr.vikari.parser.expression;

import com.atonementcrystals.dnr.vikari.TestUtils;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementField;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.ReferenceCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.VikariType;
import com.atonementcrystals.dnr.vikari.core.crystal.number.IntegerCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.BinaryOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.AddOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.BinaryExpression;
import com.atonementcrystals.dnr.vikari.core.expression.Expression;
import com.atonementcrystals.dnr.vikari.core.expression.LiteralExpression;
import com.atonementcrystals.dnr.vikari.core.expression.VariableExpression;
import com.atonementcrystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonementcrystals.dnr.vikari.core.statement.Statement;
import com.atonementcrystals.dnr.vikari.core.statement.VariableDeclarationStatement;
import com.atonementcrystals.dnr.vikari.error.SyntaxError;
import com.atonementcrystals.dnr.vikari.error.SyntaxErrorReporter;
import com.atonementcrystals.dnr.vikari.interpreter.Lexer;
import com.atonementcrystals.dnr.vikari.interpreter.Parser;
import com.atonementcrystals.dnr.vikari.interpreter.VikariProgram;
import com.atonementcrystals.dnr.vikari.util.CoordinatePair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParserTest_Variables {
    private final AtonementField globalAtonementField = VikariProgram.initGlobalAtonementField();
    private AtonementField rootEnvironment;
    SyntaxErrorReporter syntaxErrorReporter;

    public List<Statement> lexAndParse(String sourceString) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        parser.setGlobalAtonementField(globalAtonementField);

        syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        TestUtils.assertNoSyntaxErrors(syntaxErrorReporter);
        rootEnvironment = parser.getRootEnvironment();

        return parsedStatements;
    }

    public List<Statement> lexAndParse_WithErrors(String sourceString, int expectedErrorCount) {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        parser.setGlobalAtonementField(globalAtonementField);

        syntaxErrorReporter = new SyntaxErrorReporter();
        lexer.setSyntaxErrorReporter(syntaxErrorReporter);
        parser.setSyntaxErrorReporter(syntaxErrorReporter);

        List<List<AtonementCrystal>> lexedStatements = lexer.lex(sourceString);
        List<Statement> parsedStatements = parser.parse(null, lexedStatements);

        TestUtils.assertSyntaxErrors(syntaxErrorReporter, expectedErrorCount);
        rootEnvironment = parser.getRootEnvironment();

        return parsedStatements;
    }

    public void testDeclaration(Statement statement,
                                String identifier, VikariType declaredType, VikariType instantiatedType, CoordinatePair location) {

        assertEquals(VariableDeclarationStatement.class, statement.getClass(), "Unexpected statement type.");
        VariableDeclarationStatement declarationStatement = (VariableDeclarationStatement) statement;

        assertEquals(location, statement.getLocation(), "Unexpected location.");

        AtonementCrystal variable = declarationStatement.getDeclaredVariable();
        testVariableCrystal(variable, identifier, declaredType, instantiatedType, location, rootEnvironment);
        assertNull(declarationStatement.getAssignmentOperator(), "Expected operator to be null.");
        assertNull(declarationStatement.getInitializerExpression(), "Expected initializer expression to be null.");
    }

    /**
     * Set localEnvironment to null for the redefined variable error case.
     */
    public void testVariableCrystal(AtonementCrystal variable, String expectedIdentifier,
                                    VikariType expectedDeclaredType, VikariType expectedInstantiatedType,
                                    CoordinatePair expectedCoordinates, AtonementField localEnvironment) {
        // Check variable from expression
        assertEquals(expectedIdentifier, variable.getIdentifier(), "Unexpected variable identifier.");
        assertEquals(expectedDeclaredType.getTypeCrystal(), variable.getDeclaredType(), "Unexpected declared type.");

        if (expectedInstantiatedType == null) {
            assertNull(variable.getInstantiatedType(), "Expected instantiated type to be null.");
        } else{
            assertEquals(expectedInstantiatedType.getTypeCrystal(), variable.getInstantiatedType(), "Unexpected instantiated type.");
        }

        assertEquals(expectedCoordinates, variable.getCoordinates(), "Unexpected coordinates.");

        // Check variable exists in local environment
        if (localEnvironment != null) {
            assertTrue(localEnvironment.isDefined(expectedIdentifier), "Expected variable to be defined in local " +
                    "environment.");
            assertTrue(localEnvironment.hasFieldMember(expectedIdentifier), "Expected variable to be a field member " +
                    "of the local environment.");

            AtonementCrystal fieldMember = localEnvironment.get(expectedIdentifier);
            assertEquals(variable.getIdentifier(), fieldMember.getIdentifier(), "Expected variable to be equal to " +
                    "field member of the local environment.");

            assertEquals(variable.getDeclaredType(), fieldMember.getDeclaredType(), "Expected variable to be equal " +
                    "to field member of the local environment.");
        }
    }

    public void testVariableExpression(Statement statement, String identifier, VikariType declaredType,
                                       VikariType instantiatedType, CoordinatePair location) {

        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        assertEquals(location, statement.getLocation(), "Unexpected location.");

        Expression innerExpression = expressionStatement.getExpression();
        assertEquals(VariableExpression.class, innerExpression.getClass(), "Unexpected expression type.");

        AtonementCrystal variable = ((VariableExpression) innerExpression).getReference();
        testVariableCrystal(variable, identifier, declaredType, instantiatedType, location, rootEnvironment);
    }

    /**
     * This is a strange error case. Bare expression statements like this will never be used in production code except
     * for single-statement return values of functions. But yet it is unavoidable that "foo + 5" will return a different
     * error message than "5 + foo" without significant refactoring. Because of how the Parser detects a variable
     * declaration statement. (Any statement beginning with an undefined reference variable.)
     */
    @Test
    @Order(1)
    public void testUndefinedVariableReference_LeadingWithVariable() {
        String sourceString = "foo + 5";

        int expectedErrorCount = 1;
        List<Statement> statements = lexAndParse_WithErrors(sourceString, expectedErrorCount);

        List<SyntaxError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        TestUtils.testSyntaxError(syntaxErrors.get(0), new CoordinatePair(0, 4), sourceString, "Expected token(s) in " +
                "variable declaration statement");

        int expectedStatementCount = 1;
        int actualStatementCount = statements.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected statement count.");

        // This is a malformed variable declaration. Simply assert the type.
        Statement statement = statements.get(0);
        assertEquals(VariableDeclarationStatement.class, statement.getClass(), "Unexpected statement type.");
    }

    @Test
    @Order(2)
    public void testUndefinedVariableReference_VariableAtEnd() {
        String sourceString = "5 + foo";

        int expectedErrorCount = 1;
        List<Statement> statements = lexAndParse_WithErrors(sourceString, expectedErrorCount);

        List<SyntaxError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        TestUtils.testSyntaxError(syntaxErrors.get(0), new CoordinatePair(0, 4), sourceString, "Undefined variable reference.");

        int expectedStatementCount = 1;
        int actualStatementCount = statements.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected statement count.");

        // statement 1
        Statement statement = statements.get(0);
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
        AtonementCrystal rightOperand = ((VariableExpression) right).getReference();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(ReferenceCrystal.class, rightOperand.getClass(), "Unexpected variable type.");

        IntegerCrystal number = (IntegerCrystal) leftOperand;
        ReferenceCrystal variable = (ReferenceCrystal) rightOperand;

        assertEquals(5, number.getValue(), "Unexpected literal value.");
        assertEquals("foo", variable.getIdentifier(), "Unexpected reference identifier.");
    }

    @Test
    @Order(3)
    public void testVariable_DeclarationThenPrimary() {
        String sourceString = "foo,foo";

        List<Statement> statements = lexAndParse(sourceString);

        int expectedStatementCount = 2;
        int actualStatementCount = statements.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected statement count.");

        testDeclaration(statements.get(0), "foo", VikariType.ATONEMENT_CRYSTAL, null, new CoordinatePair(0, 0));
        testVariableExpression(statements.get(1), "foo", VikariType.ATONEMENT_CRYSTAL, null, new CoordinatePair(0, 4));
    }

    @Test
    @Order(4)
    public void testVariable_ArithmeticExpressionTypeError() {
        String sourceString = """
                foo
                foo + 5
                5 + foo
                """;

        int expectedErrorCount = 2;
        List<Statement> statements = lexAndParse_WithErrors(sourceString, expectedErrorCount);

        List<SyntaxError> syntaxErrors = syntaxErrorReporter.getSyntaxErrors();
        TestUtils.testSyntaxError(syntaxErrors.get(0), new CoordinatePair(1, 0), "foo + 5", "Arithmetic " +
                "expression expects a Number for operands.");
        TestUtils.testSyntaxError(syntaxErrors.get(1), new CoordinatePair(2, 4), "5 + foo", "Arithmetic " +
                "expression expects a Number for operands.");

        int expectedStatementCount = 3;
        int actualStatementCount = statements.size();
        assertEquals(expectedStatementCount, actualStatementCount, "Unexpected statement count.");

        // declaration
        testDeclaration(statements.get(0), "foo", VikariType.ATONEMENT_CRYSTAL, null, new CoordinatePair(0, 0));

        // arithmetic expressions

        // statement 2
        Statement statement = statements.get(1);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        ExpressionStatement expressionStatement = (ExpressionStatement) statement;

        // first expression: "foo + 5"
        Expression expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        BinaryExpression binaryExpression = (BinaryExpression) expression;
        Expression left = binaryExpression.getLeft();
        BinaryOperatorCrystal operator = binaryExpression.getOperator();
        Expression right = binaryExpression.getRight();

        assertEquals(VariableExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(LiteralExpression.class, right.getClass(), "Unexpected expression type.");

        AtonementCrystal leftOperand = ((VariableExpression) left).getReference();
        AtonementCrystal rightOperand = ((LiteralExpression) right).getValue();

        assertEquals(ReferenceCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(IntegerCrystal.class, rightOperand.getClass(), "Unexpected variable type.");

        ReferenceCrystal variable = (ReferenceCrystal) leftOperand;
        IntegerCrystal number = (IntegerCrystal) rightOperand;

        assertEquals("foo", variable.getIdentifier(), "Unexpected reference identifier.");
        assertEquals(5, number.getValue(), "Unexpected literal value.");

        // statement 3
        statement = statements.get(2);
        assertEquals(ExpressionStatement.class, statement.getClass(), "Unexpected statement type.");
        expressionStatement = (ExpressionStatement) statement;

        // first expression
        expression = expressionStatement.getExpression();
        assertEquals(BinaryExpression.class, expression.getClass(), "Unexpected expression type.");

        binaryExpression = (BinaryExpression) expression;
        left = binaryExpression.getLeft();
        operator = binaryExpression.getOperator();
        right = binaryExpression.getRight();

        assertEquals(LiteralExpression.class, left.getClass(), "Unexpected expression type.");
        assertEquals(AddOperatorCrystal.class, operator.getClass(), "Unexpected operator type.");
        assertEquals(VariableExpression.class, right.getClass(), "Unexpected expression type.");

        leftOperand = ((LiteralExpression) left).getValue();
        rightOperand = ((VariableExpression) right).getReference();

        assertEquals(IntegerCrystal.class, leftOperand.getClass(), "Unexpected literal type.");
        assertEquals(ReferenceCrystal.class, rightOperand.getClass(), "Unexpected variable type.");

        number = (IntegerCrystal) leftOperand;
        variable = (ReferenceCrystal) rightOperand;

        assertEquals(5, number.getValue(), "Unexpected literal value.");
        assertEquals("foo", variable.getIdentifier(), "Unexpected reference identifier.");
    }
}