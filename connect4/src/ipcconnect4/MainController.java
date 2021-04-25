package ipcconnect4;

import ipcconnect4.util.LocalPreferences;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {

    @FXML
    public Pane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalPreferences prefs = LocalPreferences.getInstance();
        Main.root = root;
        Locale.setDefault(prefs.getLang());
        Main.changeIsDarkMode(prefs.getIsDarkMode());
        Main.reset();
    }

}
