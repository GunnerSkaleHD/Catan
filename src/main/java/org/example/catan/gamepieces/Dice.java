package org.example.catan.gamepieces;

import java.util.Random;

/**
 * Represents a set of dice used in the game "The Settlers of Catan".
 * Allows rolling a specified number of standard 6-sided dice.
 */
public class Dice {
    private final int amount;
    private final Random random;

    /**
     * Constructs a Dice object with the given number of dice.
     *
     * @param amount the number of 6-sided dice to roll
     */
    public Dice(int amount) {
        this.amount = amount;
        this.random = new Random();
    }

    /**
     * Rolls all dice and returns the total sum of their results.
     *
     * @return the sum of the dice rolls
     */
    public int rollDice() {
        int total = 0;
        for (int i = 0; i < amount; i++) {
            total += random.nextInt(6) + 1; // roll a die (1–6)
        }
        return total;
    }
}
