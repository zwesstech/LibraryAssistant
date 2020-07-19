package library.assistant.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private StackPane rootContainer;

    @FXML
    private JFXTextField nDaysWithoutFine;

    @FXML
    private JFXTextField finePerDay;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXTextField serverName;

    @FXML
    private JFXTextField smtpPort;

    @FXML
    private JFXTextField emailAddress;

    @FXML
    private JFXPasswordField emailPassword;

    @FXML
    private JFXCheckBox sslCheckbox;

    @FXML
    private JFXSpinner progressSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultValues();
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }

    @FXML
   private void handleSaveButtonAction(ActionEvent event) {
        int nDays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String uName = username.getText();
        String pass = password.getText();

        Preferences preferences = Preferences.getPreferences();
        preferences.setDaysWithoutFine(nDays);
        preferences.setFinePerDay(fine);
        preferences.setUsername(uName);
        preferences.setPassword(pass);

        Preferences.writePreferenceToFile(preferences);

    }

    private Stage getStage(){
        return ((Stage) nDaysWithoutFine.getScene().getWindow());
    }

    private void initDefaultValues(){
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        String passHash = String.valueOf(preferences.getPassword());
        password.setText(String.valueOf(preferences.getPassword()));

    }

    @FXML
    void handleTestMailAction(ActionEvent event) {

    }

    @FXML
    void saveMailServerConfiguration(ActionEvent event) {

    }

    @FXML
    void handleDatabaseExportAction(ActionEvent event) {

    }

}
