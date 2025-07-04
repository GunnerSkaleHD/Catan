package org.example.catan;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.beans.value.ChangeListener;
import org.example.catan.Graph.Node;

import java.util.*;

public class BoardController {

    @FXML
    private Pane boardPane;
    private BoardView boardView;

    private List<Player> players = new ArrayList<>();
    private Player currentPlayer;

    @FXML
    public void initialize() {
        this.boardView = new BoardView(boardPane);

        // Initialize board with radius 3 (standard Catan board)
        boardView.initializeBoard(3);

        // Create dummy players
        players.add(new Player("Player 1", Color.BLUE));
        players.add(new Player("Player 2", Color.ORANGE));
        currentPlayer = players.get(0);

        // Add size listener for window resizing
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> boardView.positionVisuals();
        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);
        boardView.positionVisuals();

        // Set click handler for settlements
        boardView.setOnVertexClickHandler(this::handleVertexClick);
    }

    private void handleVertexClick(Circle clickedVertex) {
        // Get the node associated with this circle
        Node node = boardView.getNodeFromCircle(clickedVertex);

        // TODO: Add game logic validation here
        // - Check if settlement can be placed (distance rule, etc.)
        // - Check if player has resources
        // - Update game state

        System.out.println("Placing settlement at node: " + node.getId());

        // Place settlement
        boardView.drawSettlement(clickedVertex, currentPlayer.getColor());

        // Switch to next player (simple implementation)
        currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    }

    // Method to get access to the CatanBoard for game logic
    public CatanBoard getCatanBoard() {
        return boardView.getCatanBoard();
    }
}