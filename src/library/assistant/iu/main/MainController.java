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
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
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

public class MainController implements Initializable {

    @FXML
    private StackPane rootPane;


    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private StackPane bookInfoContainer;

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

    Boolean isReadyForSubmission = false;

    DatabaseHandler databaseHandler;

    PieChart bookChart;

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
        String id = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? "Available" : "Not Available";
                bookStatus.setText(status);

                flag = true;
            }
            if (!flag) {
                bookName.setText("No Such Book Available");
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
                memberName.setText("No Such Member Available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                JFXButton button = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Complete", null);

            } else {
                JFXButton button = new JFXButton("Okay,I'll Check");
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
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Issue", "Are you sure you want to Issue the book" + bookName.getText() + " to " + memberName.getText() + " ?");

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
                issueDateHolder.setText(dateOfIssue.toString());
                Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                Long daysElapsed = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS);
                numberDaysHolder.setText(daysElapsed.toString());
                fineInfoHolder.setText("Not Supported Yet");

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
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Please select a book to submit", "Can't simply submit a null book :-)");
            return;
        }

        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            String id = bookID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if (databaseHandler.execAction(ac1) && databaseHandler.execAction(ac2)) {
                JFXButton button = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book has been Submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton button = new JFXButton("Okay. I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Submission Failed", null);
            }

        });

        JFXButton noButton = new JFXButton("NO, Cancel");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            JFXButton button = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Submission Operation Failed", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Submission Operation", "Are you sure you want to return book ?");
    }

    @FXML
    void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to renew");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to renew book ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE BOOKID = '" + bookID.getText() + "'";
            System.out.println(ac);
            if (databaseHandler.execAction(ac)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Book Has Been Renewed");
                alert.showAndWait();
                loadBookInfoTwo(null);
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Renew Has Failed");
                alert.showAndWait();
            }
        }else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancelled");
            alert.setHeaderText(null);
            alert.setContentText("Renew Operation Cancelled");
            alert.showAndWait();
        }
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
    void handleMenuFullScreen(ActionEvent event) {
        Stage stage = getStage();
        stage.setFullScreen(stage.isFullScreen());
    }

    private void initDrawer(){
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
    }

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
    }

    private void initGraphs() {
        bookChart = new PieChart(databaseHandler.getBookGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);


    }
}

