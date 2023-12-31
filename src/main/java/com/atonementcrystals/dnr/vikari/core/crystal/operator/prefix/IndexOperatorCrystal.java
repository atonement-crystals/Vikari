package com.atonementcrystals.dnr.vikari.core.crystal.operator.prefix;

import com.atonementcrystals.dnr.vikari.core.crystal.AtonementCrystal;
import com.atonementcrystals.dnr.vikari.core.crystal.identifier.TokenType;

/**
 * The index operator $ looks up a type member with an index value.
 * This index value can have one of two forms:
 * <ol>
 *     <li>
 *         A number:<br/>
 *         Numbers look up a type member in a crystal's field or
 *         region based on its index using the declaration order of
 *         type members in that type's field declaration.
 *     </li>
 *     <li>
 *         A string:<br/>
 *         Strings look up a type member in a crystal's field or
 *         region based on that type member's identifier name.
 *     </li>
 * </ol>
 * Either a literal value, a variable, or any expression within a
 * grouping [ ] can be passed as an argument to $. Default behavior
 * of $ can be changed by overriding the <code>get!()</code> and
 * <code>set!()</code> dnr methods. <i>(Either for the entire crystal,
 * or for a given region.)</i>
 */
public class IndexOperatorCrystal extends AtonementCrystal {

    public IndexOperatorCrystal() {
        super(TokenType.INDEX_OPERATOR.getIdentifier());
    }

}
