package org.example.catan;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.beans.value.ChangeListener;

import java.util.*;

public class BoardController {

    @FXML
    private Pane boardPane;
    private BoardView boardView;

    private List<Player> players = new ArrayList<>(); //Dummyklasse für Spieler
    private Player currentPlayer;

    @FXML
    public void initialize() {
        this.boardView = new BoardView(boardPane);

        boardView.createAndColorTiles();
        boardView.initializeVertexPoints();

        //Dummy-Spieler
        players.add(new Player("Player 1", Color.BLUE));
        players.add(new Player("Player 2", Color.ORANGE));
        currentPlayer = players.get(0);

        //sizeListener für Fenstergröße
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> boardView.positionVisuals();
        boardPane.widthProperty().addListener(sizeListener);
        boardPane.heightProperty().addListener(sizeListener);
        boardView.positionVisuals();

        boardView.setOnVertexClickHandler(this::handleVertexClick);

    }

    private void handleVertexClick(Circle clickedVertex) {
        //Später Logik für Siedlungsplatzierung
        boardView.drawSettlement(clickedVertex, currentPlayer.getColor());
    }

}