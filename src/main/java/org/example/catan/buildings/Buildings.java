package org.example.catan.buildings;

/**
 * Enum representing different types of buildings in the game "The Settlers of Catan".
 *
 * Each building type has an associated number of victory points.
 */
public enum Buildings {
    /**
     * A road segment connecting two nodes.
     * Worth 0 victory points.
     */
    STREET(0),

    /**
     * A small settlement placed on a single node.
     * Worth 1 victory point.
     */
    SETTLEMENT(1);

    private final int victoryPoints;

    /**
     * Constructs a building type with the specified number of victory points.
     *
     * @param victoryPoints the number of victory points this building type provides
     */
    Buildings(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * Returns the number of victory points awarded by this building type.
     *
     * @return the victory point value
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
