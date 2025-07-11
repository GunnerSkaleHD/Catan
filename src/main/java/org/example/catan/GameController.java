package org.example.catan;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.example.catan.Graph.HexTile;
import org.example.catan.Graph.Node;
import org.example.catan.TradeManager;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameController {

    private final Set<Integer> blockedNodes = new HashSet<>();
    private final List<Player> players = new ArrayList<>();
    private CatanBoard board;  // assumes 3 is the correct config/depth
    @FXML
    private Pane boardPane;
    private BoardView boardView;
    private Player currentPlayer;
    private Player startingPlayer;
    private int[][] adjacencyMatrix;
    private int currentPlayerIndex;
    private int currentPlayerDiceRolls;
    private Bank bank;

    private Map<Player, List<Trade>> tradeBuffer = new HashMap<>();
    private TradeManager tradeManager = new TradeManager();


    private boolean canBuildStreet(Player player) {
        return player.getResourceCount(Resources.WOOD) >= 1 &&
                player.getResourceCount(Resources.BRICK) >= 1;
    }

    private boolean canBuildSettlement(Player player) {
        return player.getResourceCount(Resources.WOOD) >= 1 &&
                player.getResourceCount(Resources.BRICK) >= 1 &&
                player.getResourceCount(Resources.WHEAT) >= 1 &&
                player.getResourceCount(Resources.SHEEP) >= 1;

    }


    @FXML
    public void initialize() {
        currentPlayerIndex = 0;
        bank = new Bank();


        players.add(new Player(Color.BLUE));
        players.add(new Player(Color.RED));
        players.add(new Player(Color.YELLOW));
        players.add(new Player(Color.WHITE));

        currentPlayer = players.getFirst();
        startingPlayer = currentPlayer; 

        board = new CatanBoard(3);
        adjacencyMatrix = new int[54][54];
        for (int i = 0; i < 54; i++) {
            for (int j = 0; j < 54; j++) {
                adjacencyMatrix[i][j] = board.graph[i][j][0];
            }
        }

        setupBoardView();
//        boardView.setOnEndTurn(this::nextPlayer);
//        boardView.setOnRollDice(this::rollDice);


        Platform.runLater(() -> {
            boardView.setCurrentPlayer(currentPlayer);
        });


        // Add resize listeners for dynamic board scaling
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            boardPane.getChildren().clear();
            this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
            boardView.setCurrentPlayer(currentPlayer);
            boardView.setOnVertexClickHandler(this::handleVertexClick);
            boardView.setOnRoadClickHandler(this::handleEdgeClick);
            boardView.setOnEndTurn(this::nextPlayer);
            boardView.setOnRollDice(this::rollDice);
            boardView.setOnTradeWithBank(() -> new TradeDialog(currentPlayer, bank, boardView));
            boardView.setOnTradeWithPlayer(() -> new PlayerTradeDialog(currentPlayer, players, boardView, tradeManager));
        };


        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);

        // Initial click handler setup
        boardView.setOnVertexClickHandler(this::handleVertexClick);
        boardView.setOnRoadClickHandler(this::handleEdgeClick);
        boardPane.setStyle("-fx-background-color: LIGHTBLUE;");

    }


    private void handleEdgeClick(Line ghostLine) {
        int[] nodes = (int[]) ghostLine.getUserData();
        if (nodes == null || nodes.length != 2) {
            System.out.println("❌ Invalid edge click data.");
            return;
        }
        if (currentPlayerDiceRolls == 0) {
            return;
        }

        if (!canBuildStreet(currentPlayer) || !bank.useStreet()) {
            System.out.println("🚫 Cannot build street (resources or bank limit).");
            return;
        }


        // Try to update the model first
        boolean placed = currentPlayer.placeStreet(nodes[0], nodes[1]);
        if (!placed) {
            System.out.println("❌ Street placement failed (limit reached or duplicate).");
            return;
        }

        // Visually place it
        boardView.placeRoad(ghostLine, currentPlayer.getColor());
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }


    private void handleVertexClick(Circle clickedVertex) {
        Node node = (Node) clickedVertex.getUserData();
        int nodeId = node.getId();
        if (!canBuildSettlement(currentPlayer) || !bank.useSettlement()) {
            System.out.println("🚫 Cannot build settlement (resources or bank limit).");
            return;
        }

        if (currentPlayerDiceRolls == 0) {
            return;
        }

        // Prevent placing if node is blocked
        if (blockedNodes.contains(nodeId)) {
            System.out.println("❌ Node " + nodeId + " is blocked.");
            return;
        }
        boolean placed = currentPlayer.placeSettlement(nodeId);
        if (!placed) {
            System.out.println("❌ Settlement placement failed (limit reached or duplicate).");
            return;
        }

        // ✅ Place settlement
        boardView.placeSettlement(clickedVertex, currentPlayer.getColor());

        // ✅ Block this node and its adjacent nodes
        blockedNodes.add(nodeId);
        boardView.hideVertexByNodeId(nodeId); // hide current

        for (int i = 0; i < adjacencyMatrix[nodeId].length; i++) {
            if (adjacencyMatrix[nodeId][i] == 1) {
                blockedNodes.add(i);
                boardView.hideVertexByNodeId(i); // hide adjacent
            }
        }

        System.out.println("✅ Settlement placed on node " + nodeId);


        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    private void setupBoardView() {
        this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
        boardView.setCurrentPlayer(currentPlayer);
        boardView.setOnVertexClickHandler(this::handleVertexClick);
        boardView.setOnRoadClickHandler(this::handleEdgeClick);
        boardView.setOnRollDice(this::rollDice);
        boardView.setOnEndTurn(this::nextPlayer);
    }

    private void nextPlayer() {
        if (currentPlayer.equals(startingPlayer)) {
            tradeManager.clearTradesByPlayer(currentPlayer);
        }

        if (currentPlayer.getVictoryPoints() >= 5) {
            String winnerColor = colorToString(currentPlayer.getColor());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Victory!");
            alert.setHeaderText("🏆 " + winnerColor + " wins the game!");
            alert.setContentText("Congratulations to player " + winnerColor + " for reaching 5 victory points!");
            alert.showAndWait();

        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayerDiceRolls = 0;
        boardView.setCurrentPlayer(currentPlayer);
        boardView.updateResourceDisplay();
        System.out.println("🔄 Turn switched to player: " + currentPlayer.getColor());
    }

    private void rollDice() {
        if (currentPlayerDiceRolls > 0) {
            System.out.println("Dice already rolled.");
            return;
        }

        int result = new Dice(2).rollDice();
        System.out.println("🎲 Dice rolled: " + result);

        for (HexTile tile : board.getBoard().values()) {
            if (tile.getDiceNumber() == result) {
                Resources resource = tile.getResourceType();
                Node[] nodes = tile.getHexTileNodes();

                for (Node node : nodes) {
                    int nodeId = node.getId();

                    for (Player player : players) {
                        if (player.ownsSettlementAt(nodeId)) {
                            if (bank.takeResource(resource, 1)) {
                                player.addResource(resource, 1);
                                System.out.println("🌾 " + resource + " given to " + player + " at node " + nodeId);
                            } else {
                                System.out.println("🚫 Bank is out of " + resource + ", no resource given.");
                            }
                        }
                    }
                }
            }
        }

        currentPlayerDiceRolls++;
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    private String colorToString(Color color) {
        if (Color.RED.equals(color)) return "Red";
        if (Color.BLUE.equals(color)) return "Blue";
        if (Color.YELLOW.equals(color)) return "Yellow";
        if (Color.WHITE.equals(color)) return "White";
        if (Color.ORANGE.equals(color)) return "Orange";
        return "Unknown Color";
    }


}
