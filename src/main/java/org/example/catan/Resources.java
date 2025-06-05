package org.example.catan;

public enum Resources {
    WOOD,
    BRICK,
    SHEEP,
    WHEAT,
    STONE,
    NONE; // represents the desert (no resource)

    @Override
    public String toString() {
        switch (this) {
            case WOOD: return "Wood";
            case BRICK: return "Brick";
            case SHEEP: return "Sheep";
            case WHEAT: return "Wheat";
            case STONE: return "Stone";
            case NONE: return "None (Desert)";
            default: return super.toString();
        }
    }
}
