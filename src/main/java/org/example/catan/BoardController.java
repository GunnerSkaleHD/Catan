package org.example.catan;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.beans.value.ChangeListener;

import java.util.*;

public class BoardController {

    @FXML
    private Pane boardPane;

    //hexagon Radius
    private static final double HEX_SIZE = 50;

    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEX_HEIGHT_3_4 = (2 * HEX_SIZE) * 0.75;

    private static final int[] TILES_PER_ROW = {3, 4, 5, 4, 3};

    private final List<Polygon> allTiles = new ArrayList<>();

    private final List<Circle> vertexPoints = new ArrayList<>(); //Kreis für Hausplatzierung

    @FXML
    public void initialize() {
        createAndColorTiles();
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> positionTiles();
    
        //sizeListener für Fenstergrößte
        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);
        positionTiles();
    }    

     //Positionsberechnung durch Fenstergröße
    private void positionTiles() {
        double paneWidth = boardPane.getWidth();
        double paneHeight = boardPane.getHeight();

        //Zentierung mittleres feld
        double startX = (paneWidth / 2.0) - (HEX_WIDTH * 2);
        double startY = (paneHeight / 2.0) - (HEX_HEIGHT_3_4 * 2);

        int tileIndex = 0;

        for (int row = 0; row < TILES_PER_ROW.length; row++) {
            int numTilesInRow = TILES_PER_ROW[row];
            double rowOffsetX = (TILES_PER_ROW[2] - numTilesInRow) * HEX_WIDTH / 2.0;

            for (int col = 0; col < numTilesInRow; col++) {
                if (tileIndex >= allTiles.size()) break;

                double centerX = startX + rowOffsetX + col * HEX_WIDTH;
                double centerY = startY + row * HEX_HEIGHT_3_4;
                
                Polygon hexagon = allTiles.get(tileIndex);
                hexagon.setLayoutX(centerX);
                hexagon.setLayoutY(centerY);

                tileIndex++;
            }
        }
    }

     // Erstellung und Farbzuweisung
    private void createAndColorTiles() {
        for (int i = 0; i < 19; i++) {
            Polygon hexagon = createHexagon();
            allTiles.add(hexagon);
        }
        randomizeTileColors();
        boardPane.getChildren().addAll(allTiles);
    }

    private Polygon createHexagon() {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i + 30;
            double angle_rad = Math.PI / 180 * angle_deg;
            double pointX = HEX_SIZE * Math.cos(angle_rad);
            double pointY = HEX_SIZE * Math.sin(angle_rad);
            polygon.getPoints().addAll(pointX, pointY);
        }
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2.0);
        return polygon;
    }

    // Zufällige Farbzuweisung
    public void randomizeTileColors() {
        List<Color> resourceColors = new ArrayList<>(Arrays.asList(
                Color.FORESTGREEN, Color.FORESTGREEN, Color.FORESTGREEN, Color.FORESTGREEN,
                Color.web("#c7ad7f"), Color.web("#c7ad7f"), Color.web("#c7ad7f"), Color.web("#c7ad7f"),
                Color.GOLD, Color.GOLD, Color.GOLD, Color.GOLD,
                Color.FIREBRICK, Color.FIREBRICK, Color.FIREBRICK,
                Color.GRAY, Color.GRAY, Color.GRAY
        ));
        Collections.shuffle(resourceColors);

        // Wüste
        int desertIndex = 9;

        int colorIndex = 0;
        for (int i = 0; i < allTiles.size(); i++) {
            if (i == desertIndex) {
                allTiles.get(i).setFill(Color.SANDYBROWN);
            } else {
                if(colorIndex < resourceColors.size()) {
                    allTiles.get(i).setFill(resourceColors.get(colorIndex++));
                }
            }
        }
    }
}