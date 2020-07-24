package library.assistant.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.iu.main.MainController;
import library.assistant.settings.Preferences;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LibraryAssistantUtil {
    public static final String IMAGE_LOC = "/resources/icon.png";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static void setStageIcon(Stage stage){
        stage.getIcons().add(new Image(IMAGE_LOC));
    }

    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        Object controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage = null;
            if (parentStage != null){
                stage = parentStage;
            }else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            setStageIcon(stage);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
        return controller;
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

    public static String getDateString(Date date){
        return DATE_FORMAT.format(date);
    }

    public static boolean validateEmailAddress(String emailID){
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(emailID).matches();
    }

    public static void openFileWithDesktop(File file){
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        }catch (IOException ex){
            Logger.getLogger(LibraryAssistantUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
