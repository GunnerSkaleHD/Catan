package org.example.catan.gamepieces;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.ToString;
import org.example.catan.buildings.BuildingPlacement;
import org.example.catan.buildings.Buildings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class Player {

    private static final int MAX_SETTLEMENTS = 5;
    private static final int MAX_STREETS = 15;

    private final Color color;
    private final Map<Resources, Integer> inventory;
    private final List<BuildingPlacement> buildings;

    public Player(Color color) {
        this.color = color;
        this.inventory = new EnumMap<>(Resources.class);
        this.buildings = new ArrayList<>();
        initializeInventory();
        addResource(Resources.WOOD, 4);
        addResource(Resources.BRICK, 4);
        addResource(Resources.WHEAT, 2);
        addResource(Resources.SHEEP, 2);
        System.out.println("📦 Player inventory after init: " + inventory);


    }

    public int getVictoryPoints() {
        return (int) buildings.stream()
                .filter(b -> b.type() == Buildings.SETTLEMENT)
                .count();
    }


    private void initializeInventory() {
        for (Resources resource : Resources.values()) {
            if (resource != Resources.NONE) {
                inventory.put(resource, 0);
            }
        }
    }

    public int getResourceCount(Resources resource) {
        return inventory.getOrDefault(resource, 0);
    }

    public void addResource(Resources resource, int amount) {
        inventory.put(resource, getResourceCount(resource) + amount);
    }

    public boolean removeResource(Resources resource, int amount) {
        int current = getResourceCount(resource);
        if (current >= amount) {
            inventory.put(resource, current - amount);
            return true;
        }
        return false;
    }

    public Map<Resources, Integer> getInventorySnapshot() {
        return new EnumMap<>(inventory); // defensive copy
    }

    public boolean placeSettlement(int nodeId) {
        long current = buildings.stream()
                .filter(b -> b.type() == Buildings.SETTLEMENT)
                .count();

        if (current >= MAX_SETTLEMENTS) {
            System.out.println("❌ Max settlements reached.");
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

    public boolean placeStreet(int nodeA, int nodeB) {
        long current = buildings.stream()
                .filter(b -> b.type() == Buildings.STREET)
                .count();

        if (current >= MAX_STREETS) {
            System.out.println("❌ Max streets reached.");
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

    public boolean ownsSettlementAt(int nodeId) {
        return buildings.stream()
                .anyMatch(b -> b.type() == Buildings.SETTLEMENT && b.nodeA() == nodeId);
    }

    public boolean ownsStreetBetween(int nodeA, int nodeB) {
        return buildings.stream()
                .anyMatch(b -> b.type() == Buildings.STREET &&
                        ((b.nodeA() == nodeA && b.nodeB() == nodeB) || (b.nodeA() == nodeB && b.nodeB() == nodeA)));
    }
    public String getName() {
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.YELLOW)) return "Yellow";
        if (color.equals(Color.WHITE)) return "White";
        return "Unknown";
    }


}
