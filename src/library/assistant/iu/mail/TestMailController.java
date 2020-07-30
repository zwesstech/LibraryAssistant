package library.assistant.iu.mail;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import library.assistant.alert.AlertMaker;
import library.assistant.data.callback.GenericCallback;
import library.assistant.data.model.MailServerInfo;
import library.assistant.email.EmailUtil;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;



public class TestMailController implements Initializable, GenericCallback {

    private static final Logger LOGGER = LogManager.getLogger(TestMailController.class.getName());

    private MailServerInfo mailServerInfo;

    @FXML
    private JFXTextField recipientAddressInput;

    @FXML
    private JFXProgressBar progressBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void sendMailServer(MailServerInfo mailServerInfo){
        this.mailServerInfo = mailServerInfo;
    }

    @FXML
    void handleStartAction(ActionEvent event) {
        String toAddress = recipientAddressInput.getText();
        if (LibraryAssistantUtil.validateEmailAddress(toAddress)){
            EmailUtil.sendTestMail(mailServerInfo, toAddress, this);
            progressBar.setVisible(true);
        }else {
            AlertMaker.showErrorMessage("Failed", "Invalid email address");
        }

    }

    @Override
    public Object taskCompleted(Object val) {
        LOGGER.log(Level.INFO, "Callback received from Email Sender client ()", val);
        boolean result = (boolean) val;
        Platform.runLater(() -> {
            if (result){
                AlertMaker.showSimpleAlert("Success", "Email successfully sent!");
            }else {
                AlertMaker.showErrorMessage("Failed", "Something went wrong");
            }
            progressBar.setVisible(false);
        });
        return true;
    }
}
