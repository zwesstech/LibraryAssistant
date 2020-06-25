package library.assistant.iu.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.iu.listbook.BookListController;
import library.assistant.iu.listmember.MemberListController;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField mobile;

    @FXML
    private JFXTextField email;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    private Boolean isInEditMode = Boolean.FALSE;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = DatabaseHandler.getInstance();
    }

    @FXML
    void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();

        Boolean flag = mName.isEmpty() || mID.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty();
        if (flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }

        if (isInEditMode){
            handleUpdateMember();
            return;
        }



        String st = "INSERT INTO MEMBER VALUES (" +
                "'" + mID +"',"+
                "'" + mName +"',"+
                "'" + mMobile +"',"+
                "'" + mEmail +"'"+
                ")";
        System.out.println(st);
        if (handler.execAction(st)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error Occured");
            alert.showAndWait();

        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    public void inflateUI(MemberListController.Member member){
        name.setText(member.getName());
        id.setText(member.getId());
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;

    }

    private void handleUpdateMember() {
        MemberListController.Member member = new MemberListController.Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if (DatabaseHandler.getInstance().updateMember(member)){
            AlertMaker.showSimpleAlert("Success", "Member Updated");
        }else{
            AlertMaker.showErrorMessage("Failed", "Can't update member");
        }
    }

}
