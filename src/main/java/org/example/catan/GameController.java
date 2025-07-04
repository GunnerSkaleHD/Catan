package org.example.catan;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private CatanBoard board = new CatanBoard(3); // assumes 3 is the correct config/depth
    @FXML
    private Pane boardPane;
    private BoardView boardView;

    private final List<StandardPlayer> players = new ArrayList<>();
    private StandardPlayer currentPlayer;

    @FXML
    public void initialize() {
        // Instantiate BoardView with the board model
        this.boardView = new BoardView(boardPane, board);

        // Dummy players
        players.add(new StandardPlayer(Color.BLUE));
        players.add(new StandardPlayer(Color.ORANGE));
        currentPlayer = players.get(0);

        // Add resize listeners for dynamic board scaling
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            boardPane.getChildren().clear();
            this.boardView = new BoardView(boardPane, board); // reload view with new size
            boardView.setOnVertexClickHandler(this::handleVertexClick);
        };

        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);

        // Initial click handler setup
        boardView.setOnVertexClickHandler(this::handleVertexClick);
    }

    private void handleVertexClick(Circle clickedVertex) {
        boardView.drawSettlement(clickedVertex, currentPlayer.getColor());
        // You could cycle to next player here, e.g.:
        // currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
    }
}
