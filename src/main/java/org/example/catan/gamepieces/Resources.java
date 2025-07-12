package org.example.catan.gamepieces;

/**
 * Represents the different types of resources available in the game
 * "The Settlers of Catan".
 *
 * These resources are used for building and trading. The {@code NONE} value
 * is used to represent the desert tile, which provides no resources.
 */
public enum Resources {
    /** Represents wood, typically obtained from forest tiles. */
    WOOD,

    /** Represents sheep, typically obtained from pasture tiles. */
    SHEEP,

    /** Represents wheat, typically obtained from field tiles. */
    WHEAT,

    /** Represents brick, typically obtained from hill tiles. */
    BRICK,

    /** Represents stone (ore), typically obtained from mountain tiles. */
    STONE,

    /** Represents no resource (used for desert tiles). */
    NONE;

    /**
     * Returns a human-readable name for the resource.
     *
     * @return the name of the resource
     */
    @Override
    public String toString() {
        switch (this) {
            case WOOD:
                return "Wood";
            case BRICK:
                return "Brick";
            case SHEEP:
                return "Sheep";
            case WHEAT:
                return "Wheat";
            case STONE:
                return "Stone";
            case NONE:
                return "None (Desert)";
            default:
                return super.toString();
        }
    }
}
