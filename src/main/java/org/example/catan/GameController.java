package org.example.catan;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.example.catan.gamepieces.*;
import org.example.catan.graph.HexTile;
import org.example.catan.graph.IntTupel;
import org.example.catan.graph.Node;

import java.util.*;

public class GameController {

    private final Set<Integer> blockedNodes = new HashSet<>();
    private final List<Player> players = new ArrayList<>();
    private final List<TradeOffer> activeTrades = new ArrayList<>();
    private CatanBoard board;  // assumes 3 is the correct config/depth
    @FXML
    private Pane boardPane;
    private BoardView boardView;
    private Player currentPlayer;
    private int[][] adjacencyMatrix;
    private int currentPlayerIndex;
    private int currentPlayerDiceRolls;
    private Bank bank;
    private boolean waitingForBandit = false;


    private void setupBoardView() {
        this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
        boardView.setCurrentPlayer(currentPlayer);
        boardView.setOnVertexClickHandler(this::handleVertexClick);
        boardView.setOnRoadClickHandler(this::handleEdgeClick);
        boardView.setOnRollDice(this::rollDice);
        boardView.setOnEndTurn(this::nextPlayer);
        boardView.setOnTradeOfferSubmitted(this::handleTradeOffer);
        Platform.runLater(() -> boardView.placeInitialBandit(board));
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

        board = new CatanBoard(3);
        adjacencyMatrix = new int[54][54];
        for (int i = 0; i < 54; i++) {
            for (int j = 0; j < 54; j++) {
                adjacencyMatrix[i][j] = board.graph[i][j][0];
            }
        }

        setupBoardView();


        Platform.runLater(() -> {
            boardView.setCurrentPlayer(currentPlayer);
        });


        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            boardPane.getChildren().clear();
            this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
            boardView.setCurrentPlayer(currentPlayer);
            boardView.setOnVertexClickHandler(this::handleVertexClick);
            boardView.setOnRoadClickHandler(this::handleEdgeClick);
            boardView.setOnEndTurn(this::nextPlayer);
            boardView.setOnRollDice(this::rollDice);
            boardView.setOnTradeOfferSubmitted(this::handleTradeOffer);
            updateTradeViewerUI(); // ✅ Re-render any active trades
        };


        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);

        // Initial click handler setup
        boardView.setOnVertexClickHandler(this::handleVertexClick);
        boardView.setOnRoadClickHandler(this::handleEdgeClick);
        boardPane.setStyle("-fx-background-color: LIGHTBLUE;");

    }

    private void removeExpiredTrades(Player player) {
        activeTrades.removeIf(offer -> offer.getSender().equals(player));
    }


    private void handleTradeOffer(TradeOffer offer) {
        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
            return;
        }
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }
        if (offer.isBankTrade()) {
            handleBankTrade(offer);
        } else {
            activeTrades.add(offer);
            updateTradeViewerUI();
        }
    }

    private void handleBankTrade(TradeOffer offer) {
        Player player = offer.getSender();

        Map<Resources, Integer> offerMap = offer.getOffer();
        Map<Resources, Integer> wantMap = offer.getRequest();

        Resources giveRes = offerMap.keySet().iterator().next();
        int giveAmt = offerMap.get(giveRes);

        Resources wantRes = wantMap.keySet().iterator().next();

        if (giveAmt != 4) {
            showAlert("Bank trades require giving exactly 4 of one resource.");
            return;
        }

        if (!player.removeResource(giveRes, giveAmt)) {
            showAlert("You don't have enough resources for this bank trade.");
            return;
        }

        player.addResource(wantRes, 1);
        boardView.updateResourceDisplay();
        showAlert("✅ Trade with bank successful.");
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTradeViewerUI() {
        boardView.showActiveTrades(activeTrades, this::acceptTradeOffer);
    }

    private void acceptTradeOffer(TradeOffer offer) {
        Player receiver = currentPlayer;
        Player sender = offer.getSender();

        Resources giveRes = offer.getOffer().keySet().iterator().next();
        int giveAmt = offer.getOffer().get(giveRes);

        Resources wantRes = offer.getRequest().keySet().iterator().next();
        int wantAmt = offer.getRequest().get(wantRes);

        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }
        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
            return;
        }
        if (!receiver.removeResource(wantRes, wantAmt)) {
            showAlert("❌ You don't have enough resources to accept this trade.");
            return;
        }

        if (!sender.removeResource(giveRes, giveAmt)) {
            showAlert("❌ Sender doesn't have enough resources anymore.");
            receiver.addResource(wantRes, wantAmt); // rollback
            return;
        }


        receiver.addResource(giveRes, giveAmt);
        sender.addResource(wantRes, wantAmt);

        offer.accept(receiver);
        activeTrades.remove(offer);

        boardView.updateResourceDisplay();
        updateTradeViewerUI();
        showAlert("✅ Trade accepted.");
    }


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


    private void handleEdgeClick(Line ghostLine) {
        int[] nodes = (int[]) ghostLine.getUserData();
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }


        if (nodes == null || nodes.length != 2) {
            System.out.println("❌ Invalid edge click data.");
            return;
        }
        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
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
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }
        if (!canBuildSettlement(currentPlayer) || !bank.useSettlement()) {
            System.out.println("🚫 Cannot build settlement (resources or bank limit).");
            return;
        }

        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
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


    private void nextPlayer() {
        if (currentPlayer.getVictoryPoints() >= 5) {
            String winnerColor = colorToString(currentPlayer.getColor());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🎉 Victory!");
            alert.setHeaderText("🏆 " + winnerColor + " wins the game!");
            alert.setContentText("Congratulations to player " + winnerColor + " for reaching 5 victory points!");
            alert.showAndWait();

        }
        if (currentPlayerDiceRolls == 0) {
            showAlert("Please roll the dice before ending your turn.");
            return;
        }
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayerDiceRolls = 0;
        removeExpiredTrades(currentPlayer);
        updateTradeViewerUI();
        boardView.setCurrentPlayer(currentPlayer);
        boardView.updateResourceDisplay();
        System.out.println("🔄 Turn switched to player: " + currentPlayer.getColor());
    }

    private void rollDice() {
        if (currentPlayerDiceRolls > 0) {
            System.out.println("Dice already rolled.");
            return;
        }
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }

        int result = new Dice(2).rollDice();
        System.out.println("🎲 Dice rolled: " + result);

        if (result == 7) {
            System.out.println("🕵️ 7 rolled – bandit placement triggered.");
            waitingForBandit = true;

            boardView.promptBanditPlacement(board);
            boardView.setOnBanditPlaced(this::handleBanditPlaced);
            return; // ⛔ Skip normal distribution logic
        }

        // 🧠 Only distribute if not blocked
        for (HexTile tile : board.getBoard().values()) {
            if (tile.getDiceNumber() == result && !tile.isBlocked()) {
                Resources resource = tile.getResourceType();
                Node[] nodes = tile.getHexTileNodes();

                for (Node node : nodes) {
                    int nodeId = node.getId();

                    for (Player player : players) {
                        if (player.ownsSettlementAt(nodeId)) {
                            if (bank.takeResource(resource, 1)) {
                                player.addResource(resource, 1);
                                System.out.println("🌾 " + resource + " given to " + player.getName() + " at node " + nodeId);
                            }
                        }
                    }
                }
            }
//            else if (tile.getDiceNumber() == result && tile.isBlocked()) {
//                 System.out.println("🚫 Skipped tile with dice " + result + " due to bandit.");
//            }
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
    private void handleBanditPlaced(IntTupel coord) {
        waitingForBandit = false;

        // Unblock all tiles first
        for (HexTile tile : board.getBoard().values()) {
            tile.setBlocked(false);
        }

        HexTile selectedTile = board.getBoard().get(coord);
        if (selectedTile != null) {
            selectedTile.setBlocked(true);
            System.out.println("🚫 Bandit placed on tile at: " + coord.q() + "," + coord.r());
        }

        currentPlayerDiceRolls++; // Now allow ending turn or other actions
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }



}
