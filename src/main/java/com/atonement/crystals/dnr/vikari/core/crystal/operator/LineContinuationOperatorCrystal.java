package com.atonement.crystals.dnr.vikari.core.crystal.operator;

import com.atonement.crystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonement.crystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The line continuation operator ~ allows for individual statements to be
 * defined across multiple lines.
 */
public class LineContinuationOperatorCrystal  extends AtonementCrystal {

    public LineContinuationOperatorCrystal() {
        super(TokenType.LINE_CONTINUATION.getIdentifier());
    }

}