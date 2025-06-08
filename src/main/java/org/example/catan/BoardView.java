package org.example.catan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BoardView {
    //hexagon Größen
    private static final double HEX_SIZE = 50;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEX_HEIGHT_3_4 = (2 * HEX_SIZE) * 0.75;

    private static final int[] TILES_PER_ROW = {3, 4, 5, 4, 3};

    //Listen und Maps für Spielelemente
    private final List<Polygon> allTiles = new ArrayList<>();
    private final List<Circle> vertexPoints = new ArrayList<>(); //Kreis für Siedlungsplatzierung
    private final Map<Circle, Rectangle> placedSettlements = new HashMap<>(); //Map für platzierte Siedlungen

    private Consumer<Circle> onVertexClickCallback;

    private final Pane boardPane;

    public BoardView(Pane boardPane) {
        this.boardPane = boardPane;
    }

    //Siedlungsplätze initialisieren an Ecken
    public void initializeVertexPoints() {
        for (int i = 0; i < 54; i++) {
            Circle vertex = new Circle(8);
            vertex.setFill(Color.ORANGERED.deriveColor(1, 1, 1, 0.5));
            vertex.setStroke(Color.RED);
            vertex.setOnMouseClicked(event -> {
            if (onVertexClickCallback != null) {
                onVertexClickCallback.accept(vertex);
            }});
            vertexPoints.add(vertex); // Füge den Kreis zur Liste hinzu
        }
        boardPane.getChildren().addAll(vertexPoints); // Füge alle Kreise zur Anzeige hinzu
    }

    //Gesamte Positionierung
    public void positionVisuals() {
        double paneWidth = boardPane.getWidth();
        double paneHeight = boardPane.getHeight();

        //Zentrierung mittleres feld
        double startX = (paneWidth / 2.0) - (HEX_WIDTH * 2);
        double startY = (paneHeight / 2.0) - (HEX_HEIGHT_3_4 * 2);

        int tileIndex = 0;

        //Positionierung der Hexagone
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

        //Positionierung der Kreise (Siedlungsplätze)
        List<Point2D> uniqueVertexCoordinates = new ArrayList<>();
        Map<String, Point2D> uniqueVerticesMap = new LinkedHashMap<>();

        //Berechnung und Speicherung Koordinaten
        for (Polygon hexagon : allTiles) {
            for (int i = 0; i < 6; i++) {
                double x = hexagon.getLayoutX() + hexagon.getPoints().get(i * 2);
                double y = hexagon.getLayoutY() + hexagon.getPoints().get(i * 2 + 1);
                String key = Math.round(x) + ":" + Math.round(y);

                if (!uniqueVerticesMap.containsKey(key)) {
                    uniqueVerticesMap.put(key, new Point2D(x, y));
                }
            }
        }
        uniqueVertexCoordinates.addAll(uniqueVerticesMap.values());

        //Positioniere die existierenden Kreise auf den neu berechneten Koordinaten
        for (int i = 0; i < uniqueVertexCoordinates.size(); i++) {
            if (i < vertexPoints.size()) {
                Circle vertex = vertexPoints.get(i);
                Point2D pos = uniqueVertexCoordinates.get(i);
                vertex.setCenterX(pos.getX());
                vertex.setCenterY(pos.getY());
            }
        }

        //Positioniere die bereits platzierten Häuser neu
        for (Map.Entry<Circle, Rectangle> entry : placedSettlements.entrySet()) {
            Circle vertex = entry.getKey();
            Rectangle house = entry.getValue();

            // Neupositionierung & Zentrierung
            house.setLayoutX(vertex.getCenterX() - house.getWidth() / 2);
            house.setLayoutY(vertex.getCenterY() - house.getHeight() / 2);
        }
    }

    // Erstellung und Farbzuweisung aller felder
    public void createAndColorTiles() {
        for (int i = 0; i < 19; i++) {
            Polygon hexagon = createHexagon();
            allTiles.add(hexagon);
        }
        randomizeTileColors();
        boardPane.getChildren().addAll(allTiles);
    }

    // Erstellung eines Hexagons mit 6 Eckpunkten
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
                allTiles.get(i).setFill(Color.SANDYBROWN); //Wüste
            } else {
                if(colorIndex < resourceColors.size()) {
                    allTiles.get(i).setFill(resourceColors.get(colorIndex++));
                }
            }
        }
    }

    //Siedlung zeichnen
    public void drawSettlement(Circle clickedVertex, Color playerColor){
        Rectangle house = new Rectangle(16, 16);
        house.setLayoutX(clickedVertex.getCenterX() - house.getWidth() / 2);
        house.setLayoutY(clickedVertex.getCenterY() - house.getHeight() / 2);
        
        house.setFill(playerColor);
        house.setStroke(Color.BLACK);
        boardPane.getChildren().add(house);
        
        placedSettlements.put(clickedVertex, house); 
        
        clickedVertex.setVisible(false);
    }

    //Klick-Handler für Siedlungsplätze
    public void setOnVertexClickHandler(Consumer<Circle> callback) {
        this.onVertexClickCallback = callback;
    }

}
