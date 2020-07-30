package library.assistant.iu.about;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.util.LibraryAssistantUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {

    private static final String LINKED_IN = "https://www.linkedin.com";
    private static final String FACEBOOK = "https://www.facebook.com";
    private static final String GITHUB =   "https://www.github.com";
    private static final String YOUTUBE =   "https://www.youtube.com";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AlertMaker.showTrayMessage(String.format("Hello %s!", System.getProperty("user.name")), "Thanks for trying out Library Assistant");
    }

    private void loadWebPage(String url){
        try {
            Desktop.getDesktop().browse(new URI(url));
        }catch (IOException | URISyntaxException el){
            el.printStackTrace();
            handleWebPageLoadException(url);
        }
    }

    private void handleWebPageLoadException(String url){
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(url);
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(browser));
        stage.setScene(scene);
        stage.setTitle("Sizwe Nkosi");
        stage.show();
        LibraryAssistantUtil.setStageIcon(stage);
    }

    @FXML
    void loadGithub(ActionEvent event) {
        loadWebPage(GITHUB);
    }

    @FXML
    void loadFacebook(ActionEvent event) {
        loadWebPage(FACEBOOK);
    }

    @FXML
    void loadLinkedIn(ActionEvent event) {
        loadWebPage(LINKED_IN);
    }

    @FXML
    void loadYoutubeChannel(ActionEvent event) {
        loadWebPage(YOUTUBE);
    }

}
