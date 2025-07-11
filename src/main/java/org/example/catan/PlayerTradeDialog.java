package org.example.catan;

import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PlayerTradeDialog extends Dialog<Void> {

    public PlayerTradeDialog(Player currentPlayer, List<Player> players) {
        setTitle("Trade with Player");

        ComboBox<Player> targetPlayerBox = new ComboBox<>();
        for (Player p : players) {
            if (!p.equals(currentPlayer)) targetPlayerBox.getItems().add(p);
        }

        ComboBox<Resources> giveRes = new ComboBox<>();
        giveRes.getItems().addAll(Resources.values());

        TextField giveAmount = new TextField();

        ComboBox<Resources> getRes = new ComboBox<>();
        getRes.getItems().addAll(Resources.values());

        TextField getAmount = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.addRow(0, new Label("Trade With:"), targetPlayerBox);
        grid.addRow(1, new Label("Give Resource:"), giveRes);
        grid.addRow(2, new Label("Give Amount:"), giveAmount);
        grid.addRow(3, new Label("Get Resource:"), getRes);
        grid.addRow(4, new Label("Get Amount:"), getAmount);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Player target = targetPlayerBox.getValue();
                    Resources give = giveRes.getValue();
                    Resources want = getRes.getValue();
                    int giveAmt = Integer.parseInt(giveAmount.getText());
                    int wantAmt = Integer.parseInt(getAmount.getText());

                    if (currentPlayer.getResourceCount(give) >= giveAmt &&
                        target.getResourceCount(want) >= wantAmt) {
                        currentPlayer.removeResource(give, giveAmt);
                        currentPlayer.addResource(want, wantAmt);
                        target.removeResource(want, wantAmt);
                        target.addResource(give, giveAmt);
                    } else {
                        showAlert("Trade failed!");
                    }

                } catch (Exception e) {
                    showAlert("Invalid input!");
                }
            }
            return null;
        });

        showAndWait();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.showAndWait();
    }
}


