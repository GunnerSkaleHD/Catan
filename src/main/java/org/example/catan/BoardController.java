package org.example.catan;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class BoardController {

    @FXML private Pane boardPane;

    // “radius” you used in Hexagon
    private static final double SIZE       = 50;
    // your shape is 2*SIZE wide and 2*SIZE tall
    private static final double HEX_WIDTH  = 2 * SIZE;     // 100
    private static final double HEX_HEIGHT = 2 * SIZE;     // 100

    // pointy-topped grid spacing:
    private static final double H_SPACING = HEX_WIDTH;      // neighbor centers horizontally
    private static final double V_SPACING = SIZE * 1.5;     // neighbor centers vertically

    // 5 rows: lengths 3,4,5,4,3
    private static final int[][] TILE_LAYOUT = {
            {0,1,2},
            {0,1,2,3},
            {0,1,2,3,4},
            {0,1,2,3},
            {0,1,2}
    };

    // your 19 terrains
    private static final String[] TERRAIN_TYPES = {
            "brick","sheep","ore",
            "wood","wheat","sheep","ore",
            "brick","wheat","wood","wheat","sheep",
            "wood","ore","brick","wood",
            "wheat","sheep","desert"
    };

    // your 19 numbers (0 = desert)
    private static final int[] TILE_NUMBERS = {
            5,2,6,
            3,8,10,9,
            12,11,6,4,10,
            9,3,11,8,
            4,5,0
    };

    private static Color getColorFor(String t) {
        return switch (t) {
            case "wheat"  -> Color.LIGHTYELLOW;
            case "wood"   -> Color.FORESTGREEN;
            case "brick"  -> Color.TOMATO;
            case "ore"    -> Color.LIGHTGRAY;
            case "sheep"  -> Color.LIGHTGREEN;
            case "desert" -> Color.BEIGE;
            default       -> Color.WHITE;
        };
    }

    @FXML
    public void initialize() {
        int idx = 0;
        double paneW = boardPane.getPrefWidth();  // e.g. 650

        for (int row = 0; row < TILE_LAYOUT.length; row++) {
            int cols = TILE_LAYOUT[row].length;
            // how wide this row is in pixels
            double rowW = (cols - 1) * H_SPACING + HEX_WIDTH;
            // center it
            double startX = (paneW - rowW) / 2;
            // drop each row by V_SPACING
            double y = row * V_SPACING;

            for (int col = 0; col < cols; col++) {
                String terrain = TERRAIN_TYPES[idx];
                int    number  = TILE_NUMBERS [idx++];
                Color  fill    = getColorFor(terrain);

                Hexagon hex = new Hexagon();
                hex.setTerrain(fill);

                Label lbl = new Label(number == 0 ? "" : String.valueOf(number));
                lbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:black;");

                StackPane cell = new StackPane(hex, lbl);
                cell.setPrefSize(HEX_WIDTH, HEX_HEIGHT);
                cell.setLayoutX(startX + col * H_SPACING);
                cell.setLayoutY(y);

                boardPane.getChildren().add(cell);
            }
        }
    }

    @FXML public void onRollDice() { /* … */ }
    @FXML public void onEndTurn()  { /* … */ }
}
