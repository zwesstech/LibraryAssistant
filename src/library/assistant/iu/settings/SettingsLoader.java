package library.assistant.iu.settings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;

public class SettingsLoader extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/library/assistant/iu/settings/settings.fxml"));
        primaryStage.setTitle("Settings");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(()-> {
            DatabaseHandler.getInstance();
        }).start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
