package library.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private JFXTextField nDaysWithoutFine;

    @FXML
    private JFXTextField finePerDay;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    void handleCancelButtonAction(ActionEvent event) {

    }

    @FXML
    void handleSaveButtonAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
