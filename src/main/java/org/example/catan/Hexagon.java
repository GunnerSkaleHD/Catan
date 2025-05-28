package org.example.catan;

import javafx.scene.shape.Polygon;

public class Hexagon extends Polygon {
    private static final double SIZE = 50;

    public Hexagon() {
        super(
                SIZE, 0,
                SIZE * 2, SIZE * 0.5,
                SIZE * 2, SIZE * 1.5,
                SIZE, SIZE * 2,
                0, SIZE * 1.5,
                0, SIZE * 0.5
        );
        setStroke(javafx.scene.paint.Color.BLACK);
        setStrokeWidth(2);
    }

    public void setTerrain(javafx.scene.paint.Paint fill) {
        setFill(fill);
    }
}
