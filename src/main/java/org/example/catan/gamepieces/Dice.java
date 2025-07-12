package org.example.catan.gamepieces;

import java.util.Random;

public class Dice {
    private final int amount;
    private final Random random;

    public Dice(int amount) {
        this.amount = amount;
        this.random = new Random();
    }

    public int rollDice() {
        int total = 0;
        for (int i = 0; i < amount; i++) {
            total += random.nextInt(6) + 1; // roll a die (1–6)
        }
        return total;
    }
}
