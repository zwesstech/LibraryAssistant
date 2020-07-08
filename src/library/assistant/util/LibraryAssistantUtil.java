package library.assistant.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.iu.main.MainController;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryAssistantUtil {
    public static final String IMAGE_LOC = "/resources/icon.png";

    public static void setStageIcon(Stage stage){
        stage.getIcons().add(new Image(IMAGE_LOC));
    }

    public static void loadWindow(URL loc, String title, Stage parentStage) {
        try {
            Parent parent = FXMLLoader.load(loc);
            Stage stage = null;
            if (parentStage != null){
                stage = parentStage;
            }else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
