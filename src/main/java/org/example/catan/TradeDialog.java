package org.example.catan;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TradeDialog extends Dialog<Void> {
    public TradeDialog(Player player, Bank bank, BoardView boardView) {
        setTitle("Trade with Bank");

        Label giveLabel = new Label("Give Resource:");
        ComboBox<Resources> giveResource = new ComboBox<>();
        giveResource.getItems().addAll(Resources.values());

        Label getLabel = new Label("Get Resource:");
        ComboBox<Resources> getResource = new ComboBox<>();
        getResource.getItems().addAll(Resources.values());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, giveLabel, giveResource);
        grid.addRow(1, getLabel, getResource);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Resources giveRes = giveResource.getValue();
                    Resources getRes = getResource.getValue();

                    if (giveRes == null || getRes == null || giveRes == getRes) {
                        showAlert("Invalid resource selection!");
                        return null;
                    }

                    if (player.getResourceCount(giveRes) >= 4 && bank.takeResource(getRes, 1)) {
                        player.removeResource(giveRes, 4);
                        player.addResource(getRes, 1);
                        bank.giveResource(giveRes, 4);
                        boardView.updateResourceDisplay(); // ✅ Anzeige aktualisieren
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
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


