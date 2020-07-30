package library.assistant.iu.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DataHelper;
import library.assistant.database.DatabaseHandler;
import library.assistant.data.model.MailServerInfo;
import library.assistant.database.export.DatabaseExporter;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private final static Logger LOGGER = LogManager.getLogger(DatabaseHandler.class.getName());

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
        MailServerInfo mailServerInfo = readMailServerInfo();
        if (mailServerInfo != null){
            if (DataHelper.updateMailServerInfo(mailServerInfo)){
                AlertMaker.showSimpleAlert("Success", "Something went wrong!");
            }
        }
    }

    @FXML
    void saveMailServerConfiguration(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailServerInfo();
        if (mailServerInfo != null){
            if (DataHelper.updateMailServerInfo(mailServerInfo)){
                AlertMaker.showErrorMessage("Failed", "Something went wrong");
            }
        }
    }

    @FXML
    void handleDatabaseExportAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location to create Backup");
        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory == null){
            AlertMaker.showErrorMessage("Export cancelled", "Noi Valid Directory Found");
        }else {
            DatabaseExporter databaseExporter = new DatabaseExporter(selectedDirectory);
            progressSpinner.visibleProperty().bind(databaseExporter.runningProperty());
            new Thread(databaseExporter).start();
        }
    }

    private void loadMailServerConfigurations(){
        MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();
        if (mailServerInfo != null){
            LOGGER.log(Level.INFO, "Mail server info loaded from DB");
            serverName.setText(mailServerInfo.getMailServer());
            smtpPort.setText(String.valueOf(mailServerInfo.getPort()));
            emailAddress.setText(mailServerInfo.getEmailID());
            emailPassword.setText(mailServerInfo.getPassword());
            sslCheckbox.setSelected(mailServerInfo.getSslEnabled());
        }
    }

    private MailServerInfo readMailServerInfo(){
        try{
            MailServerInfo mailServerInfo
                    = new MailServerInfo(serverName.getText(), Integer.parseInt(smtpPort.getText()), emailAddress.getText(), emailPassword.getText(), sslCheckbox.isSelected());
            if (!mailServerInfo.validate() || !LibraryAssistantUtil.validateEmailAddress(emailAddress.getText())){
                throw new InvalidParameterException();
            }
            return mailServerInfo;
        }catch (Exception exp){
            AlertMaker.showErrorMessage("Invalid Entries Found", "Correct input and try again");
            LOGGER.log(Level.WARN, exp);
        }
        return null;
    }

}
