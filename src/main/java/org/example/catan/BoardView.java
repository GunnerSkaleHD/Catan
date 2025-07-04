package org.example.catan;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.example.catan.Graph.HexTile;
import org.example.catan.Graph.IntTupel;
import org.example.catan.Graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BoardView {
    private static final double HEX_SIZE = 50;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double[] CORNER_ANGLES_DEG = {-90, -30, 30, 90, 150, 210};

    private final Pane boardPane;

    private final Map<Integer, Circle> nodeCircles = new HashMap<>();
    private final Map<Circle, Rectangle> placedSettlements = new HashMap<>();
    private final List<Polygon> allTiles = new ArrayList<>();

    private Consumer<Circle> onVertexClickCallback;

    public BoardView(Pane boardPane, CatanBoard catanBoard) {
        this.boardPane = boardPane;
        loadBoardFromModel(catanBoard);
    }

    public void loadBoardFromModel(CatanBoard catanBoard) {
        for (Map.Entry<IntTupel, HexTile> entry : catanBoard.getBoard().entrySet()) {
            IntTupel coord = entry.getKey();
            HexTile tile = entry.getValue();

            // Axial to pixel
            double paneWidth = boardPane.getWidth();
            double paneHeight = boardPane.getHeight();

            // Center offset based on board size (assume radius ~2)
            double offsetX = paneWidth / 2.0;
            double offsetY = paneHeight / 2.0;

            double centerX = offsetX + HEX_SIZE * Math.sqrt(3) * (coord.q() + coord.r() / 2.0);
            double centerY = offsetY + HEX_SIZE * 1.5 * coord.r();


            Polygon hex = createHexagon(centerX, centerY);
            hex.setFill(resourceToColor(tile.getResourceType()));
            allTiles.add(hex);
            boardPane.getChildren().add(hex);

            for (int i = 0; i < tile.getHexTileNodes().length; i++) {
                Node node = tile.getHexTileNodes()[i];
                if (!nodeCircles.containsKey(node.getId())) {
                    double angle_deg = CORNER_ANGLES_DEG[i];
                    double angle_rad = Math.toRadians(angle_deg);

                    double vx = centerX + HEX_SIZE * Math.cos(angle_rad);
                    double vy = centerY + HEX_SIZE * Math.sin(angle_rad);

                    Circle vertex = new Circle(8);
                    vertex.setFill(Color.ORANGERED.deriveColor(1, 1, 1, 0.5));
                    vertex.setStroke(Color.RED);
                    vertex.setCenterX(vx);
                    vertex.setCenterY(vy);
                    vertex.setUserData(node);

                    vertex.setOnMouseClicked(event -> {
                        if (onVertexClickCallback != null) {
                            onVertexClickCallback.accept(vertex);
                        }
                    });

                    nodeCircles.put(node.getId(), vertex);
                    boardPane.getChildren().add(vertex);
                }
            }

        }

        // Reposition already placed settlements
        for (Map.Entry<Circle, Rectangle> entry : placedSettlements.entrySet()) {
            Circle vertex = entry.getKey();
            Rectangle house = entry.getValue();
            house.setLayoutX(vertex.getCenterX() - house.getWidth() / 2);
            house.setLayoutY(vertex.getCenterY() - house.getHeight() / 2);
        }
    }

    private Polygon createHexagon(double centerX, double centerY) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i + 30;
            double angle_rad = Math.PI / 180 * angle_deg;
            double x = centerX + HEX_SIZE * Math.cos(angle_rad);
            double y = centerY + HEX_SIZE * Math.sin(angle_rad);
            hex.getPoints().addAll(x, y);
        }
        hex.setStroke(Color.BLACK);
        hex.setStrokeWidth(2);
        return hex;
    }

    private Color resourceToColor(Resources resource) {
        return switch (resource) {
            case WOOD -> Color.FORESTGREEN;
            case WHEAT -> Color.web("#c7ad7f");
            case SHEEP -> Color.GOLD;
            case BRICK -> Color.FIREBRICK;
            case STONE -> Color.GRAY;
            case NONE -> Color.SANDYBROWN;
            default -> Color.LIGHTGRAY;
        };
    }

    public void drawSettlement(Circle clickedVertex, Color playerColor) {
        Rectangle house = new Rectangle(16, 16);
        house.setFill(playerColor);
        house.setStroke(Color.BLACK);
        house.setLayoutX(clickedVertex.getCenterX() - house.getWidth() / 2);
        house.setLayoutY(clickedVertex.getCenterY() - house.getHeight() / 2);
        boardPane.getChildren().add(house);

        placedSettlements.put(clickedVertex, house);
        clickedVertex.setVisible(false);
    }

    public void setOnVertexClickHandler(Consumer<Circle> callback) {
        this.onVertexClickCallback = callback;
    }
}
