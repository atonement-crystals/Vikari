package com.atonementcrystals.dnr.vikari.core.crystal.operator.assignment.bool;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The right logical OR assignment operator ">> offers a shorthand syntax for
 * performing a logical OR operation on the left and right operands, and then
 * assigning the result to the right operand.
 */
public class RightLogicalOrAssignmentOperatorCrystal extends AtonementCrystal {

    public RightLogicalOrAssignmentOperatorCrystal() {
        super(TokenType.RIGHT_LOGICAL_OR_ASSIGNMENT.getIdentifier());
    }

}
