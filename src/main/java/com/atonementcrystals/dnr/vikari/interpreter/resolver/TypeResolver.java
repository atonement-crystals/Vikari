package com.atonementcrystals.dnr.vikari.interpreter.resolver;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.NullTypeCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.TypeCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.TypeHierarchy;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.VikariType;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.BinaryOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.UnaryOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.comparison.EqualsOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.comparison.NotEqualsOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.logical.LogicalNotOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.AddOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.LeftDivideOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.MultiplyOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.NegateOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.RightDivideOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.operator.math.SubtractOperatorCrystal;
import com.atonementcrystals.dnr.vikari.core.expression.BinaryExpression;
import com.atonementcrystals.dnr.vikari.core.expression.BooleanLogicExpression;
import com.atonementcrystals.dnr.vikari.core.expression.Expression;
import com.atonementcrystals.dnr.vikari.core.expression.GroupingExpression;
import com.atonementcrystals.dnr.vikari.core.expression.LeftAssignmentExpression;
import com.atonementcrystals.dnr.vikari.core.expression.LiteralExpression;
import com.atonementcrystals.dnr.vikari.core.expression.NullLiteralExpression;
import com.atonementcrystals.dnr.vikari.core.expression.PrintExpression;
import com.atonementcrystals.dnr.vikari.core.expression.RightAssignmentExpression;
import com.atonementcrystals.dnr.vikari.core.expression.UnaryExpression;
import com.atonementcrystals.dnr.vikari.core.expression.VariableExpression;
import com.atonementcrystals.dnr.vikari.core.statement.ExpressionStatement;
import com.atonementcrystals.dnr.vikari.core.statement.PrintStatement;
import com.atonementcrystals.dnr.vikari.core.statement.SyntaxErrorStatement;
import com.atonementcrystals.dnr.vikari.core.statement.VariableDeclarationStatement;
import com.atonementcrystals.dnr.vikari.util.CoordinatePair;
import com.atonementcrystals.dnr.vikari.util.Utils;

import java.util.List;
import java.util.Set;

/**
 * Walks the AST and resolves what is the return type for a statement and each sub-expression in the statement.
 */
public class TypeResolver extends Resolver<TypeCrystal> {
    /** Need a concrete, non-null TypeCrystal instance for type resolution errors. */
    private static final TypeCrystal INVALID_TYPE = VikariType.INVALID.getTypeCrystal();

    /** The set of all basic arithmetic operators handled by the same type resolving logic rules. */
    private static final Set<Class<? extends BinaryOperatorCrystal>> ARITHMETIC_OPERATORS = Set.of(
            AddOperatorCrystal.class, SubtractOperatorCrystal.class, MultiplyOperatorCrystal.class,
            LeftDivideOperatorCrystal.class, RightDivideOperatorCrystal.class);

    /** The list of all numeric types from highest to lowest order regarding casting rules. */
    private static final List<VikariType> NUMERIC_TYPES = List.of(VikariType.BIG_DECIMAL, VikariType.DOUBLE,
            VikariType.FLOAT, VikariType.BIG_INTEGER, VikariType.LONG, VikariType.INTEGER);

    @Override
    public TypeCrystal visit(BinaryExpression expr) {
        TypeCrystal leftType = expr.getLeft().accept(this);
        TypeCrystal rightType = expr.getRight().accept(this);

        BinaryOperatorCrystal operator = expr.getOperator();
        if (isArithmeticOperator(operator)) {
            CoordinatePair leftLocation = expr.getLeft().getLocation();
            CoordinatePair rightLocation = expr.getRight().getLocation();
            return resolveArithmeticExpression(leftType, rightType, leftLocation, rightLocation);
        } else if (isEqualityOperator(operator)) {
            return VikariType.BOOLEAN.getTypeCrystal();
        }
        throw new IllegalStateException("Unimplemented operator: " + Utils.getSimpleClassName(operator));
    }

    public boolean isArithmeticOperator(BinaryOperatorCrystal operator) {
        return ARITHMETIC_OPERATORS.contains(operator.getClass());
    }

    public TypeCrystal resolveArithmeticExpression(TypeCrystal left, TypeCrystal right, CoordinatePair leftLocation,
                                                      CoordinatePair rightLocation) {
        boolean invalidOperands = false;
        // Prevent cascading type errors for multiple expressions of the same statement.
        if (left == INVALID_TYPE || right == INVALID_TYPE) {
            invalidOperands = true;
        }
        if (left != INVALID_TYPE && (left == null || !left.hasType(VikariType.NUMBER))) {
            error(leftLocation, "Arithmetic expression expects a Number for operands.");
            invalidOperands = true;
        }
        if (right != INVALID_TYPE && (right == null || !right.hasType(VikariType.NUMBER))) {
            error(rightLocation, "Arithmetic expression expects a Number for operands.");
            invalidOperands = true;
        }
        if (invalidOperands) {
            return INVALID_TYPE;
        }

        // NUMERIC_TYPES is ordered such that type upcasting is automatically handled.
        for (VikariType vikariType : NUMERIC_TYPES) {
            if (left.hasType(vikariType) || right.hasType(vikariType)) {
                TypeCrystal typeCrystal = vikariType.getTypeCrystal();
                return typeCrystal;
            }
        }
        throw new IllegalStateException("Bad internal state. Unimplemented Numeric type.");
    }

    public boolean isEqualityOperator(BinaryOperatorCrystal operator) {
        return operator instanceof EqualsOperatorCrystal || operator instanceof NotEqualsOperatorCrystal;
    }

    @Override
    public TypeCrystal visit(BooleanLogicExpression expr) {
        TypeCrystal leftType = expr.getLeft().accept(this);
        TypeCrystal rightType = expr.getRight().accept(this);

        boolean invalidOperands = false;
        if (leftType != INVALID_TYPE && !leftType.hasType(VikariType.BOOLEAN)) {
            CoordinatePair leftLocation = expr.getLeft().getLocation();
            error(leftLocation, "Boolean logic expression expects a Boolean for operands.");
            invalidOperands = true;
        }
        if (rightType != INVALID_TYPE && !rightType.hasType(VikariType.BOOLEAN)) {
            CoordinatePair rightLocation = expr.getRight().getLocation();
            error(rightLocation, "Boolean logic expression expects a Boolean for operands.");
            invalidOperands = true;
        }
        if (invalidOperands) {
            return INVALID_TYPE;
        }

        return VikariType.BOOLEAN.getTypeCrystal();
    }

    @Override
    public TypeCrystal visit(GroupingExpression expr) {
        return expr.getExpression().accept(this);
    }

    @Override
    public TypeCrystal visit(LiteralExpression expr) {
        AtonementCrystal literal = expr.getValue();
        TypeCrystal typeCrystal = literal.getInstantiatedType();
        return typeCrystal;
    }

    @Override
    public TypeCrystal visit(PrintExpression expr) {
        // TODO: Change implementation when print expressions return a String.
        return null;
    }

    @Override
    public TypeCrystal visit(UnaryExpression expr) {
        UnaryOperatorCrystal operator = expr.getOperator();
        TypeCrystal operandType = expr.getOperand().accept(this);

        // Don't report a cascade of errors for a single invalid type instance.
        if (operandType == INVALID_TYPE) {
            return INVALID_TYPE;
        }

        if (operator instanceof NegateOperatorCrystal) {
            if (!operandType.hasType(VikariType.NUMBER)) {
                CoordinatePair operandLocation = expr.getOperand().getLocation();
                error(operandLocation, "Negate expression expects a Number as its operand.");
                return INVALID_TYPE;
            }
            return operandType;
        } else if (operator instanceof LogicalNotOperatorCrystal) {
            if (!operandType.hasType(VikariType.BOOLEAN)) {
                CoordinatePair operandLocation = expr.getOperand().getLocation();
                error(operandLocation, "Logical not expression expects a Boolean as its operand.");
                return INVALID_TYPE;
            }
            return VikariType.BOOLEAN.getTypeCrystal();
        }
        throw new IllegalStateException("Unimplemented operator: " + Utils.getSimpleClassName(operator));
    }

    @Override
    public TypeCrystal visit(VariableExpression expr) {
        AtonementCrystal reference = expr.getReference();

        // Undeclared variable reference.
        if (reference.getDeclaredType() == null) {
            return INVALID_TYPE;
        }

        TypeCrystal typeCrystal = reference.getInstantiatedType();
        return typeCrystal;
    }

    @Override
    public TypeCrystal visit(VariableDeclarationStatement stmt) {
        AtonementCrystal declaredVariable = stmt.getDeclaredVariable();
        TypeCrystal declaredType = declaredVariable.getDeclaredType();

        // Optionally resolve the initializer expression.
        Expression initializerExpression = stmt.getInitializerExpression();
        if (initializerExpression != null) {
            TypeCrystal initializerType = initializerExpression.accept(this);

            if (initializerType instanceof NullTypeCrystal initializerNullType) {
                return handleNullType(declaredVariable, declaredType, initializerNullType);
            } else if (initializerType.hasType(declaredType) || allowNumericAssignment(declaredType, initializerType)) {
                declaredVariable.setInstantiatedType(initializerType);
            } else {
                declaredVariable.setInstantiatedType(INVALID_TYPE);
                if (initializerType != INVALID_TYPE) {
                    assignmentError(declaredVariable.getCoordinates(), declaredType, initializerType);
                }
            }

            return initializerType;
        } else {
            TypeCrystal nullType = TypeHierarchy.getNullTypeFor(declaredType);
            declaredVariable.setInstantiatedType(nullType);
        }

        return declaredType;
    }

    @Override
    public TypeCrystal visit(LeftAssignmentExpression expr) {
        TypeCrystal rvalueType = expr.getRvalue().accept(this);
        Expression lvalueExpr = expr.getLvalue();

        AtonementCrystal lvalue;
        TypeCrystal lvalueDeclaredType;

        if (lvalueExpr instanceof VariableExpression) {
            lvalue = ((VariableExpression) lvalueExpr).getReference();
            lvalueDeclaredType = lvalue.getDeclaredType();
        } else {
            return expr.getLvalue().accept(this);
        }

        if (lvalue.getDeclaredType() == INVALID_TYPE) {
            return INVALID_TYPE;
        } else if (rvalueType instanceof NullTypeCrystal rvalueNullType) {
            return handleNullType(lvalue, lvalueDeclaredType, rvalueNullType);
        } else if (rvalueType.hasType(lvalueDeclaredType) || allowNumericAssignment(lvalueDeclaredType, rvalueType)) {
            lvalue.setInstantiatedType(rvalueType);
        } else {
            lvalue.setInstantiatedType(INVALID_TYPE);
            if (rvalueType != INVALID_TYPE) {
                assignmentError(expr.getLvalue().getLocation(), lvalueDeclaredType, rvalueType);
            }
        }

        return rvalueType;
    }

    @Override
    public TypeCrystal visit(RightAssignmentExpression expr) {
        TypeCrystal rvalueType = expr.getRvalue().accept(this);
        Expression lvalueExpr = expr.getLvalue();

        AtonementCrystal lvalue;
        TypeCrystal lvalueDeclaredType;

        if (lvalueExpr instanceof VariableExpression) {
            lvalue = ((VariableExpression) lvalueExpr).getReference();
            lvalueDeclaredType = lvalue.getDeclaredType();
        } else {
            return expr.getLvalue().accept(this);
        }

        if (lvalue.getDeclaredType() == INVALID_TYPE) {
            return INVALID_TYPE;
        } else if (rvalueType instanceof NullTypeCrystal rvalueNullType) {
            return handleNullType(lvalue, lvalueDeclaredType, rvalueNullType);
        } else if (rvalueType.hasType(lvalueDeclaredType) || allowNumericAssignment(lvalueDeclaredType, rvalueType)) {
            lvalue.setInstantiatedType(rvalueType);
        } else {
            lvalue.setInstantiatedType(INVALID_TYPE);
            if (rvalueType != INVALID_TYPE) {
                assignmentError(expr.getLvalue().getLocation(), lvalueDeclaredType, rvalueType);
            }
        }

        return rvalueType;
    }

    /**
     * Handles a NULL rvalue type in assignment expressions and variable declaration statements.
     * @param lvalue The target being assigned to.
     * @param lvalueDeclaredType The declared type of the assignment target.
     * @param rvalueNullType The specific NULL instantiated type of the value being assigned.
     * @return The proper NULL instantiated type for the assignment target, if the assignment is
     *         valid. Otherwise, this returns rvalueNullType.
     */
    private TypeCrystal handleNullType(AtonementCrystal lvalue, TypeCrystal lvalueDeclaredType,
                                NullTypeCrystal rvalueNullType) {
        TypeCrystal rvalueParentType = rvalueNullType.getParent();
        if (rvalueParentType.hasType(lvalueDeclaredType) || rvalueParentType == VikariType.NULL.getTypeCrystal()) {
            NullTypeCrystal lvalueNullType = TypeHierarchy.getNullTypeFor(lvalueDeclaredType);
            lvalue.setInstantiatedType(lvalueNullType);
            return lvalueNullType;
        } else {
            assignmentError(lvalue.getCoordinates(), lvalueDeclaredType, rvalueParentType);
            return rvalueNullType;
        }
    }

    /**
     * Checks if both types are Numbers. (Any Number can be assigned to any Number variable.)
     * @param declaredType The type of the assignment target variable.
     * @param initializerType The type of the initializer expression.
     * @return True if both types are Numbers, else false.
     */
    public boolean allowNumericAssignment(TypeCrystal declaredType, TypeCrystal initializerType) {
        return declaredType.hasType(VikariType.NUMBER) && initializerType.hasType(VikariType.NUMBER);
    }

    @Override
    public TypeCrystal visit(NullLiteralExpression expr) {
        Expression innerExpression = expr.getExpression();
        TypeCrystal innerExpressionType = innerExpression.accept(this);

        if (!innerExpressionType.hasType(VikariType.NUMBER)) {
            CoordinatePair errorLocation = innerExpression.getLocation();
            error(errorLocation, "Null literal expression expects a Number as its operand.");
        }

        return TypeHierarchy.getNullTypeFor(VikariType.NULL);
    }

    @Override
    public TypeCrystal visit(PrintStatement stmt) {
        for (PrintExpression printExpression : stmt.getPrintExpressions()) {
            printExpression.accept(this);
        }
        // TODO: Change implementation when print expressions return a String.
        return null;
    }

    @Override
    public TypeCrystal visit(ExpressionStatement stmt) {
        return stmt.getExpression().accept(this);
    }

    @Override
    public TypeCrystal visit(SyntaxErrorStatement stmt) {
        // Ignore as this contains no expressions or crystals to resolve.
        return null;
    }

    public void assignmentError(CoordinatePair location, TypeCrystal declaredType, TypeCrystal instantiatedType) {
        error(location, "Variable with type " + declaredType + " cannot be assigned a value of type " +
                instantiatedType + ".");
    }
}
