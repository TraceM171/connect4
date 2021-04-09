package ipcconnect4.ui.auth;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.text.Text;
import model.Connect4;
import model.Player;

public class LoginController {

    private final LoginListener listener;
    private boolean firstfocus = true;

    @FXML
    private Node root;
    @FXML
    private TextField userText;
    @FXML
    private TextField passTextMask;
    @FXML
    private TextField passTextUnmask;
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
                passTextMask.textProperty().isEmpty()
        ));

        passTextMask.textProperty().bindBidirectional(passTextUnmask.textProperty());
        passTextUnmask.visibleProperty().bind(passTextMask.visibleProperty().not());
        passTextMask.managedProperty().bind(passTextMask.visibleProperty());
        passTextUnmask.managedProperty().bind(passTextUnmask.visibleProperty());

        errorText.managedProperty().bind(errorText.visibleProperty());

        userText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstfocus) {
                root.requestFocus();
                firstfocus = false;
            }
        });
    }

    @FXML
    private void okAction(ActionEvent event) {
        try {
            Player res = Connect4.getSingletonConnect4().loginPlayer(userText.getText(), passTextMask.getText());
            if (res != null) {
                errorText.setVisible(false);
                listener.onLogin(res);
            } else {
                errorText.setVisible(true);
                passTextMask.setText("");
            }
        } catch (Connect4DAOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void forgotAction(ActionEvent event) {
        listener.onForgotAction();
    }

    @FXML
    private void registerAction(ActionEvent event) {
        Main.showNYI();
    }

    @FXML
    private void changePassMask(InputEvent event) {
        passTextMask.setVisible(!passTextMask.isVisible());
        if (passTextMask.isVisible()) {
            passTextMask.requestFocus();
        } else {
            passTextUnmask.requestFocus();
        }
    }

    public interface LoginListener {

        void onLogin(Player logedPlayer);

        void onForgotAction();
    }

}
