package org.example.catan.gamepieces;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.ToString;
import org.example.catan.gamepieces.buildings.BuildingPlacement;
import org.example.catan.gamepieces.buildings.Buildings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a player in the game "The Settlers of Catan".
 * Tracks the player's color, inventory of resources, buildings placed, and allows actions such as
 * placing settlements and streets, collecting and spending resources, and computing victory points.
 */
@Getter
@ToString
public class Player {

    private static final int MAX_SETTLEMENTS = 5;
    private static final int MAX_STREETS = 15;

    private final Color color;
    private final Map<Resources, Integer> inventory;
    private final List<BuildingPlacement> buildings;

    /**
     * Creates a new player with the given color and initializes their inventory.
     * Also provides a default starting set of resources.
     *
     * @param color the player's assigned color
     */
    public Player(Color color) {
        this.color = color;
        this.inventory = new EnumMap<>(Resources.class);
        this.buildings = new ArrayList<>();
        initializeInventory();
        addResource(Resources.WOOD, 4);
        addResource(Resources.BRICK, 4);
        addResource(Resources.WHEAT, 2);
        addResource(Resources.SHEEP, 2);
    }

    /**
     * Calculates the player's current victory points based on their buildings.
     *
     * @return total victory points from settlements
     */
    public int getVictoryPoints() {
        return (int) buildings.stream()
                .filter(b -> b.type() == Buildings.SETTLEMENT)
                .count();
    }

    /**
     * Initializes the player's inventory with 0 for all resource types except NONE.
     */
    private void initializeInventory() {
        for (Resources resource : Resources.values()) {
            if (resource != Resources.NONE) {
                inventory.put(resource, 0);
            }
        }
    }

    /**
     * Returns the amount of a specific resource the player has.
     *
     * @param resource the type of resource
     * @return the quantity of that resource
     */
    public int getResourceCount(Resources resource) {
        return inventory.getOrDefault(resource, 0);
    }

    /**
     * Adds a given amount of a resource to the player's inventory.
     *
     * @param resource the resource type
     * @param amount   the quantity to add
     */
    public void addResource(Resources resource, int amount) {
        inventory.put(resource, getResourceCount(resource) + amount);
    }

    /**
     * Attempts to remove a given amount of a resource from the player's inventory.
     *
     * @param resource the resource type
     * @param amount   the quantity to remove
     * @return true if removal succeeded, false otherwise
     */
    public boolean removeResource(Resources resource, int amount) {
        int current = getResourceCount(resource);
        if (current >= amount) {
            inventory.put(resource, current - amount);
            return true;
        }
        return false;
    }

    /**
     * Returns a copy of the player's inventory for safe access.
     *
     * @return a defensive copy of the resource inventory
     */
    public Map<Resources, Integer> getInventorySnapshot() {
        return new EnumMap<>(inventory);
    }

    /**
     * Attempts to place a settlement at the given node.
     * Requires wood, brick, wheat, and sheep.
     *
     * @param nodeId the node where the settlement is to be placed
     * @return true if placement was successful, false otherwise
     */
    public boolean placeSettlement(int nodeId) {
        long current = buildings.stream()
                .filter(b -> b.type() == Buildings.SETTLEMENT)
                .count();

        if (current >= MAX_SETTLEMENTS) {
            return false;
        }
        try {
            removeResource(Resources.WOOD, 1);
            removeResource(Resources.BRICK, 1);
            removeResource(Resources.WHEAT, 1);
            removeResource(Resources.SHEEP, 1);
        } catch (Exception e) {
            return false;
        }

        buildings.add(new BuildingPlacement(Buildings.SETTLEMENT, nodeId, null));
        return true;
    }

    /**
     * Attempts to place a street between two nodes.
     * Requires wood and brick.
     *
     * @param nodeA one end of the street
     * @param nodeB the other end of the street
     * @return true if placement was successful, false otherwise
     */
    public boolean placeStreet(int nodeA, int nodeB) {
        long current = buildings.stream()
                .filter(b -> b.type() == Buildings.STREET)
                .count();

        if (current >= MAX_STREETS) {
            return false;
        }
        try {
            removeResource(Resources.WOOD, 1);
            removeResource(Resources.BRICK, 1);
        } catch (Exception e) {
            return false;
        }

        buildings.add(new BuildingPlacement(Buildings.STREET, nodeA, nodeB));
        return true;
    }

    /**
     * Checks if the player owns a settlement at the specified node.
     *
     * @param nodeId the node to check
     * @return true if a settlement is present, false otherwise
     */
    public boolean ownsSettlementAt(int nodeId) {
        return buildings.stream()
                .anyMatch(b -> b.type() == Buildings.SETTLEMENT && b.nodeA() == nodeId);
    }

    /**
     * Returns the player's name based on their color.
     *
     * @return a string name for the player
     */
    public String getName() {
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.YELLOW)) return "Yellow";
        if (color.equals(Color.WHITE)) return "White";
        return "Unknown";
    }
}
