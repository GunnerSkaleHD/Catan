package org.example.catan;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class Bank {
    private final Map<Resources, Integer> resourceStock;
    private int remainingRoads = 15 * 4;       // 15 per player (4 players max)
    private int remainingSettlements = 5 * 4;  // 5 per player

    public Bank() {
        resourceStock = new EnumMap<>(Resources.class);
        for (Resources res : Resources.values()) {
            if (res != Resources.NONE) resourceStock.put(res, 19);  // official Catan stock
        }
    }

    public boolean takeResource(Resources resource, int amount) {
        int current = resourceStock.getOrDefault(resource, 0);
        if (current >= amount) {
            resourceStock.put(resource, current - amount);
            return true;
        }
        return false;
    }

    public void giveResource(Resources resource, int amount) {
        resourceStock.put(resource, resourceStock.getOrDefault(resource, 0) + amount);
    }

    public boolean useSettlement() {
        if (remainingSettlements > 0) {
            remainingSettlements--;
            return true;
        }
        return false;
    }

    public boolean useStreet() {
        if (remainingRoads > 0) {
            remainingRoads--;
            return true;
        }
        return false;
    }
}
