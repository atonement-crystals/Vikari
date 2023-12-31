package com.atonementcrystals.dnr.vikari.core.crystal.operator.assignment.bool;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The left logical OR assignment operator "<< offers a shorthand syntax for
 * performing a logical OR operation on the left and right operands, and then
 * assigning the result to the left operand.
 */
public class LeftLogicalOrAssignmentOperatorCrystal extends AtonementCrystal {

    public LeftLogicalOrAssignmentOperatorCrystal() {
        super(TokenType.LEFT_LOGICAL_OR_ASSIGNMENT.getIdentifier());
    }

}
