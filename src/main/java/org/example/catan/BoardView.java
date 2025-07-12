package org.example.catan;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import lombok.Setter;
import org.example.catan.gamepieces.Player;
import org.example.catan.gamepieces.Resources;
import org.example.catan.gamepieces.TradeOffer;
import org.example.catan.graph.HexTile;
import org.example.catan.graph.IntTupel;
import org.example.catan.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class BoardView {
    private static final double HEX_SIZE = 50;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_SIZE;
    private static final double[] CORNER_ANGLES_DEG = {-90, -30, 30, 90, 150, 210};
    private final Map<String, Line> ghostRoads = new HashMap<>();
    private final Pane boardPane;
    private final Map<Integer, Circle> nodeCircles = new HashMap<>();
    private final Map<Circle, Rectangle> placedSettlements = new HashMap<>();
    private final List<Polygon> allTiles = new ArrayList<>();
    private Consumer<Line> onRoadClickCallback;
    private int[][] adjacencyMatrix;
    private Consumer<Circle> onVertexClickCallback;
    private VBox playerUIBox;
    private Label resourceLabel;
    private Button rollDiceButton;
    private Button endTurnButton;
    private Player currentPlayer;
    private Label playerColorLabel;
    private Map<Polygon, IntTupel> tileByPolygon = new HashMap<>();
    private Circle banditCircle = null;
    private Polygon banditTile = null;
    @Setter
    private Consumer<IntTupel> onBanditPlaced;
    private List<Circle> ghostBanditMarkers = new ArrayList<>();


    // Bottom-left
    private VBox tradeFormBox;
    private javafx.scene.control.ComboBox<Resources> giveResourceBox;
    private javafx.scene.control.Spinner<Integer> giveAmountSpinner;
    private javafx.scene.control.ComboBox<Resources> wantResourceBox;
    private javafx.scene.control.Spinner<Integer> wantAmountSpinner;
    private Button offerTradeButton;
    private CheckBox bankTradeCheckbox;

    // Top-right
    private VBox activeTradesBox;
    @Setter
    private Consumer<TradeOffer> onTradeOffer;

    public BoardView(Pane boardPane, CatanBoard catanBoard, int[][] adjacencyMatrix) {
        this.boardPane = boardPane;
        this.adjacencyMatrix = adjacencyMatrix;
        createPlayerUI();
        Platform.runLater(this::updateResourceDisplay);
        loadBoardFromModel(catanBoard);
    }

    public void promptBanditPlacement(CatanBoard catanBoard) {
        ghostBanditMarkers.clear(); // start fresh

        for (Map.Entry<Polygon, IntTupel> entry : tileByPolygon.entrySet()) {
            Polygon hex = entry.getKey();
            IntTupel coord = entry.getValue();
            HexTile tile = catanBoard.getBoard().get(coord);

            if (tile == null || tile.getResourceType() == Resources.NONE) continue;

            double centerX = hex.getBoundsInParent().getMinX() + hex.getBoundsInParent().getWidth() / 2;
            double centerY = hex.getBoundsInParent().getMinY() + hex.getBoundsInParent().getHeight() / 2;

            Circle ghost = new Circle(centerX, centerY, 20);
            ghost.setStroke(Color.BLACK);
            ghost.setFill(Color.BLACK);
            ghost.setOpacity(0.3);

            ghost.setOnMouseEntered(e -> ghost.setOpacity(1));
            ghost.setOnMouseExited(e -> ghost.setOpacity(0.5));

            ghost.setUserData(hex);

            ghost.setOnMouseClicked(e -> {
                Polygon targetHex = (Polygon) ghost.getUserData();
                IntTupel targetCoord = tileByPolygon.get(targetHex);

                if (onBanditPlaced != null) {
                    onBanditPlaced.accept(targetCoord);
                }

                placeBanditOnTile(targetHex, centerX, centerY);
                hideBanditGhosts(); // ✅ remove all ghost circles
            });

            ghostBanditMarkers.add(ghost); // ✅ track for later removal
            boardPane.getChildren().add(ghost);
        }
    }

    public void hideBanditGhosts() {
        for (Circle ghost : ghostBanditMarkers) {
            boardPane.getChildren().remove(ghost);
        }
        ghostBanditMarkers.clear();
    }

    public void placeBanditOnTile(Polygon hex, double centerX, double centerY) {
        if (banditCircle != null) {
            boardPane.getChildren().remove(banditCircle);
        }

        banditTile = hex;
        banditCircle = new Circle(centerX, centerY, 20, Color.BLACK);
        boardPane.getChildren().add(banditCircle);
    }

    public void placeInitialBandit(CatanBoard catanBoard) {
        for (Map.Entry<Polygon, IntTupel> entry : tileByPolygon.entrySet()) {
            IntTupel coord = entry.getValue();
            HexTile tile = catanBoard.getBoard().get(coord);

            if (tile.getResourceType() == Resources.NONE) { // desert tile
                tile.setBlocked(true); // mark as blocked in model

                Polygon hex = entry.getKey();
                double centerX = hex.getBoundsInParent().getMinX() + hex.getBoundsInParent().getWidth() / 2;
                double centerY = hex.getBoundsInParent().getMinY() + hex.getBoundsInParent().getHeight() / 2;

                placeBanditOnTile(hex, centerX, centerY); // already defined
                break;
            }
        }
    }

    public void updateResourceDisplay() {
        if (currentPlayer == null) return;

        StringBuilder sb = new StringBuilder("Resources:\n");

        for (Resources res : currentPlayer.getInventorySnapshot().keySet()) {
            sb.append(res.name()).append(": ").append(currentPlayer.getResourceCount(res)).append("\n");
        }

//        System.out.println("📦 Final label text:\n" + sb); // Optional: final output
        resourceLabel.setText(sb.toString());
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;

        String colorName = convertColorToName(player.getColor());
        Platform.runLater(() -> {
            playerColorLabel.setText("Current Player: " + colorName);
            updateResourceDisplay();
        });
    }

    private String convertColorToName(Color color) {
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.WHITE)) return "White";
        if (color.equals(Color.YELLOW)) return "Yellow";
        return "Unknown";
    }

    private void createTradeUI() {
        giveResourceBox = new ComboBox<>();
        for (Resources res : Resources.values()) {
            if (res != Resources.NONE) {
                giveResourceBox.getItems().add(res);
            }
        }

        giveResourceBox.setValue(Resources.WOOD);

        giveAmountSpinner = new Spinner<>(1, 19, 1);

        wantResourceBox = new ComboBox<>();
        for (Resources res : Resources.values()) {
            if (res != Resources.NONE) {
                wantResourceBox.getItems().add(res);
            }
        }

        wantResourceBox.setValue(Resources.BRICK);

        wantAmountSpinner = new Spinner<>(1, 19, 1);

        bankTradeCheckbox = new CheckBox("Trade with Bank (4:1)");

        offerTradeButton = new Button("💱 Offer Trade");

        tradeFormBox = new VBox(5,
                new Label("Give:"), giveResourceBox, giveAmountSpinner,
                new Label("Want:"), wantResourceBox, wantAmountSpinner,
                bankTradeCheckbox, offerTradeButton);
        tradeFormBox.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 10; -fx-border-color: gray;");
        tradeFormBox.setLayoutX(10);
        Platform.runLater(() -> {
            tradeFormBox.setLayoutY(boardPane.getHeight() - tradeFormBox.getHeight() - 10);
        });

        boardPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            tradeFormBox.setLayoutY(newVal.doubleValue() - tradeFormBox.getHeight() - 10);
        });


        boardPane.getChildren().add(tradeFormBox);
        bankTradeCheckbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                giveAmountSpinner.getValueFactory().setValue(4);
                wantAmountSpinner.getValueFactory().setValue(1);
                giveAmountSpinner.setDisable(true);
                wantAmountSpinner.setDisable(true);
            } else {
                giveAmountSpinner.setDisable(false);
                wantAmountSpinner.setDisable(false);
            }
        });

    }

    private void createTradeViewer() {
        activeTradesBox = new VBox(10);
        activeTradesBox.setStyle("""
        -fx-background-color: rgba(255, 255, 255, 0.9);
        -fx-padding: 10;
        -fx-border-color: gray;
        -fx-background-radius: 5;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 2, 2);
    """);

        double boxWidth = 250; // set your desired width here
        activeTradesBox.setPrefWidth(boxWidth);
        activeTradesBox.setMinWidth(boxWidth);
        activeTradesBox.setMaxWidth(boxWidth);

        activeTradesBox.setLayoutY(10); // top spacing

        boardPane.getChildren().add(activeTradesBox);

        // 🟢 Align it 10px from the right after layout pass
        Platform.runLater(() -> {
            activeTradesBox.setLayoutX(boardPane.getWidth() - boxWidth - 10);
        });

        // 🟢 Re-align on window resize
        boardPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            activeTradesBox.setLayoutX(newVal.doubleValue() - boxWidth - 10);
        });
    }


    private void createPlayerUI() {
        resourceLabel = new Label("Resources:");
        resourceLabel.setWrapText(true);
        resourceLabel.setMaxWidth(120);

        playerColorLabel = new Label("Current Player: ");
        playerColorLabel.setWrapText(true);
        playerColorLabel.setMaxWidth(120);

        rollDiceButton = new Button("🎲 Roll Dice");
        endTurnButton = new Button("➡ End Turn");

        playerUIBox = new VBox(10, playerColorLabel, resourceLabel, rollDiceButton, endTurnButton);
        playerUIBox.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-padding: 10; -fx-border-color: gray;");
        playerUIBox.setAlignment(Pos.CENTER);
        playerUIBox.setLayoutX(10);
        playerUIBox.setLayoutY(10);

        boardPane.getChildren().add(playerUIBox);

        boardPane.widthProperty().addListener((obs, oldVal, newVal) ->
                playerUIBox.setLayoutX(newVal.doubleValue() - 160));
        boardPane.heightProperty().addListener((obs, oldVal, newVal) ->
                playerUIBox.setLayoutY(newVal.doubleValue() - 120));
        createTradeUI();
        createTradeViewer();

    }

    public void hideVertexByNodeId(int nodeId) {
        Circle circle = nodeCircles.get(nodeId);
        if (circle != null) {
            circle.setVisible(false); // Hides the circle completely
            circle.setDisable(true);  // Prevents click interaction (optional if hidden)
        }
    }

    public void setOnEndTurn(Runnable action) {
        endTurnButton.setOnAction(e -> action.run());
    }

    public void setOnRollDice(Runnable handler) {
        rollDiceButton.setOnAction(e -> handler.run());
    }

    private void generateGhostRoadsFromAdjacencyMatrix() {
        if (adjacencyMatrix == null) return;

        int size = adjacencyMatrix.length;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    Circle c1 = nodeCircles.get(i);
                    Circle c2 = nodeCircles.get(j);

                    if (c1 == null || c2 == null) {
                        continue;
                    }


                    String key = i + "-" + j;
                    if (ghostRoads.containsKey(key)) continue;

                    double x1 = c1.getCenterX();
                    double y1 = c1.getCenterY();
                    double x2 = c2.getCenterX();
                    double y2 = c2.getCenterY();

                    double dx = x2 - x1;
                    double dy = y2 - y1;

                    double length = Math.sqrt(dx * dx + dy * dy);

                    double margin = 12;

                    double ux = dx / length;
                    double uy = dy / length;

                    double sx = x1 + ux * margin;
                    double sy = y1 + uy * margin;
                    double ex = x2 - ux * margin;
                    double ey = y2 - uy * margin;

                    Line ghost = new Line(sx, sy, ex, ey);

                    ghost.setStroke(Color.LIGHTGRAY);
                    ghost.setStrokeWidth(10);
                    ghost.setOpacity(0.5);
                    ghost.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

                    ghost.setOnMouseEntered(e -> ghost.setOpacity(0.8));
                    ghost.setOnMouseExited(e -> ghost.setOpacity(0.5));

                    ghost.setOnMouseClicked(e -> {
                        if (onRoadClickCallback != null) {
                            onRoadClickCallback.accept(ghost);
                        }
                    });

                    ghost.setUserData(new int[]{i, j});
                    ghostRoads.put(key, ghost);
                    boardPane.getChildren().add(ghost);
                }
            }

        }

    }

    public void loadBoardFromModel(CatanBoard catanBoard) {
        // First pass: create and add all hexagons + dice numbers
        for (Map.Entry<IntTupel, HexTile> entry : catanBoard.getBoard().entrySet()) {
            IntTupel coord = entry.getKey();
            HexTile tile = entry.getValue();

            double paneWidth = boardPane.getWidth();
            double paneHeight = boardPane.getHeight();

            double offsetX = paneWidth / 2.0;
            double offsetY = paneHeight / 2.0;

            double centerX = offsetX + HEX_SIZE * Math.sqrt(3) * (coord.q() + coord.r() / 2.0);
            double centerY = offsetY + HEX_SIZE * 1.5 * coord.r();

            // Create hexagon
            Polygon hex = createHexagon(centerX, centerY);
            hex.setFill(resourceToColor(tile.getResourceType()));
            hex.setStroke(Color.BLACK);
            hex.setStrokeWidth(2);
            allTiles.add(hex);
            boardPane.getChildren().add(hex); // 🔹 HEXES FIRST
            tileByPolygon.put(hex, coord);


            // Display dice number (if not desert)
            if (tile.getDiceNumber() != 0) {
                javafx.scene.text.Text diceText = new javafx.scene.text.Text(String.valueOf(tile.getDiceNumber()));
                diceText.setFont(javafx.scene.text.Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 18));
                diceText.setX(centerX - 8); // center adjust X
                diceText.setY(centerY + 6); // center adjust Y
                diceText.setFill(Color.BLACK);
                boardPane.getChildren().add(diceText); // 🟡 Add on top of the hex
            }
        }


        // Second pass: place node circles
        for (Map.Entry<IntTupel, HexTile> entry : catanBoard.getBoard().entrySet()) {
            IntTupel coord = entry.getKey();
            HexTile tile = entry.getValue();

            double paneWidth = boardPane.getWidth();
            double paneHeight = boardPane.getHeight();

            double offsetX = paneWidth / 2.0;
            double offsetY = paneHeight / 2.0;

            double centerX = offsetX + HEX_SIZE * Math.sqrt(3) * (coord.q() + coord.r() / 2.0);
            double centerY = offsetY + HEX_SIZE * 1.5 * coord.r();

            for (int i = 0; i < tile.getHexTileNodes().length; i++) {
                Node node = tile.getHexTileNodes()[i];
                if (!nodeCircles.containsKey(node.getId())) {
                    double angle_deg = CORNER_ANGLES_DEG[i];
                    double angle_rad = Math.toRadians(angle_deg);

                    double vx = centerX + HEX_SIZE * Math.cos(angle_rad);
                    double vy = centerY + HEX_SIZE * Math.sin(angle_rad);

                    Circle vertex = new Circle(8);
                    vertex.setFill(Color.LIGHTGRAY.deriveColor(1, 1, 1, 0.5));
                    vertex.setStroke(Color.GRAY);
                    vertex.setFill(Color.LIGHTGRAY);
                    vertex.setCenterX(vx);
                    vertex.setCenterY(vy);
                    vertex.setUserData(node);
                    vertex.setOpacity(0.5);
                    vertex.setOnMouseEntered(e -> vertex.setOpacity(0.8));
                    vertex.setOnMouseExited(e -> vertex.setOpacity(0.5));

                    vertex.setOnMouseClicked(event -> {
                        Node clickedNode = (Node) vertex.getUserData();

                        if (onVertexClickCallback != null) {
                            onVertexClickCallback.accept(vertex);
                        }
                    });


                    nodeCircles.put(node.getId(), vertex);
                    boardPane.getChildren().add(vertex); // 🔺 CIRCLES AFTER HEXES
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
        generateGhostRoadsFromAdjacencyMatrix();
    }

    private Polygon createHexagon(double centerX, double centerY) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i + 30;
            double angle_rad = Math.toRadians(angle_deg);
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
            case WOOD -> Color.DARKGREEN;
            case WHEAT -> Color.WHEAT;
            case SHEEP -> Color.ORANGE;
            case BRICK -> Color.FIREBRICK;
            case STONE -> Color.GRAY;
            case NONE -> Color.BLACK;
            default -> Color.LIGHTGRAY;
        };
    }

    public void placeSettlement(Circle clickedVertex, Color playerColor) {
        Rectangle house = new Rectangle(16, 16);
        house.setFill(playerColor);
        house.setStroke(Color.BLACK);
        house.setLayoutX(clickedVertex.getCenterX() - house.getWidth() / 2);
        house.setLayoutY(clickedVertex.getCenterY() - house.getHeight() / 2);
        boardPane.getChildren().add(house);

        placedSettlements.put(clickedVertex, house);
        clickedVertex.setVisible(false);
    }

    public void placeRoad(Line ghostLine, Color playerColor) {
        int[] nodes = (int[]) ghostLine.getUserData();
        if (nodes == null) return;

        Line solidRoad = new Line(
                ghostLine.getStartX(), ghostLine.getStartY(),
                ghostLine.getEndX(), ghostLine.getEndY()
        );
        solidRoad.setStroke(playerColor);
//        solidRoad.setStrokeWidth(1);
//        solidRoad.setFill(playerColor);
        solidRoad.setStrokeWidth(6);
        solidRoad.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

        boardPane.getChildren().add(solidRoad);
        boardPane.getChildren().remove(ghostLine);
        ghostRoads.remove(nodes[0] + "-" + nodes[1]);
    }

    public void setOnVertexClickHandler(Consumer<Circle> callback) {
        this.onVertexClickCallback = callback;
    }

    public void setOnRoadClickHandler(Consumer<Line> callback) {
        this.onRoadClickCallback = callback;
    }

    public void setOnTradeOfferSubmitted(Consumer<TradeOffer> handler) {
        offerTradeButton.setOnAction(e -> {
            Resources give = giveResourceBox.getValue();
            int giveAmt = giveAmountSpinner.getValue();
            Resources want = wantResourceBox.getValue();
            int wantAmt = wantAmountSpinner.getValue();
            boolean isBank = bankTradeCheckbox.isSelected();

            if (give == Resources.NONE || want == Resources.NONE) return;

            Map<Resources, Integer> offerMap = Map.of(give, giveAmt);
            Map<Resources, Integer> wantMap = Map.of(want, wantAmt);

            TradeOffer offer = new TradeOffer(currentPlayer, offerMap, wantMap, isBank);
            handler.accept(offer);
        });
    }

    private String getColorEmoji(Color color) {
        if (Color.RED.equals(color)) return "🔴";
        if (Color.BLUE.equals(color)) return "🔵";
        if (Color.YELLOW.equals(color)) return "🟡";
        if (Color.WHITE.equals(color)) return "⚪";
//        if (Color.ORANGE.equals(color)) return "🟠";
        return "❔";
    }


    public void showActiveTrades(List<TradeOffer> offers, Consumer<TradeOffer> onAccept) {
        activeTradesBox.getChildren().clear();

        for (TradeOffer offer : offers) {
            String colorEmoji = getColorEmoji(offer.getSender().getColor());

            String offerText = colorEmoji + " " + offer.getSender().getName() + " offers " +
                    offer.getOffer().entrySet().iterator().next().getValue() + " " +
                    offer.getOffer().keySet().iterator().next().name() +
                    " for " +
                    offer.getRequest().entrySet().iterator().next().getValue() + " " +
                    offer.getRequest().keySet().iterator().next().name();

            Label label = new Label(offerText);
            label.setWrapText(true);

            Button acceptBtn = new Button("✅ Accept");
            acceptBtn.setStyle("-fx-border-radius: 3; -fx-background-radius: 3;");
            acceptBtn.setOnAction(e -> onAccept.accept(offer));

            VBox tradeCard = new VBox(5, label, acceptBtn);
            tradeCard.setStyle("""
                        -fx-background-color: white;
                        -fx-padding: 8;
                        -fx-border-color: lightgray;
                        -fx-background-radius: 4;
                    """);

            activeTradesBox.getChildren().add(tradeCard);
        }
    }


}
