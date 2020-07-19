package library.assistant.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.iu.main.MainController;
import library.assistant.settings.Preferences;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryAssistantUtil {
    public static final String IMAGE_LOC = "/resources/icon.png";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

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

    public static Float getFineAmount(int totalDays){
        Preferences pref = Preferences.getPreferences();
        Integer fineDays = totalDays - pref.getDaysWithoutFine();
        Float fine = 0f;
        if (fineDays > 0){
            fine = fineDays * pref.getFinePerDay();
        }
        return fine;
    }

    public static String formatDateTimeString(Date date){
        return DATE_TIME_FORMAT.format(date);
    }

    public static String formatDateTimeString(Long time){
        return DATE_TIME_FORMAT.format(new Date(time));
    }
}
