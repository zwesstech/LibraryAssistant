package library.assistant.iu.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.iu.main.MainController;
import library.assistant.settings.Preferences;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    @FXML
    private Label titleLabel;

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
        titleLabel.setText("Library Assistant Login");
        titleLabel.setStyle("-fx-background-color: black; -fx-text-fill: white");

        String uname = username.getText();
        String pword = DigestUtils.sha1Hex(password.getText());

        if (uname.equals(preferences.getUsername()) && pword.equals(preferences.getPassword())){
            closeStage();
            loadMain();
        }else {
            titleLabel.setText("Invalid Credentials");
            titleLabel.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white");
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
            stage.setTitle("Library App");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
