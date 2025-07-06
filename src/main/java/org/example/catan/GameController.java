package org.example.catan;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.example.catan.Graph.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {

    private final Set<Integer> blockedNodes = new HashSet<>();
    private final List<Player> players = new ArrayList<>();
    private CatanBoard board;  // assumes 3 is the correct config/depth
    @FXML
    private Pane boardPane;
    private BoardView boardView;
    private Player currentPlayer;
    private int[][] adjacencyMatrix;

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


        // Dummy players
        players.add(new Player(Color.WHITE));
        players.add(new Player(Color.ORANGE));
        players.add(new Player(Color.BLUE));
        players.add(new Player(Color.RED));
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


        // Add resize listeners for dynamic board scaling
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            boardPane.getChildren().clear();
            this.boardView = new BoardView(boardPane, board, adjacencyMatrix);
            boardView.setCurrentPlayer(currentPlayer);
            boardView.setOnVertexClickHandler(this::handleVertexClick);
            boardView.setOnRoadClickHandler(this::handleEdgeClick); // ✅ Use proper method
        };


        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);

        // Initial click handler setup
        boardView.setOnVertexClickHandler(this::handleVertexClick);
        boardView.setOnRoadClickHandler(this::handleEdgeClick);


    }

    private void handleEdgeClick(Line ghostLine) {
        int[] nodes = (int[]) ghostLine.getUserData();
        if (nodes == null || nodes.length != 2) {
            System.out.println("❌ Invalid edge click data.");
            return;
        }

        if (!canBuildStreet(currentPlayer)) {
//            System.out.println("❌ Not enough resources to build a street.");
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
        if (!canBuildSettlement(currentPlayer)) {
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
    }

}
