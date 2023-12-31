package com.atonementcrystals.dnr.vikari.core.crystal.operator;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The variable arguments list operator ... denotes that any number
 * of arguments of the provided type may be sequentially passed to
 * that function's arguments list. These sequential arguments are
 * then rolled up into a list of that provided type.<br/>
 * <br/>
 * Multiple variable argument lists may be provided for the same
 * function signature. Given that each argument has a clearly
 * distinguishable type from an immediately preceding variable
 * argument list.
 */
public class VariableArgumentsListOperatorCrystal extends AtonementCrystal {

    public VariableArgumentsListOperatorCrystal() {
        super(TokenType.VARIABLE_ARGUMENTS_LIST.getIdentifier());
    }

}
