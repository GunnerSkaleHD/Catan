package org.example.catan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import org.example.catan.Graph.HexTile;
import org.example.catan.Graph.IntTupel;
import org.example.catan.Graph.Node;

public class BoardView {
    // Hexagon dimensions
    private static final double HEX_SIZE = 50;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double HEX_HEIGHT = 2 * HEX_SIZE;

    // Game elements
    private final List<Polygon> allTiles = new ArrayList<>();
    private final List<Circle> vertexPoints = new ArrayList<>();
    private final List<Text> diceNumbers = new ArrayList<>();
    private final Map<Circle, Rectangle> placedSettlements = new HashMap<>();
    private final Map<Resources, Color> resourceColors = new HashMap<>();

    // Board logic integration
    private CatanBoard catanBoard;
    private Map<IntTupel, Polygon> hexagonMap = new HashMap<>();
    private Map<Node, Circle> nodeCircleMap = new HashMap<>();

    private Consumer<Circle> onVertexClickCallback;
    private final Pane boardPane;

    public BoardView(Pane boardPane) {
        this.boardPane = boardPane;
        initializeResourceColors();
    }

    private void initializeResourceColors() {
        resourceColors.put(Resources.WOOD, Color.FORESTGREEN);
        resourceColors.put(Resources.BRICK, Color.FIREBRICK);
        resourceColors.put(Resources.SHEEP, Color.web("#c7ad7f")); // Light green/beige
        resourceColors.put(Resources.WHEAT, Color.GOLD);
        resourceColors.put(Resources.STONE, Color.GRAY);
        resourceColors.put(Resources.NONE, Color.SANDYBROWN); // Desert
    }

    // Initialize board with CatanBoard logic
    public void initializeBoard(int radius) {
        catanBoard = new CatanBoard(radius);
        createTilesFromBoard();
        initializeVertexPoints();
    }

    // Create hexagon tiles based on CatanBoard data
    private void createTilesFromBoard() {
        for (IntTupel coords : catanBoard.hex_coords) {
            HexTile hexTile = catanBoard.board.get(coords);
            if (hexTile != null) {
                Polygon hexagon = createHexagon();

                // Set color based on resource type
                Color resourceColor = resourceColors.get(hexTile.getResourceType());
                hexagon.setFill(resourceColor);

                allTiles.add(hexagon);
                hexagonMap.put(coords, hexagon);

                // Create dice number text if not desert
                if (hexTile.getDiceNumber() > 0) {
                    Text diceText = createDiceNumberText(hexTile.getDiceNumber());
                    diceNumbers.add(diceText);
                    boardPane.getChildren().add(diceText);
                }
            }
        }
        boardPane.getChildren().addAll(allTiles);
    }

    // Create dice number text
    private Text createDiceNumberText(int diceNumber) {
        Text text = new Text(String.valueOf(diceNumber));
        text.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        text.setTextAlignment(TextAlignment.CENTER);

        // Highlight 6 and 8 in red
        if (diceNumber == 6 || diceNumber == 8) {
            text.setFill(Color.RED);
        } else {
            text.setFill(Color.BLACK);
        }

        return text;
    }

    // Create settlement points at hex vertices
    public void initializeVertexPoints() {
        Map<Node, Circle> processedNodes = new HashMap<>();

        for (IntTupel coords : catanBoard.hex_coords) {
            HexTile hexTile = catanBoard.board.get(coords);
            if (hexTile != null) {
                Node[] nodes = hexTile.getHexTileNodes();

                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];

                    // Only create one circle per unique node
                    if (!processedNodes.containsKey(node)) {
                        Circle vertex = new Circle(8);
                        vertex.setFill(Color.ORANGERED.deriveColor(1, 1, 1, 0.5));
                        vertex.setStroke(Color.RED);

                        // Store reference to the node
                        vertex.setUserData(node);

                        vertex.setOnMouseClicked(event -> {
                            if (onVertexClickCallback != null) {
                                onVertexClickCallback.accept(vertex);
                            }
                        });

                        vertexPoints.add(vertex);
                        processedNodes.put(node, vertex);
                        nodeCircleMap.put(node, vertex);
                        boardPane.getChildren().add(vertex);
                    }
                }
            }
        }
    }

    // Position all visual elements
    public void positionVisuals() {
        double paneWidth = boardPane.getWidth();
        double paneHeight = boardPane.getHeight();

        double centerX = paneWidth / 2.0;
        double centerY = paneHeight / 2.0;

        // Position hexagons
        int diceTextIndex = 0;
        for (IntTupel coords : catanBoard.hex_coords) {
            HexTile hexTile = catanBoard.board.get(coords);
            if (hexTile != null) {
                Polygon hexagon = hexagonMap.get(coords);

                // Convert axial coordinates to pixel coordinates
                double x = centerX + HEX_WIDTH * (coords.q() + coords.r() / 2.0);
                double y = centerY + HEX_HEIGHT * 0.75 * coords.r();

                hexagon.setLayoutX(x);
                hexagon.setLayoutY(y);

                // Position dice number text
                if (hexTile.getDiceNumber() > 0 && diceTextIndex < diceNumbers.size()) {
                    Text diceText = diceNumbers.get(diceTextIndex);
                    diceText.setLayoutX(x - 8); // Offset for text centering
                    diceText.setLayoutY(y + 5); // Offset for text centering
                    diceTextIndex++;
                }
            }
        }

        // Position vertex points
        for (IntTupel coords : catanBoard.hex_coords) {
            HexTile hexTile = catanBoard.board.get(coords);
            if (hexTile != null) {
                Node[] nodes = hexTile.getHexTileNodes();
                Polygon hexagon = hexagonMap.get(coords);

                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];
                    Circle vertex = nodeCircleMap.get(node);

                    if (vertex != null) {
                        // Calculate vertex position
                        double angle = Math.PI / 3 * i + Math.PI / 6; // Start at 30 degrees
                        double x = hexagon.getLayoutX() + HEX_SIZE * Math.cos(angle);
                        double y = hexagon.getLayoutY() + HEX_SIZE * Math.sin(angle);

                        vertex.setCenterX(x);
                        vertex.setCenterY(y);
                    }
                }
            }
        }

        // Reposition placed settlements
        for (Map.Entry<Circle, Rectangle> entry : placedSettlements.entrySet()) {
            Circle vertex = entry.getKey();
            Rectangle house = entry.getValue();

            house.setLayoutX(vertex.getCenterX() - house.getWidth() / 2);
            house.setLayoutY(vertex.getCenterY() - house.getHeight() / 2);
        }
    }

    // Create a hexagon polygon
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

    // Draw settlement at clicked vertex
    public void drawSettlement(Circle clickedVertex, Color playerColor) {
        Rectangle house = new Rectangle(16, 16);
        house.setLayoutX(clickedVertex.getCenterX() - house.getWidth() / 2);
        house.setLayoutY(clickedVertex.getCenterY() - house.getHeight() / 2);

        house.setFill(playerColor);
        house.setStroke(Color.BLACK);
        boardPane.getChildren().add(house);

        placedSettlements.put(clickedVertex, house);
        clickedVertex.setVisible(false);
    }

    // Set click handler for vertices
    public void setOnVertexClickHandler(Consumer<Circle> callback) {
        this.onVertexClickCallback = callback;
    }

    // Get the CatanBoard instance
    public CatanBoard getCatanBoard() {
        return catanBoard;
    }

    // Get the node associated with a circle
    public Node getNodeFromCircle(Circle circle) {
        return (Node) circle.getUserData();
    }
}