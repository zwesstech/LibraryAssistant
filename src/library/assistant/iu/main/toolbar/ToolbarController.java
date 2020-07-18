package library.assistant.iu.main.toolbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import library.assistant.util.LibraryAssistantUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void loadAddMember(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addmember/member_add.fxml"), "Add New Member", null);
    }
    @FXML
    public void loadAddBook(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addbook/add_book.fxml"), "Add New Book", null);
    }
    @FXML
    public void loadMemberTable(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listmember/member_list.fxml"), "Member List", null);
    }
    @FXML
    public void loadBookTable(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listbook/book_list.fxml"), "Book List", null);

    }
    @FXML
    public void loadSettings(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/settings/settings.fxml"), "Settings", null);
    }

    @FXML
    void loadIssuedBookList(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/issuedlist/issued_list.fxml"), "Issued List", null);
    }
}
