package ipcconnect4.auth;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Connect4;
import model.Player;

public class LoginController {

    private LoginListener listener;

    @FXML
    private TextField userText;
    @FXML
    private TextField passText;
    @FXML
    private Button okButton;
    @FXML
    private Text errorText;

    public LoginController(LoginListener listener) {
        this.listener = listener;
    }

    @FXML
    public void initialize() {
        // okButton disabled if userText or passText are empty
        okButton.disableProperty().bind(Bindings.or(
                userText.textProperty().isEmpty(),
                passText.textProperty().isEmpty()
        ));

        errorText.managedProperty().bind(errorText.visibleProperty());
    }

    @FXML
    private void okAction(ActionEvent event) {
        try {
            Player res = Connect4.getSingletonConnect4().loginPlayer(userText.getText(), passText.getText());
            if (res != null) {
                errorText.setVisible(false);
                listener.onLogin(res);
            } else {
                errorText.setVisible(true);
                passText.setText("");
            }
        } catch (Connect4DAOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void forgotAction(ActionEvent event) {
    }

    @FXML
    private void registerAction(ActionEvent event) {
        Main.showNYI();
    }

    public interface LoginListener {

        void onLogin(Player logedPlayer);
    }

}
