package library.assistant.iu.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.iu.main.MainController;
import library.assistant.iu.settings.Preferences;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final static Logger LOGGER = LogManager.getLogger(LoginController.class.getName());

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    Preferences preferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.getPreferences();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String uname = username.getText();
        String pword = DigestUtils.shaHex(password.getText());

        if (uname.equals(preferences.getUsername()) && pword.equals(preferences.getPassword())){
            closeStage();
            loadMain();
            LOGGER.log(Level.INFO, "User successfully logged in ()", uname);
        }else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }

    @FXML
   private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage)username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/iu/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "()", e);
        }
    }

}
