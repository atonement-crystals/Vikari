package com.atonementcrystals.dnr.vikari.core.crystal.operator.comparison;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The greater than operator > checks if the left operand is greater
 * than the right operand.
 */
public class GreaterThanOperatorCrystal extends AtonementCrystal {

    public GreaterThanOperatorCrystal() {
        super(TokenType.GREATER_THAN.getIdentifier());
    }

}
