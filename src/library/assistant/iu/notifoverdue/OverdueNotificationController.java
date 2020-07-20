package library.assistant.iu.notifoverdue;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import library.assistant.database.DatabaseHandler;
import library.assistant.iu.listbook.BookListController;
import library.assistant.settings.Preferences;
import library.assistant.util.LibraryAssistantUtil;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class OverdueNotificationController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<NotificationItem, JFXCheckBox> colNotify;

    @FXML
    private TableColumn<NotificationItem, String> colMemID;

    @FXML
    private TableColumn<NotificationItem, String> colMemberName;

    @FXML
    private TableColumn<NotificationItem, String> colEmail;

    @FXML
    private TableColumn<NotificationItem, String> colBookName;

    @FXML
    private TableColumn<NotificationItem, Integer> colDays;

    @FXML
    private TableColumn<NotificationItem, Float> colFineAmount;

   private ObservableList<NotificationItem> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initialize();
        loadData();
    }

    private void initialize(){
        colNotify.setCellValueFactory(new NotificationControlCellValueFactory());
        colMemID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("memberEmail"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("dayCount"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
    }


    private void loadData() {
        Preferences preferences = Preferences.getPreferences();
        Long overdueBegin = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(preferences.getDaysWithoutFine());

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String query = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, MEMBER.name, MEMBER.id, MEMBER.email, BOOK.title FROM ISSUE\n"
                + "LEFT OUTER JOIN MEMBER\n"
                + "ON MEMBER.id = ISSUE.memberID\n"
                + "LEFT OUTER JOIN BOOK\n"
                + "ON BOOK.id = ISSUE.bookID\n"
                + "WHERE ISSUE.issueTime < ?";
try {
    PreparedStatement statement = handler.getConnection().prepareStatement(query);
    statement.setTimestamp(1, new Timestamp(overdueBegin));
    ResultSet rs = statement.executeQuery();
    int counter = 0;
    while (rs.next()){
        counter += 1;
        String memberName = rs.getString("name");
        String memberID = rs.getString("memberID");
        String email = rs.getString("email");
        String bookID = rs.getString("bookID");
        String bookTitle = rs.getString("title");
        Timestamp issueTime = rs.getTimestamp("issueTime");
        System.out.println("Issued on " + issueTime);
        Integer days = Math.toIntExact(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - issueTime.getTime())) + 1;
        Float fine = LibraryAssistantUtil.getFineAmount(days);

        NotificationItem item = new NotificationItem(true, memberID, memberName, email, bookTitle, LibraryAssistantUtil.getDateString(issueTime), days, fine);
        list.add(item);
    }

}catch (SQLException ex){
    ex.printStackTrace();

}

    }

    @FXML
    void handleSendNotificationAction(ActionEvent event) {

    }

    private class NotificationControlCellValueFactory implements Callback<TableColumn.CellDataFeatures<NotificationItem, JFXCheckBox>, ObservableValue<JFXCheckBox>> {
        @Override
        public ObservableValue<JFXCheckBox> call(TableColumn.CellDataFeatures<NotificationItem, JFXCheckBox> param) {
            NotificationItem item = param.getValue();
            JFXCheckBox checkBox = new JFXCheckBox();
            checkBox.selectedProperty().setValue(item.isNotify());

            return null;
        }
    }
}
