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

/**
 * Controls the core game flow for the Catan board game, including initialization, turn logic,
 * dice rolls, resource distribution, bandit placement, and player interaction.
 * Coordinates between the UI (BoardView) and game logic (CatanBoard, Players, Dice).
 */
public class GameController {
    private final Set<Integer> blockedNodes = new HashSet<>();
    private final List<Player> players = new ArrayList<>();
    private final List<TradeOffer> activeTrades = new ArrayList<>();
    @FXML
    private Pane boardPane;
    private CatanBoard board;
    private BoardView boardView;
    private Player currentPlayer;
    private int[][] adjacencyMatrix;
    private int currentPlayerIndex;
    private int currentPlayerDiceRolls;
    private Bank bank;
    private boolean waitingForBandit = false;


    /**
     * Sets up the visual board, event handlers, and interactive elements for gameplay.
     * Includes click handling for roads, settlements, turn transitions, and dice rolls.
     */
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

    /**
     * Called automatically by JavaFX after FXML loading.
     * Initializes player list, board state, and game flow.
     * Also populates the adjacency matrix used for graph logic.
     */
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


        Platform.runLater(() -> boardView.setCurrentPlayer(currentPlayer));


        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            boardPane.getChildren().clear();
            this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
            boardView.setCurrentPlayer(currentPlayer);
            boardView.setOnVertexClickHandler(this::handleVertexClick);
            boardView.setOnRoadClickHandler(this::handleEdgeClick);
            boardView.setOnEndTurn(this::nextPlayer);
            boardView.setOnRollDice(this::rollDice);
            boardView.setOnTradeOfferSubmitted(this::handleTradeOffer);
            updateTradeViewerUI();
        };


        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);

        boardPane.setStyle("-fx-background-color: LIGHTBLUE;");

    }

    /**
     * Removes trade offers from the UI and backend that are no longer valid
     * for the given player.
     *
     * @param player The player whose trades should be cleared.
     */
    private void removeExpiredTrades(Player player) {
        activeTrades.removeIf(offer -> offer.getSender().equals(player));
    }

    /**
     * Processes the acceptance of a trade offer.
     * Updates resources of involved players and refreshes the trade UI.
     *
     * @param offer The trade offer being accepted.
     */
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
            showAlert("You don't have enough resources to accept this trade.");
            return;
        }

        if (!sender.removeResource(giveRes, giveAmt)) {
            showAlert("Sender doesn't have enough resources anymore.");
            receiver.addResource(wantRes, wantAmt);
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

    /**
     * Refreshes the trade UI to show all currently available offers from other players.
     */
    private void updateTradeViewerUI() {
        boardView.showActiveTrades(activeTrades, this::acceptTradeOffer);
    }

    /**
     * Handles rolling the dice for the current player.
     * Prevents rolling if already rolled or if waiting for bandit placement.
     * Rolls two dice, updates the dice display, and distributes resources.
     * If a 7 is rolled, triggers bandit placement. Otherwise, gives resources to players with settlements on
     * matching tiles.
     * Increments the dice roll counter and updates the resource display.
     */
    private void rollDice() {
        if (currentPlayerDiceRolls > 0) {
            return;
        }
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }

        int result = new Dice(2).rollDice();

        Platform.runLater(() -> boardView.updateDiceNumber(result));


        if (result == 7) {
            waitingForBandit = true;
            stealFromRandomPlayer(currentPlayer, players);
            boardView.promptBanditPlacement(board);
            boardView.setOnBanditPlaced(this::handleBanditPlaced);
            return;
        }

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
                            }
                        }
                    }
                }
            }
        }

        currentPlayerDiceRolls++;
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    /**
     * Switches to the next player's turn in the game.
     * Advances the index, sets the current player, updates the UI,
     * and prompts the player to roll dice. Also cleans up expired trades.
     */
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
    }

    /**
     * Handles an incoming trade offer from a player.
     * If the trade is with the bank, processes it directly.
     * Otherwise, adds the trade offer to the list of active trades.
     *
     * @param offer the trade offer to be processed
     */
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

    /**
     * Handles user interaction when clicking a ghost road (edge).
     * Validates game state, player resources, and street placement before
     * updating both the model and view.
     *
     * @param ghostLine the line representing the clicked edge
     */
    private void handleEdgeClick(Line ghostLine) {
        int[] nodes = (int[]) ghostLine.getUserData();
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }


        if (nodes == null || nodes.length != 2) {
            return;
        }
        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
            return;
        }


        if (!canBuildStreet(currentPlayer) || !bank.useStreet()) {
            return;
        }


        boolean placed = currentPlayer.placeStreet(nodes[0], nodes[1]);
        if (!placed) {
            return;
        }

        boardView.placeRoad(ghostLine, currentPlayer.getColor());
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    /**
     * Handles user interaction when clicking a vertex (node) to place a settlement.
     * Checks if placement is allowed, updates the game model and view,
     * and blocks surrounding nodes according to Catan rules.
     *
     * @param clickedVertex the circle representing the clicked vertex
     */
    private void handleVertexClick(Circle clickedVertex) {
        Node node = (Node) clickedVertex.getUserData();
        int nodeId = node.getId();
        if (waitingForBandit) {
            showAlert("Please place the bandit before continuing.");
            return;
        }
        if (!canBuildSettlement(currentPlayer) || !bank.useSettlement()) {
            return;
        }

        if (currentPlayerDiceRolls == 0) {
            showAlert("You must roll the dice before interacting with anything else.");
            return;
        }

        if (blockedNodes.contains(nodeId)) {
            return;
        }
        boolean placed = currentPlayer.placeSettlement(nodeId);
        if (!placed) {
            return;
        }

        boardView.placeSettlement(clickedVertex, currentPlayer.getColor());

        blockedNodes.add(nodeId);
        boardView.hideVertexByNodeId(nodeId);

        for (int i = 0; i < adjacencyMatrix[nodeId].length; i++) {
            if (adjacencyMatrix[nodeId][i] == 1) {
                blockedNodes.add(i);
                boardView.hideVertexByNodeId(i);
            }
        }
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    /**
     * Processes a trade with the bank.
     * Validates that the player gives exactly 4 of one resource and has enough in inventory.
     * If valid, completes the trade by removing and adding resources accordingly.
     *
     * @param offer the trade offer directed at the bank
     */
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

    /**
     * Handles logic after the bandit is placed on a tile.
     * Updates game model by marking the selected tile as blocked and unblocking all others.
     *
     * @param coord the coordinate of the tile where the bandit was placed
     */
    private void handleBanditPlaced(IntTupel coord) {
        waitingForBandit = false;
        HexTile selectedTile = board.getBoard().get(coord);
        if (selectedTile != null) {
            selectedTile.setBlocked(true);
        }

        currentPlayerDiceRolls++;
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }

    /**
     * Converts a JavaFX Color object to a readable color name string.
     *
     * @param color the color to convert
     * @return the string representation of the color
     */
    private String colorToString(Color color) {
        if (Color.RED.equals(color)) return "Red";
        if (Color.BLUE.equals(color)) return "Blue";
        if (Color.YELLOW.equals(color)) return "Yellow";
        if (Color.WHITE.equals(color)) return "White";
        if (Color.ORANGE.equals(color)) return "Orange";
        return "Unknown Color";
    }

    /**
     * Checks if a player has enough resources to build a street.
     *
     * @param player the player to check
     * @return true if the player has at least 1 wood and 1 brick, false otherwise
     */
    private boolean canBuildStreet(Player player) {
        return player.getResourceCount(Resources.WOOD) >= 1 &&
                player.getResourceCount(Resources.BRICK) >= 1;
    }

    /**
     * Checks if a player has enough resources to build a settlement.
     *
     * @param player the player to check
     * @return true if the player has at least 1 wood, brick, wheat, and sheep, false otherwise
     */
    private boolean canBuildSettlement(Player player) {
        return player.getResourceCount(Resources.WOOD) >= 1 &&
                player.getResourceCount(Resources.BRICK) >= 1 &&
                player.getResourceCount(Resources.WHEAT) >= 1 &&
                player.getResourceCount(Resources.SHEEP) >= 1;

    }

    /**
     * Displays an informational alert dialog with a given message.
     *
     * @param message the message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Steals a random resource from a random other player.
     * This method selects a random player from the provided player list (excluding the current player),
     * who has at least one resource card. It then steals a random resource type (that the victim actually owns)
     * and transfers one card of that type from the victim to the current player. If no eligible victims exist,
     * the method displays an alert and does nothing.
     *
     * @param playerList    The list of all players in the game.
     * @param currentPlayer The player who is performing the steal (the one who rolled a 7).
     */
    private void stealFromRandomPlayer(Player currentPlayer, List<Player> playerList) {
        List<Player> candidates = new ArrayList<>();
        for (Player p : playerList) {
            if (!p.equals(currentPlayer)) {
                int total = 0;
                for (Resources res : Resources.values()) {
                    if (res != Resources.NONE) {
                        total += p.getResourceCount(res);
                    }
                }
                if (total > 0) {
                    candidates.add(p);
                }
            }
        }
        if (candidates.isEmpty()) {
            showAlert("No player has any resources to steal.");
            return;
        }

        Player victim = candidates.get(new Random().nextInt(candidates.size()));

        List<Resources> victimResources = new ArrayList<>();
        for (Resources res : Resources.values()) {
            if (res != Resources.NONE && victim.getResourceCount(res) > 0) {
                victimResources.add(res);
            }
        }
        if (victimResources.isEmpty()) {
            showAlert(victim.getName() + " has no resources to steal.");
            return;
        }

        Resources stolen = victimResources.get(new Random().nextInt(victimResources.size()));
        victim.removeResource(stolen, 1);
        currentPlayer.addResource(stolen, 1);

        showAlert(currentPlayer.getName() + " stole 1 " + stolen.toString().toLowerCase() + " from " + victim.getName() + "!");
        Platform.runLater(() -> boardView.updateResourceDisplay());
    }
}
