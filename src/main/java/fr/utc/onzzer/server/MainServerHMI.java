package fr.utc.onzzer.server;

import fr.utc.onzzer.server.hmi.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainServerHMI extends Application {

        @Override
        public void start(Stage stage) throws IOException {
            // Global controller that contains services references.
            GlobalController controller = new GlobalController(8000);
            controller.getComServicesProvider().start();

            // Loading the view.
            FXMLLoader fxmlLoader = new FXMLLoader(MainServerHMI.class.getResource("/fxml/main-view.fxml"));

            MainViewController mainViewController = new MainViewController(controller);
            fxmlLoader.setController(mainViewController);

            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Onzzer Server Supervisor");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.show();

        }

    public static void main(String[] args) {
        launch();
    }
}
