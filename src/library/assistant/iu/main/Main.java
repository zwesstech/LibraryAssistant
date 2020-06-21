package library.assistant.iu.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

public class Main extends Application {
        @Override
        public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/library/assistant/iu/login/login.fxml"));

            stage.setTitle("Library App");
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

            LibraryAssistantUtil.setStageIcon(stage);

            new Thread(()-> {
                    DatabaseHandler.getInstance();
            }).start();

        }

        public static void main(String[] args) {
            launch(args);
        }
}
