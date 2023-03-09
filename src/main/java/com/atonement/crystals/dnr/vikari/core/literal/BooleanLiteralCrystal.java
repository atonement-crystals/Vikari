package com.atonement.crystals.dnr.vikari.core.literal;

import com.atonement.crystals.dnr.vikari.core.AtonementCrystal;

/**
 * Boolean literal identifiers are an explicit value of `true` or `false`.
 */
public class BooleanLiteralCrystal extends AtonementCrystal {

    private Boolean value;

    public BooleanLiteralCrystal(String identifier) {
        super(identifier);
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
