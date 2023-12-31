package com.atonementcrystals.dnr.vikari.core.crystal.identifier;

/**
 * Represents important keywords in Vikari that are reserved words for things
 * like special field regions and literal values.
 */
public enum Keyword {

    // regions
    PACKAGE("package"),
    IMPORT("import"),
    INHERITS("inherits"),
    PRIVATE("private"),
    PUBLIC("public"),
    DNR("dnr"),
    RUNTIME("runtime"),

    // literals
    TRUE("true"),
    FALSE("false"),
    NULL("null");

    /**
     * Create a new Keyword.
     * @param identifier The default string identifier.
     */
    Keyword(String identifier) {
        this.identifier = identifier;
    }

    private final String identifier;

    /**
     * @return The token's default string representation in Vikari.
     */
    public String getIdentifier() {
        return identifier;
    }
}
