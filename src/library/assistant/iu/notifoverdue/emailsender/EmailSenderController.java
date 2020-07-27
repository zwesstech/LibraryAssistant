package library.assistant.iu.notifoverdue.emailsender;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import library.assistant.iu.notifoverdue.NotificationItem;
import library.assistant.util.LibraryAssistantUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSenderController implements Initializable {

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Text text;

    private List<NotificationItem> list;
    private StringBuilder emailText = new StringBuilder();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Scanner scanner = new Scanner(getClass().getResourceAsStream(LibraryAssistantUtil.MAIL_CONTENT_LOC));
            while (scanner.hasNext()){
                emailText.append(scanner.nextLine()).append("\n");
            }
            System.out.println(emailText);
        }catch (Exception e){
            Logger.getLogger(EmailSenderController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void setNotIfRequestData(){}
}
