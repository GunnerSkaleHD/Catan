package org.example.catan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class CatanApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(@NotNull Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Catan Board");
        stage.setScene(scene);
        stage.show();
    }
}
