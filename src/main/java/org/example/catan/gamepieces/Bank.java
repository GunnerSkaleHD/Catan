package org.example.catan.gamepieces;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the central bank in the game of Catan.
 * The bank holds all available resources and tracks
 * the remaining roads and settlements that can be placed.
 */
@Getter
public class Bank {
    /** Stores the quantity of each resource available in the bank. */
    private final Map<Resources, Integer> resourceStock;

    /** Total number of roads remaining in the game (shared across all players). */
    private int remainingRoads = 15 * 4;

    /** Total number of settlements remaining in the game (shared across all players). */
    private int remainingSettlements = 5 * 4;

    /**
     * Initializes the bank with the default number of resources (19 each, except NONE).
     */
    public Bank() {
        resourceStock = new EnumMap<>(Resources.class);
        for (Resources res : Resources.values()) {
            if (res != Resources.NONE) {
                resourceStock.put(res, 19);
            }
        }
    }

    /**
     * Attempts to take the specified amount of a resource from the bank.
     *
     * @param resource the resource to take
     * @param amount   the amount to take
     * @return true if the bank had enough and the amount was deducted, false otherwise
     */
    public boolean takeResource(Resources resource, int amount) {
        int current = resourceStock.getOrDefault(resource, 0);
        if (current >= amount) {
            resourceStock.put(resource, current - amount);
            return true;
        }
        return false;
    }

    /**
     * Attempts to use one available settlement piece.
     *
     * @return true if a settlement was available and used, false otherwise
     */
    public boolean useSettlement() {
        if (remainingSettlements > 0) {
            remainingSettlements--;
            return true;
        }
        return false;
    }

    /**
     * Attempts to use one available road piece.
     *
     * @return true if a road was available and used, false otherwise
     */
    public boolean useStreet() {
        if (remainingRoads > 0) {
            remainingRoads--;
            return true;
        }
        return false;
    }
}
