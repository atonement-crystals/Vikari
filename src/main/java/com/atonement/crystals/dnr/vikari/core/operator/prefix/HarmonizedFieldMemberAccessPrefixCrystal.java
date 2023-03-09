package com.atonement.crystals.dnr.vikari.core.operator.prefix;

import com.atonement.crystals.dnr.vikari.core.AtonementCrystal;
import com.atonement.crystals.dnr.vikari.core.identifier.DefaultIdentifierMapping;

public class HarmonizedFieldMemberAccessPrefixCrystal extends AtonementCrystal {

    public HarmonizedFieldMemberAccessPrefixCrystal() {
        super(DefaultIdentifierMapping.HARMONIZED_FIELD_MEMBER_ACCESS.getIdentifier());
    }
}
