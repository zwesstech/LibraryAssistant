package library.assistant.iu.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;

public class Main extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
            primaryStage.setTitle("Library App");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            DatabaseHandler.getInstance();
        }

        public static void main(String[] args) {
            launch(args);
        }
}
