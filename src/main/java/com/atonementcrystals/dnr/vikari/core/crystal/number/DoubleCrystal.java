package com.atonementcrystals.dnr.vikari.core.crystal.number;

import com.atonementcrystals.dnr.vikari.core.crystal.identifier.VikariType;

public class DoubleCrystal extends NumberCrystal<Double> {
    public DoubleCrystal(String identifier, String value) {
        super(identifier, value);
        setType(VikariType.DOUBLE);
    }

    public DoubleCrystal(String identifier, Double value) {
        super(identifier, value);
        setType(VikariType.DOUBLE);
    }

    public DoubleCrystal(Double value) {
        this(value.toString(), value);
    }

    @Override
    public DoubleCrystal copy() {
        DoubleCrystal copy = new DoubleCrystal(getIdentifier(), getValue());
        copyFields(this, copy);
        return copy;
    }

    @Override
    public Double initialize(String value) {
        return Double.valueOf(value);
    }
}
