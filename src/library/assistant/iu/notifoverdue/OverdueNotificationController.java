package library.assistant.iu.notifoverdue;

import com.google.common.collect.ImmutableList;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DataHelper;
import library.assistant.database.DatabaseHandler;
import library.assistant.data.model.MailServerInfo;
import library.assistant.iu.notifoverdue.emailsender.EmailSenderController;
import library.assistant.iu.settings.Preferences;
import library.assistant.util.LibraryAssistantUtil;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OverdueNotificationController implements Initializable {

    private ObservableList<NotificationItem> list = FXCollections.observableArrayList();

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableView<NotificationItem> tableView;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkForMailServerConfig();
        initialize();
        loadData();
    }

    private void initialize() {
        colNotify.setCellValueFactory(new NotificationControlCellValueFactory());
        colMemID.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("memberEmail"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colDays.setCellValueFactory(new PropertyValueFactory<>("dayCount"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        tableView.setItems(list);
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
            while (rs.next()) {
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

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void checkForMailServerConfig() {
        JFXButton button = new JFXButton("Okay");
        button.setOnAction((ActionEvent event) -> {
            ((Stage) rootPane.getScene().getWindow()).close();
        });
        MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();
        System.out.println(mailServerInfo);
        if (mailServerInfo == null || !mailServerInfo.validate()) {
            System.out.println("Mail server configured");
            AlertMaker.showMaterialDialog(rootPane, contentPane, ImmutableList.of(button), "Mail server is not configured", "Please configure mail server first.\nIt is available under Settings");
        }
    }

    @FXML
    void handleSendNotificationAction(ActionEvent event) {
        List<NotificationItem> selectedItems = list.stream().filter(item -> item.isNotify()).collect(Collectors.toList());
        if (selectedItems.isEmpty()) {
            AlertMaker.showErrorMessage("Nothing Selected", "Nothing selected to notify");
            return;
        }
        Object controller = LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/notifoverdue/emailsender/email_sender.fxml"), "Notify Overdue", null);
        if (controller != null) {
            EmailSenderController cont = (EmailSenderController) controller;
            cont.start();
        }

    }

    private class NotificationControlCellValueFactory implements Callback<TableColumn.CellDataFeatures<NotificationItem, JFXCheckBox>, ObservableValue<JFXCheckBox>> {
        @Override
        public ObservableValue<JFXCheckBox> call(TableColumn.CellDataFeatures<NotificationItem, JFXCheckBox> param) {
            NotificationItem item = param.getValue();
            JFXCheckBox checkBox = new JFXCheckBox();
            checkBox.selectedProperty().setValue(item.isNotify());
            checkBox.selectedProperty().addListener((ov, old, new_val) -> {
                item.setNotify(new_val);
            });
            return new SimpleObjectProperty<>(checkBox);
        }
    }
}
