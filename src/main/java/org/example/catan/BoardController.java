package org.example.catan;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;


public class BoardController {

    @FXML
    private Pane boardPane;
    private BoardView boardView;

    private List<StandardPlayer> players = new ArrayList<>(); //Dummyklasse für Spieler
    private StandardPlayer currentPlayer;

    @FXML
    public void initialize() {
        this.boardView = new BoardView(boardPane);

        boardView.createAndColorTiles();
        boardView.initializeVertexPoints();

        //Dummy-Spieler
        players.add(new StandardPlayer("Color.BLUE"));
        players.add(new StandardPlayer("Color.ORANGE"));
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