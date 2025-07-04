package org.example.catan;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Player {
    protected Color color;
    protected Map<String, Integer> resources;
    protected List<Development> developmentCards;

    public Player(Color color) {
        this.color = color;
        this.resources = new HashMap<>();
        this.developmentCards = new ArrayList<>();

        resources.put("wood", 0);
        resources.put("wheat", 0);
        resources.put("sheep", 0);
        resources.put("stone", 0);
        resources.put("bricks", 0);
    }

    public Color getColor() {
        return color;
    }

    public boolean hasResource(String type, int amount) {
        return resources.getOrDefault(type.toLowerCase(), 0) >= amount;
    }

    public boolean removeResource(String type, int amount) {
        type = type.toLowerCase();
        if (hasResource(type, amount)) {
            resources.put(type, resources.get(type) - amount);
            return true;
        }
        return false;
    }

    public void addResource(String type, int amount) {
        type = type.toLowerCase();
        resources.put(type, resources.getOrDefault(type, 0) + amount);
    }

    public void addDevelopmentCard(Development card) {
        developmentCards.add(card);
    }

    public abstract void buySettlement(Bank bank);

    public abstract void buyCity(Bank bank);

    public abstract void buyRoad(Bank bank);

    public abstract void buyCard(Bank bank);

    public abstract void trade();


    // Just testing
    public void playerStatus() {
        System.out.println("Player color: " + color);
        System.out.println("Player resources: " + resources);
        System.out.println("Player development cards: " + developmentCards);
    }

}

