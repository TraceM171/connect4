package ipcconnect4.ui.auth;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import static ipcconnect4.Main.styleSheet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    private ImageView okButton;
    @FXML
    private Text errorText;
    @FXML
    private ImageView passMaskIV;

    public LoginController(LoginListener listener) {
        this.listener = listener;
    }

    @FXML
    private void initialize() {
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
    private void tfOkAction(ActionEvent event) {
        if (!okButton.isDisable()) {
            okAction(null);
        }
    }

    @FXML
    private void okAction(MouseEvent event) {
        try {
            Player res = Connect4.getSingletonConnect4().loginPlayer(userText.getText(), passTextMask.getText());
            if (res != null) {
                errorText.setVisible(false);
                if (res.equals(Main.player1) || res.equals(Main.player2)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(Main.rb.getString("login_title"));
                    alert.setHeaderText(Main.rb.getString("login_already_header"));
                    alert.setContentText(Main.rb.getString("login_already_content"));

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().clear();
                    dialogPane.getStylesheets().add(styleSheet);
                    dialogPane.getStyleClass().add("dialog");

                    Image iconImage = new Image(Main.class.getResourceAsStream("/resources/img/icon.png"));
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(iconImage);

                    alert.showAndWait();
                } else {
                    listener.onLogin(res);
                }
            } else {
                errorText.setVisible(true);
                passTextMask.setText("");
            }
        } catch (Connect4DAOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void forgotAction(MouseEvent event) {
        listener.onForgotAction();
    }

    @FXML
    private void registerAction(MouseEvent event) {
        Main.showNYI();
    }

    @FXML
    private void changePassMask(InputEvent event) {
        passTextMask.setVisible(!passTextMask.isVisible());
        if (passTextMask.isVisible()) {
            passTextUnmask.setFocusTraversable(false);
            passTextMask.requestFocus();
            passMaskIV.setImage(new Image("/resources/img/show_pass.png"));
        } else {
            passTextMask.setFocusTraversable(false);
            passTextUnmask.requestFocus();
            passMaskIV.setImage(new Image("/resources/img/hide_pass.png"));
        }
    }

    public interface LoginListener {

        void onLogin(Player logedPlayer);

        void onForgotAction();
    }

}
