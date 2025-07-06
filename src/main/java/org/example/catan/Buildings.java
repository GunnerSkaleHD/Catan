package org.example.catan;

public enum Buildings {
    STREET(0),
    SETTLEMENT(1);

    private final int victoryPoints;

    Buildings(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
