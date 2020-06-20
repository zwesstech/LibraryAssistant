package library.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultValues();
    }

    @FXML
    private JFXTextField nDaysWithoutFine;

    @FXML
    private JFXTextField finePerDay;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {

    }

    @FXML
   private void handleSaveButtonAction(ActionEvent event) {

    }

    private void initDefaultValues(){
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));
    }
}
