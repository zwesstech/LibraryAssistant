package library.assistant.iu.mail;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import library.assistant.database.MailServerInfo;
import library.assistant.email.EmailUtil;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;



public class TestMailController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(TestMailController.class.getName());

    private MailServerInfo mailServerInfo;

    @FXML
    private JFXTextField recipientAddressInput;

    @FXML
    private JFXProgressBar progressBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void handleStartAction(ActionEvent event) {
        String toAddress = recipientAddressInput.getText();
        if (LibraryAssistantUtil.validateEmailAddress(toAddress)){

        }

    }

}
