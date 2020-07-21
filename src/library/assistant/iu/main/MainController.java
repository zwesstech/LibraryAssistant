package library.assistant.iu.main;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DataHelper;
import library.assistant.database.DatabaseHandler;
import library.assistant.iu.callback.BookReturnCallback;
import library.assistant.iu.issuedlist.IssuedListController;
import library.assistant.iu.main.toolbar.ToolbarController;
import library.assistant.util.LibraryAssistantUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable, BookReturnCallback {

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane bookInfoContainer;

    @FXML
    private StackPane memberInfoContainer;

    @FXML
    private JFXTabPane mainTabPane;

    @FXML
    private Tab bookIssueTab;

    @FXML
    private Tab renewTab;

    @FXML
    private HBox book_info;

    @FXML
    private HBox member_info;

    @FXML
    private TextField bookIDInput;

    @FXML
    private Text bookName;

    @FXML
    private Text bookAuthor;

    @FXML
    private Text bookStatus;

    @FXML
    private TextField memberIDInput;

    @FXML
    private Text memberName;

    @FXML
    private Text memberMobile;

    @FXML
    private JFXTextField bookID;

    @FXML
    private ListView<String> issueDataList;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXButton renewButton;

    @FXML
    private JFXButton submissionButton;

    @FXML
    private JFXButton btnIssue;

    @FXML
    private HBox submissionDataContainer;

    @FXML
    private Text memberNameHolder;

    @FXML
    private Text memberEmailHolder;

    @FXML
    private Text memberContactHolder;

    @FXML
    private Text bookNameHolder;

    @FXML
    private Text bookAuthorHolder;

    @FXML
    private Text bookPublisherHolder;

    @FXML
    private Text issueDateHolder;

    @FXML
    private Text numberDaysHolder;

    @FXML
    private Text fineInfoHolder;

    private static final String BOOK_NOT_AVAILABLE = "Not Available";
    private static final String NO_SUCH_BOOK_AVAILABLE = "No Such Book Available";
    private static final String NO_SUCH_MEMBER_AVAILABLE = "No Such Member Available";
    private static final String BOOK_AVAILABLE = "Available";

    private Boolean isReadyForSubmission = false;
    private DatabaseHandler databaseHandler;
    private PieChart bookChart;
    private PieChart memberChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXDepthManager.setDepth(book_info, 1);
        JFXDepthManager.setDepth(member_info, 1);

        databaseHandler = DatabaseHandler.getInstance();
        initDrawer();
        initGraphs();
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        enableDisableGraph(false);

        String id = bookIDInput.getText();
        ResultSet rs = DataHelper.getBookInfoWithIssueData(id);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                Timestamp issuedOn = rs.getTimestamp("issueTime");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? BOOK_AVAILABLE : String.format("Issued on %s", LibraryAssistantUtil.getDateString(new Date(issuedOn.getTime())));
                if (!bStatus){
                    bookStatus.getStyleClass().add("not-available");
                }else {
                    bookStatus.getStyleClass().remove("not-available");
                }
                bookStatus.setText(status);

                flag = true;
            }
            if (!flag) {
                bookName.setText(NO_SUCH_BOOK_AVAILABLE);
            }else {
                memberIDInput.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    void clearMemberCache() {
        memberName.setText("");
        memberMobile.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        enableDisableGraph(false);

        String id = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");

                memberName.setText(mName);
                memberMobile.setText(mMobile);

                flag = true;
            }
            if (!flag) {
                memberName.setText(NO_SUCH_MEMBER_AVAILABLE);
            }else {
                btnIssue.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    @FXML
    private void loadIssueOperation(ActionEvent event) {
        if (checkForIssueValidity()){
            JFXButton btn = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Invalid Input", null);
            return;
        }
        if (bookStatus.getText().equals(BOOK_NOT_AVAILABLE)) {
            JFXButton btn = new JFXButton("Okay");
            JFXButton viewDetails = new JFXButton("View Details");
            viewDetails.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                String bookToBeLoaded = bookIDInput.getText();
                bookID.setText(bookToBeLoaded);
                bookID.fireEvent(new ActionEvent());
                mainTabPane.getSelectionModel().select(renewTab);
            });
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn, viewDetails), "Already issued book", "This book is already issued. Cant process issue request");
            return;

        }

                String memberID = memberIDInput.getText();
                String bookID = bookIDInput.getText();

            JFXButton yesButton = new JFXButton("YES");
            yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                        + "'" + memberID + "',"
                        + "'" + bookID + "')";
                String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
                System.out.println(str + " and " + str2);

                if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)){
                    JFXButton button = new JFXButton("Done");
                    button.setOnAction((actionEvent) ->{
                        bookIDInput.requestFocus();
                    });
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Complete", null);
                refreshGraphs();
            }else {
                    JFXButton button = new JFXButton("Okay, I'll Check");
                    AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue Operation Failed", null);
                }
                clearIssueEntries();
        });
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton button = new JFXButton("That's Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue Cancelled", null);
            clearIssueEntries();
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Issue",
                String.format("Are you sure you want to Issue the book '%s' to '%s' ?", bookName.getText(), memberName.getText()));

    }

    @FXML
    void loadBookInfoTwo(ActionEvent event) {
        clearEntries();
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        try {

            String id = bookID.getText();
            String myQuery = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, ISSUE.renew_count,\n"
                    + "MEMBER.name, MEMBER.mobile, MEMBER.email,\n"
                    + "BOOK.title, BOOK.author, BOOK.publisher, BOOK.isAvail\n"
                    + "FROM ISSUE\n"
                    + "LEFT JOIN MEMBER\n"
                    + "ON ISSUE.memberID=MEMBER.ID\n"
                    + "LEFT JOIN BOOK\n"
                    + "ON ISSUE.bookID=BOOK.ID\n"
                    + "WHERE ISSUE.bookID='" + id + "'";
            ResultSet rs = databaseHandler.execQuery(myQuery);
            if (rs.next()) {
                memberNameHolder.setText(rs.getString("name"));
                memberContactHolder.setText(rs.getString("mobile"));
                memberEmailHolder.setText(rs.getString("email"));

                bookNameHolder.setText(rs.getString("title"));
                bookAuthorHolder.setText(rs.getString("author"));
                bookPublisherHolder.setText(rs.getString("publisher"));

                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                Date dateOfIssue = new Date(mIssueTime.getTime());
                issueDateHolder.setText(LibraryAssistantUtil.formatDateTimeString(dateOfIssue));
                Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                Long days = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS) + 1;
                String daysElapsed = String.format("Used %d days", days);
                numberDaysHolder.setText(daysElapsed);
                Float fine = LibraryAssistantUtil.getFineAmount(days.intValue());
                if (fine > 0){
                fineInfoHolder.setText(String.format("Fine : %.2f", LibraryAssistantUtil.getFineAmount(days.intValue())));
                }else {
                    fineInfoHolder.setText("");
                }
                isReadyForSubmission = true;
                disableEnableControls(true);
                submissionDataContainer.setOpacity(1);
            }else {
                JFXButton button = new JFXButton("Okay,I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "No such Book Exists in Issue Database", null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    private void loadSubmissionOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton button = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Please select a book to submit", "Cant simply submit a null book :-)");
            return;
        }

        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            String id = bookID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {
                JFXButton btn = new JFXButton("Done!");
                btn.setOnAction((actionEvent) -> {
                            bookIDInput.requestFocus();
                        });
                    AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been submitted", null);
                    disableEnableControls(false);
                    submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay. I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission has failed", null);
            }

        });

        JFXButton noButton = new JFXButton("NO, Cancel");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            JFXButton button = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Submission operation cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Submission Operation", "Are you sure you want to return book ?");
    }

    @FXML
    void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton button = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Please select a book to renew", null);
            return;
        }

        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE BOOKID = '" + bookID.getText() + "'";
            System.out.println(ac);
            if (databaseHandler.execAction(ac)) {
                JFXButton btn = new JFXButton("Alright");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been renewed", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew has failed", null);
            }
        });
        JFXButton noButton = new JFXButton("NO, Don't!");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            JFXButton button = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Renew operation cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Renew Operation", "Are you sure you want to renew book ?");
    }

    private Stage getStage(){
        return (Stage)rootPane.getScene().getWindow();
    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addbook/add_book.fxml"), "Add New Book", null);
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addmember/member_add.fxml"), "Add New Member", null);
    }


    @FXML
    private void handleMenuViewBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listbook/book_list.fxml"), "Book List", null);
    }

    @FXML
    private void handleMenuViewMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listmember/member_list.fxml"), "Member List", null);
    }

    @FXML
    void handleIssueList(ActionEvent event) {
        Object controller = LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/issuedlist/issued_list.fxml"), "Issued Book List", null);
        if (controller != null){
            IssuedListController cont = (IssuedListController) controller;
            cont.setBookReturnCallback(this);
        }
    }

    @FXML
    void handleMenuFullScreen(ActionEvent event) {
        Stage stage = getStage();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void initDrawer(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/iu/main/toolbar/toolbar.fxml"));
            VBox toolbar = loader.load();
            drawer.setSidePane(toolbar);
            ToolbarController controller = loader.getController();
            controller.setBookReturnCallback(this);
        }catch (IOException ex){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) ->{
            drawer.toggle();
        });
        drawer.setOnDrawerOpening(event -> {
            task.setRate(task.getRate() * -1);
            task.play();
            drawer.toFront();
        });
        drawer.setOnDrawerClosed(event -> {
            drawer.toBack();
            task.setRate(task.getRate() * -1);
            task.play();
        });
    }

  /*  private void initDrawer(){
        try {
           VBox toolbar = FXMLLoader.load(getClass().getResource("/library/assistant/iu/main/toolbar/toolbar.fxml"));
            drawer.setSidePane(toolbar);
            drawer.setDefaultDrawerSize(150);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);

        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            if (drawer.isClosed()){
                drawer.open();
                drawer.setMinWidth(200);
            }else {
                drawer.close();
                drawer.setMinWidth(0);
            }
        });
    }*/

    private void clearEntries() {
        memberNameHolder.setText("");
        memberEmailHolder.setText("");
        memberContactHolder.setText("");

        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");

        issueDateHolder.setText("");
        numberDaysHolder.setText("");
        fineInfoHolder.setText("");

        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);

    }

    private void disableEnableControls(boolean enableFlag) {
        if (enableFlag) {
            renewButton.setDisable(false);
            submissionButton.setDisable(false);
        }else {
            renewButton.setDisable(true);
            submissionButton.setDisable(true);
        }
    }

    private void clearIssueEntries() {
        bookIDInput.clear();
        memberIDInput.clear();
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
        memberMobile.setText("");
        memberName.setText("");
        enableDisableGraph(true);
    }

    private void initGraphs() {
        bookChart = new PieChart(databaseHandler.getBookGraphStatistics());
        memberChart = new PieChart(databaseHandler.getMemberGraphStatistics());
        memberInfoContainer.getChildren().add(memberChart);
        bookInfoContainer.getChildren().add(bookChart);

        bookIssueTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                clearIssueEntries();
                if (bookIssueTab.isSelected()){
                    refreshGraphs();
                }
            }
        });
    }
    private void enableDisableGraph(Boolean status){
        if (status){
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        }else {
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
    }

    private boolean checkForIssueValidity(){
        bookIDInput.fireEvent(new ActionEvent());
        memberIDInput.fireEvent(new ActionEvent());
        return bookIDInput.getText().isEmpty() || memberIDInput.getText().isEmpty()
                || memberName.getText().isEmpty() || bookName.getText().isEmpty()
                || bookName.getText().equals(NO_SUCH_BOOK_AVAILABLE) || memberName.getText().equals(NO_SUCH_MEMBER_AVAILABLE);
    }

    private void refreshGraphs(){
        bookChart.setData(databaseHandler.getBookGraphStatistics());
        memberChart.setData(databaseHandler.getMemberGraphStatistics());
    }

    @FXML
    void handleMenuOverdueNotification(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/notifoverdue/overdue_notification.fxml"), "Notify Users", null);
    }

    @Override
    public void loadBookReturn(String bookID) {

    }
}

