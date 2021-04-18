package ipcconnect4;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {

    @FXML
    public Pane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.root = root;
        Main.startWithLanguage(Main.DEF_LANG);
    }

}
