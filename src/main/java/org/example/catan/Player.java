//Dummy-Klasse
package org.example.catan;

import javafx.scene.paint.Color;

public class Player {
    private final String name;
    private final Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}