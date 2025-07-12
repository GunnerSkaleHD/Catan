package org.example.catan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Catan application.
 * Initializes the JavaFX environment and loads the main game UI from FXML.
 */
public class CatanApplication extends Application {

    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the primary stage for the JavaFX application.
     * Loads the FXML layout and sets up the main scene for the game window.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setTitle("Catan Board");
        stage.setScene(scene);
        stage.show();
    }
}
