package com.atonementcrystals.dnr.vikari.core.crystal.literal;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;

/**
 * A multi-line string literal is defined across more than one line.
 */
public class MultiLineStringLiteralCrystal extends AtonementCrystal {

    private String string;
    private MultiLineStringLiteralCrystal next;

    public MultiLineStringLiteralCrystal(String identifier) {
        super(identifier);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public MultiLineStringLiteralCrystal getNext() {
        return next;
    }

    public void setNext(MultiLineStringLiteralCrystal next) {
        this.next = next;
    }
}