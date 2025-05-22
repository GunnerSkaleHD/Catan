package org.example.catan;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardController {
    private final Random random = new Random();
    @FXML
    private Polygon TileRow1_1;
    @FXML
    private Polygon TileRow1_2;
    @FXML
    private Polygon TileRow1_3;
    @FXML
    private Polygon TileRow2_1;
    @FXML
    private Polygon TileRow2_2;
    @FXML
    private Polygon TileRow2_3;
    @FXML
    private Polygon TileRow2_4;
    @FXML
    private Polygon TileRow3_1;
    @FXML
    private Polygon TileRow3_2;
    @FXML
    private Polygon TileRow3_3;
    @FXML
    private Polygon TileRow3_4;
    @FXML
    private Polygon TileRow3_5;
    @FXML
    private Polygon TileRow4_1;
    @FXML
    private Polygon TileRow4_2;
    @FXML
    private Polygon TileRow4_3;
    @FXML
    private Polygon TileRow4_4;
    @FXML
    private Polygon TileRow5_1;
    @FXML
    private Polygon TileRow5_2;
    @FXML
    private Polygon TileRow5_3;

    @FXML
    public void randomizeTileColors() {
        List<Color> tileColors = Arrays.asList(Color.FORESTGREEN, Color.FORESTGREEN, Color.FORESTGREEN,
                Color.FORESTGREEN, Color.FIREBRICK, Color.FIREBRICK, Color.FIREBRICK, Color.GOLD, Color.GOLD,
                Color.GOLD, Color.GOLD, Color.LIGHTGREEN, Color.LIGHTGREEN, Color.LIGHTGREEN, Color.LIGHTGREEN,
                Color.GRAY, Color.GRAY, Color.GRAY);

        Collections.shuffle(tileColors, new Random());

        List<Polygon> tiles = Arrays.asList(TileRow1_1, TileRow1_2, TileRow1_3, TileRow2_1, TileRow2_2, TileRow2_3,
                TileRow2_4, TileRow3_1, TileRow3_2, /*TileRow3_3 (missing?),*/ TileRow3_4, TileRow3_5, TileRow4_1,
                TileRow4_2, TileRow4_3, TileRow4_4, TileRow5_1, TileRow5_2, TileRow5_3);

        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setFill(tileColors.get(i));
        }
    }


    @FXML
    public void initialize() {
        randomizeTileColors();
    }


}
