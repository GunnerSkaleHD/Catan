package org.example.catan.gamepieces.buildings;

/**
 * Represents the placement of a building in the game "The Settlers of Catan".
 *
 * A building can be placed on either a single node (for example, a settlement)
 * or between two nodes (for example, a road). The type defines the kind of
 * building, and the node values define its location on the game board.
 *
 * @param type  the type of building being placed (e.g., settlement, road, city)
 * @param nodeA the primary node ID associated with the building
 * @param nodeB the secondary node ID, or null for buildings that only use one node
 */
public record BuildingPlacement(Buildings type, int nodeA, Integer nodeB) {}
