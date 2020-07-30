package library.assistant.iu.main.toolbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import library.assistant.iu.callback.BookReturnCallback;
import library.assistant.iu.issuedlist.IssuedListController;
import library.assistant.util.LibraryAssistantUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolbarController implements Initializable {

    private BookReturnCallback callback;

    public void setBookReturnCallback(BookReturnCallback callback){
        this.callback = callback;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    private void loadAddMember(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addmember/member_add.fxml"), "Add New Member", null);
    }
    @FXML
    private void loadAddBook(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/addbook/add_book.fxml"), "Add New Book", null);
    }
    @FXML
    private void loadMemberTable(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listmember/member_list.fxml"), "Member List", null);
    }
    @FXML
    private void loadBookTable(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/listbook/book_list.fxml"), "Book List", null);

    }
    @FXML
    private void loadSettings(ActionEvent actionEvent) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/settings/settings.fxml"), "Settings", null);
    }

    @FXML
    private void loadIssuedBookList(ActionEvent event) {
        Object controller = LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/iu/issuedlist/issued_list.fxml"), "Issued Book List", null);
        if (controller != null){
            IssuedListController cont = (IssuedListController) controller;
            cont.setBookReturnCallback(callback);
        }
    }
}
