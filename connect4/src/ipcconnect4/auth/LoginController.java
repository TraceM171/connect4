package ipcconnect4.auth;

import ipcconnect4.Connect4;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML
    private TextField userText;
    @FXML
    private TextField passText;
    @FXML
    private CheckBox rememberCheckB;
    @FXML
    private Button okButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void okAction(ActionEvent event) {
    }

    @FXML
    private void forgotAction(ActionEvent event) {
    }

    @FXML
    private void registerAction(ActionEvent event) {
        Connect4.showNYI();
    }
    
}
