package com.atonementcrystals.dnr.vikari.interpreter;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementField;
import com.atonementcrystals.dnr.vikari.core.crystal.TypeCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.TypeHierarchy;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.VikariType;
import com.atonementcrystals.dnr.vikari.core.crystal.literal.BooleanCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.literal.NullCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.literal.NullKeywordCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.BinaryOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.ReferenceCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TypeReferenceCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.TypeLabelOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.assignment.RightAssignmentOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.logical.LogicalNotOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.BinaryExpression;
import com.atonementcrystals.dnr.vikari.core.expression.BinaryExpressionConstructor;
import com.atonementcrystals.dnr.vikari.core.expression.BooleanLogicExpression;
import com.atonementcrystals.dnr.vikari.core.expression.Expression;
import com.atonementcrystals.dnr.vikari.core.expression.LeftAssignmentExpression;
import com.atonementcrystals.dnr.vikari.core.expression.NullLiteralExpression;
import com.atonementcrystals.dnr.vikari.core.expression.PrintExpression;
import com.atonementcrystals.dnr.vikari.core.expression.RightAssignmentExpression;
import com.atonementcrystals.dnr.vikari.core.expression.VariableExpression;
import com.atonementcrystals.dnr.vikari.core.statement.PrintStatement;
import com.atonementcrystals.dnr.vikari.core.statement.Statement;
import com.atonementcrystals.dnr.vikari.core.statement.SyntaxErrorStatement;
import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;
import com.atonementcrystals.dnr.vikari.core.crystal.number.NumberCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.PrintStatementOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.NegateOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.GroupingExpression;
import com.atonementcrystals.dnr.vikari.core.expression.LiteralExpression;
import com.atonementcrystals.dnr.vikari.core.expression.UnaryExpression;
import com.atonementcrystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonementcrystals.dnr.vikari.core.statement.VariableDeclarationStatement;
import com.atonementcrystals.dnr.vikari.error.SyntaxError;
import com.atonementcrystals.dnr.vikari.error.SyntaxErrorReporter;
import com.atonementcrystals.dnr.vikari.error.Vikari_FieldMemberExistsException;
import com.atonementcrystals.dnr.vikari.error.Vikari_ParserException;
import com.atonementcrystals.dnr.vikari.error.Vikari_UndefinedFieldMemberException;
import com.atonementcrystals.dnr.vikari.interpreter.resolver.TypeResolver;
import com.atonementcrystals.dnr.vikari.util.CoordinatePair;
import com.atonementcrystals.dnr.vikari.util.TokenPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Parses the output of the Lexer into an abstract syntax tree consisting of a list of statements.
 */
public class Parser {
    private static final Logger log = LogManager.getLogger(Parser.class);

    private File currentFile;
    private List<List<AtonementCrystal>> lexedStatements;
    private int lineNumber;
    private int tokenNumber;

    private int lineCount;
    private int lastLineLength;

    private List<AtonementCrystal> currentLine;

    private SyntaxErrorReporter syntaxErrorReporter;
    private final TypeResolver typeResolver;

    /** Parent field of the rootEnvironment. */
    private AtonementField globalAtonementField;

    /** Root environments are unique per source file. */
    private final Map<String, AtonementField> rootEnvironments;
    private AtonementField rootEnvironment;
    private AtonementField currentEnvironment;

    private boolean errorReportingEnabled = true;

    public Parser() {
        typeResolver = new TypeResolver();
        rootEnvironments = new HashMap<>();
    }

    public void setSyntaxErrorReporter(SyntaxErrorReporter syntaxErrorReporter) {
        this.syntaxErrorReporter = syntaxErrorReporter;
    }

    public void setGlobalAtonementField(AtonementField globalAtonementField) {
        this.globalAtonementField = globalAtonementField;
    }

    public AtonementField getRootEnvironment() {
        return rootEnvironment;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @SuppressWarnings("unused")
    public void reset() {
        currentFile = null;
        lexedStatements = null;
        lineNumber = 0;
        tokenNumber = 0;
        lineCount = 0;
        lastLineLength = 0;
        currentLine = null;
        typeResolver.clear();
        rootEnvironments.clear();
        rootEnvironment = null;
        currentEnvironment = null;
    }

    public void resetTo(int lineNumber) {
        this.lineNumber = lineNumber;
        for (int i = lexedStatements.size() - 1; i >= lineNumber; i--) {
            lexedStatements.remove(i);
        }
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    @SuppressWarnings("unused")
    public void setErrorReportingEnabled(boolean errorReportingEnabled) {
        this.errorReportingEnabled = errorReportingEnabled;
    }

    public List<Statement> parse(File file, List<List<AtonementCrystal>> lexedStatements) {
        log.trace("parse({})", file == null ? "null" : "``" + file + "``");
        currentFile = file;

        // First pass on user input.
        if (lineNumber == 0 && this.lexedStatements == null) {
            this.lexedStatements = lexedStatements;
            establishRootEnvironment();
        }

        // REPL mode is reusing the Parser state.
        else {
            this.lexedStatements.addAll(lexedStatements);
        }

        // Short-circuit if no statements exist to parse.
        List<Statement> statements = new ArrayList<>();
        if (lexedStatements.isEmpty()) {
            return statements;
        }

        lineCount = this.lexedStatements.size();
        lastLineLength = this.lexedStatements.get(this.lexedStatements.size() - 1).size();

        // Parse the file.
        while (!isAtEnd()) {
            currentLine = this.lexedStatements.get(lineNumber);
            do {
                Statement statement = statement();
                statements.add(statement);
            } while (!isAtEndOfStatement());
            advanceToNextLine();
        }

        // Visit the Resolvers.
        if (errorReportingEnabled) {
            typeResolver.resolve(statements);
            typeResolver.reportErrors(syntaxErrorReporter, file);
        }

        currentFile = null;
        return statements;
    }

    /**
     * Environments are used in the Parser to detect variable declarations
     * and to ensure that variables are declared before they are used.
     */
    private void establishRootEnvironment() {
        // File is for a type or a script.
        // Cache the environment with the file path.
        if (currentFile != null) {
            String filePath = currentFile.getAbsolutePath();
            if (rootEnvironments.containsKey(filePath)) {
                rootEnvironment = rootEnvironments.get(filePath);
            } else {
                rootEnvironment = new AtonementField(globalAtonementField);
                rootEnvironments.put(filePath, rootEnvironment);
            }
        }

        // Code is executed with -c or the REPL.
        // So only one environment is necessary.
        else if (rootEnvironment == null) {
            rootEnvironment = new AtonementField(globalAtonementField);
        }
        currentEnvironment = rootEnvironment;
    }

    private Statement statement() {
        try {
            if (checkVariableDeclaration()) {
                return variableDeclarationStatement();
            }
            if (check(TokenType.TYPE_LABEL)) {
                return printStatement();
            }
            return expressionStatement();
        } catch (Vikari_ParserException e) {
            synchronize();

            List<AtonementCrystal> lexedStatement = getLastVisitedLexedStatement();
            String statementString = lexedStatement.stream()
                    .map(AtonementCrystal::getIdentifier)
                    .collect(Collectors.joining(""));

            CoordinatePair statementLocation = lexedStatement.get(0).getCoordinates();

            SyntaxErrorStatement syntaxErrorStatement = new SyntaxErrorStatement(statementString);
            syntaxErrorStatement.setLocation(statementLocation);
            return syntaxErrorStatement;
        }
    }

    private boolean checkVariableDeclaration() {
        if (check(ReferenceCrystal.class)) {
            AtonementCrystal reference = peek();
            String identifier = reference.getIdentifier();

            // We know it is a variable declaration if it is undefined.
            if (!currentEnvironment.hasFieldMember(identifier)) {
                return true;
            }

            // Parse as a variable declaration for the error case.
            return lookAhead() instanceof TypeLabelOperatorCrystal;
        }
        return false;
    }

    private Statement variableDeclarationStatement() {
        ReferenceCrystal variableToDefine = consume(ReferenceCrystal.class, "Expected a variable reference.");

        // Check for an optional type label.
        TypeReferenceCrystal typeReference;
        TypeCrystal declaredType = null;

        if (match(TokenType.TYPE_LABEL)) {
            typeReference = consume(TypeReferenceCrystal.class, "Expected type reference after type label operator.");
            String typeName = typeReference.getIdentifier();

            // Fetch the crystal definition from the environment.
            try {
                declaredType = (TypeCrystal) rootEnvironment.get(typeName);
            } catch (Vikari_UndefinedFieldMemberException e) {
                error(typeReference, "Unknown Type.");
                declaredType = VikariType.ATONEMENT_CRYSTAL.getTypeCrystal();
            } catch (ClassCastException e) {
                throw new Vikari_ParserException("Internal error. Type identifier not mapped to a TypeCrystal.");
            }
        }

        if (declaredType == null) {
            declaredType = VikariType.ATONEMENT_CRYSTAL.getTypeCrystal();
        }
        variableToDefine.setDeclaredType(declaredType);

        // Set up the final expression statement.
        VariableDeclarationStatement declarationStatement;

        // Handle initializing to a non-default value.
        if (match(TokenType.LEFT_ASSIGNMENT)) {
            BinaryOperatorCrystal operator = (BinaryOperatorCrystal) previous();
            Expression initializerExpression = expression();
            declarationStatement = new VariableDeclarationStatement(variableToDefine, declaredType, operator,
                    initializerExpression);
        }

        // Case for not specifying a default value.
        else {
            declarationStatement = new VariableDeclarationStatement(variableToDefine, declaredType, null, null);
        }

        // Define the crystal in the current scope. (For checking use of undefined crystals.)
        String identifierToDefine = variableToDefine.getIdentifier();
        try {
            currentEnvironment.define(identifierToDefine, variableToDefine);
            declarationStatement.setEnvironment(currentEnvironment);
        } catch (Vikari_FieldMemberExistsException e) {
            error(variableToDefine, "Variable is already defined.");
        }

        if (!isAtEndOfStatement()) {
            error(peek(), "Unexpected token(s) in variable declaration statement.");
        }

        // Synchronize after an error case.
        advanceToEndOfStatement();

        CoordinatePair location = variableToDefine.getCoordinates();
        declarationStatement.setLocation(location);
        return declarationStatement;
    }

    private Statement printStatement() {
        List<PrintExpression> printExpressions = new ArrayList<>();
        int currentLine = lineNumber;

        while (match(TokenType.TYPE_LABEL)) {
            AtonementCrystal typeLabel = previous();
            PrintStatementOperatorCrystal printOperator = new PrintStatementOperatorCrystal();
            printOperator.setCoordinates(typeLabel.getCoordinates());

            Expression expr = null;
            if (currentLine == lineNumber && !check(TokenType.TYPE_LABEL) && !isAtEnd() && !isAtEndOfStatement()) {
                expr = expression();
            }

            PrintExpression printExpression = new PrintExpression(printOperator, expr);
            printExpression.setLocation(printOperator.getCoordinates());
            printExpressions.add(printExpression);
        }

        PrintStatement printStatement = new PrintStatement(printExpressions);
        CoordinatePair location = printExpressions.get(0).getLocation();
        printStatement.setLocation(location);

        return printStatement;
    }

    private Statement expressionStatement() {
        Expression expression = expression();

        if (!isAtEndOfStatement()) {
            error(peek(), "Expected expression.");
        }

        ExpressionStatement expressionStatement = new ExpressionStatement(expression);
        expressionStatement.setLocation(expression.getLocation());

        // Synchronize after an error case.
        advanceToEndOfStatement();

        return expressionStatement;
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression left = orExpression();

        if (match(TokenType.LEFT_ASSIGNMENT)) {
            BinaryOperatorCrystal operator = (BinaryOperatorCrystal) previous();
            Expression right = assignment();

            if (!(left instanceof VariableExpression)) {
                error(left.getLocation(), "Invalid target for assignment expression.");
            }

            LeftAssignmentExpression assignmentExpression = new LeftAssignmentExpression(left, operator, right);
            return assignmentExpression;
        }

        if (match(TokenType.CONTINUE)) {
            BinaryOperatorCrystal operator = new RightAssignmentOperatorCrystal();
            operator.setCoordinates(previous().getCoordinates());

            Expression right = assignment();

            if (!(right instanceof VariableExpression)) {
                error(right.getLocation(), "Invalid target for assignment expression.");
            }

            RightAssignmentExpression assignmentExpression = new RightAssignmentExpression(left, operator, right);
            return assignmentExpression;
        }

        return left;
    }

    private Expression binaryOperatorExpression(Supplier<Expression> nextExpression,
                                                BinaryExpressionConstructor constructor, TokenType... tokenTypes) {
        Expression left = nextExpression.get();
        CoordinatePair location = left.getLocation();

        while (match(tokenTypes)) {
            BinaryOperatorCrystal operator = (BinaryOperatorCrystal) previous();
            Expression right = nextExpression.get();
            left = constructor.construct(left, operator, right);
            left.setLocation(location);
        }

        return left;
    }

    private Expression orExpression() {
        return binaryOperatorExpression(this::andExpression, BooleanLogicExpression::new, TokenType.LOGICAL_OR);
    }

    private Expression andExpression() {
        return binaryOperatorExpression(this::equalityExpression, BooleanLogicExpression::new, TokenType.LOGICAL_AND);
    }

    private Expression equalityExpression() {
        return binaryOperatorExpression(this::termExpression, BinaryExpression::new, TokenType.EQUALS,
                TokenType.NOT_EQUALS);
    }

    private Expression termExpression() {
        return binaryOperatorExpression(this::factorExpression, BinaryExpression::new, TokenType.ADD,
                TokenType.SUBTRACT);
    }

    private Expression factorExpression() {
        return binaryOperatorExpression(this::unaryExpression, BinaryExpression::new, TokenType.MULTIPLY,
                TokenType.LEFT_DIVIDE, TokenType.RIGHT_DIVIDE);
    }

    private Expression unaryExpression() {
        if (match(TokenType.SUBTRACT)) {
            AtonementCrystal previous = previous();
            NegateOperatorCrystal negateOperator = new NegateOperatorCrystal();
            negateOperator.setCoordinates(previous.getCoordinates());

            Expression right = unaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(negateOperator, right);
            unaryExpression.setLocation(negateOperator.getCoordinates());

            return unaryExpression;
        }

        if (match(TokenType.LOGICAL_NOT)) {
            LogicalNotOperatorCrystal notOperator = (LogicalNotOperatorCrystal) previous();

            Expression right = unaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(notOperator, right);
            unaryExpression.setLocation(notOperator.getCoordinates());

            return unaryExpression;
        }

        return primary();
    }

    private Expression primary() {
        // Literal value
        if (match(NumberCrystal.class, BooleanCrystal.class, NullKeywordCrystal.class)) {
            AtonementCrystal previous = previous();
            LiteralExpression literalExpression = new LiteralExpression(previous);
            literalExpression.setLocation(previous.getCoordinates());
            return literalExpression;
        }

        if (match(TokenType.SWORD)) {
            return nullLiteral();
        }

        if (match(TokenType.LEFT_SQUARE_BRACKET)) {
            return groupingExpression();
        }

        // Variable reference
        if (match(ReferenceCrystal.class)) {
            AtonementCrystal reference = previous();
            String identifier = reference.getIdentifier();
            if (!currentEnvironment.isDefined(identifier)) {
                reference.setType(VikariType.INVALID);
                error(reference, "Undefined variable reference.");
            } else {
                // Since assignments aren't processed yet, need to fetch the declared type.
                AtonementCrystal fieldMember = currentEnvironment.get(identifier);
                reference.setDeclaredType(fieldMember.getDeclaredType());
                reference.setField(fieldMember.getField());
            }
            return new VariableExpression(reference);
        }

        AtonementCrystal errorCrystal;
        if (isAtEndOfStatement() || isAtEnd()) {
            errorCrystal = previous();
        } else {
            errorCrystal = peek();
        }

        throw error(errorCrystal, "Expected expression.");
    }

    private Expression nullLiteral() {
        AtonementCrystal firstSword = previous();
        GroupingExpression groupingExpression = null;
        AtonementCrystal secondCrystal = null;

        if (match(TokenType.LEFT_SQUARE_BRACKET)) {
            groupingExpression = groupingExpression();
            secondCrystal = consume(TokenType.SWORD, "Expected second sword in null literal expression.");
        }

        CoordinatePair location = firstSword.getCoordinates();

        // NullLiteralExpression of the form: "__[n]__".
        if (groupingExpression != null && secondCrystal != null) {
            Expression innerExpression = groupingExpression.getExpression();
            NullLiteralExpression nullLiteralExpression = new NullLiteralExpression(innerExpression);
            nullLiteralExpression.setLocation(location);
            return nullLiteralExpression;
        }

        // Null expression consisting of a single sword: "__".
        else {
            String identifier = firstSword.getIdentifier();
            int length = identifier.length();
            NullCrystal nullCrystal = new NullCrystal(identifier, length);
            nullCrystal.setCoordinates(location);

            TypeCrystal nullType = TypeHierarchy.getNullTypeFor(VikariType.NULL);
            nullCrystal.setDeclaredType(nullType);
            nullCrystal.setInstantiatedType(nullType);

            LiteralExpression literalExpression = new LiteralExpression(nullCrystal);
            literalExpression.setLocation(location);
            return literalExpression;
        }
    }

    private GroupingExpression groupingExpression() {
        AtonementCrystal bracket = previous();
        CoordinatePair location = bracket.getCoordinates();

        Expression expression = expression();
        consume(TokenType.RIGHT_SQUARE_BRACKET, "Expected `]` after expression.");

        GroupingExpression groupingExpression = new GroupingExpression(expression);
        groupingExpression.setLocation(location);
        return groupingExpression;
    }

    private boolean isAtEndOfStatement(int tokenNumber) {
        return tokenNumber >= currentLine.size();
    }

    private boolean isAtEndOfStatement() {
        return tokenNumber >= currentLine.size();
    }

    private boolean isAtEnd(int tokenNumber) {
        return isAtEnd(lineNumber, tokenNumber);
    }

    private boolean isAtEnd(int lineNumber, int tokenNumber) {
        return lineNumber >= lineCount || (
                (lineNumber == lineCount - 1) &&
                        (tokenNumber >= lastLineLength));
    }

    private boolean isAtEnd() {
        return isAtEnd(lineNumber, tokenNumber);
    }

    private boolean match(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (check(tokenType)) {
                advance();
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    private boolean match(Class<? extends AtonementCrystal>... crystalTypes) {
        for (Class<? extends AtonementCrystal> crystalType : crystalTypes) {
            if (check(crystalType)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private AtonementCrystal lookAhead() {
        int position = tokenNumber + 1;
        if (!isAtEnd(position) && !isAtEndOfStatement(position)) {
            return currentLine.get(position);
        }
        return currentLine.get(tokenNumber);
    }

    private AtonementCrystal advance() {
        AtonementCrystal previous = peek();
        if (!isAtEnd()) {
            tokenNumber++;
        }
        return previous;
    }

    private void advanceToEndOfStatement() {
        while (tokenNumber < currentLine.size()) {
            advance();
        }
    }

    private void advanceToNextLine() {
        ++lineNumber;
        tokenNumber = 0;
        if (isAtEnd()) {
            currentLine = null;
        } else {
            currentLine = lexedStatements.get(lineNumber);
        }
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) {
            return false;
        }
        AtonementCrystal crystal = peek();
        return tokenType.getJavaType().isInstance(crystal);
    }

    private boolean check(Class<? extends AtonementCrystal> crystalType) {
        if (isAtEnd()) {
            return false;
        }
        AtonementCrystal crystal = peek();
        return crystalType.isInstance(crystal);
    }

    private AtonementCrystal peek() {
        if (tokenNumber >= currentLine.size()) {
            return null;
        }
        return lexedStatements.get(lineNumber).get(tokenNumber);
    }

    private AtonementCrystal previous() {
        TokenPosition position = new TokenPosition(lineNumber, tokenNumber);
        position = backup(position);

        int lineNumber = position.getLineNumber();
        int tokenNumber = position.getTokenNumber();
        return lexedStatements.get(lineNumber).get(tokenNumber);
    }

    private TokenPosition backup(TokenPosition position) {
        int lineNumber = position.getLineNumber();
        int tokenNumber = position.getTokenNumber();
        if (tokenNumber == 0) {
            lineNumber--;
            List<AtonementCrystal> prevStatement = lexedStatements.get(lineNumber);
            tokenNumber = prevStatement.size() - 1;
        } else {
            tokenNumber--;
        }
        return new TokenPosition(lineNumber, tokenNumber);
    }

    private AtonementCrystal consume(TokenType tokenType, String errorMessage) {
        return consume(tokenType.getJavaType(), errorMessage);
    }

    private <T extends AtonementCrystal> T consume(Class<T> crystalType, String errorMessage) {
        if (check(crystalType)) {
            return (T) advance();
        }
        AtonementCrystal errorCrystal;
        if (isAtEndOfStatement() || isAtEnd()) {
            AtonementCrystal previous = previous();
            CoordinatePair location = previous.getCoordinates();
            errorCrystal = new AtonementCrystal("");
            int row = location.getRow();
            int nextCol = location.getColumn() + previous.getIdentifier().length();
            errorCrystal.setCoordinates(new CoordinatePair(row, nextCol));
        } else {
            errorCrystal = peek();
        }
        throw error(errorCrystal, errorMessage);
    }

    private Vikari_ParserException error(AtonementCrystal crystal, String errorMessage) {
        CoordinatePair location = crystal.getCoordinates();
        return error(location, errorMessage);
    }

    private Vikari_ParserException error(CoordinatePair location, String errorMessage) {
        if (errorReportingEnabled) {
            SyntaxError syntaxError = new SyntaxError(currentFile, location, errorMessage);
            syntaxErrorReporter.add(syntaxError);
        }
        return new Vikari_ParserException(errorMessage);
    }

    private void synchronize() {
        advanceToEndOfStatement();
    }

    private List<AtonementCrystal> getLastVisitedLexedStatement() {
        List<AtonementCrystal> lastLine = currentLine;
        if (currentLine == null) {
            lastLine = lexedStatements.get(lexedStatements.size() - 1);
        }
        return lastLine;
    }
}
