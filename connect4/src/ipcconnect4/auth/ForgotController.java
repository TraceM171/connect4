package ipcconnect4.auth;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Connect4;
import model.Player;

public class ForgotController {

    private static final int CODE_LENGTH = 6;

    private static int generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(new Random().nextInt(10));
        }
        return Integer.valueOf(sb.toString());
    }

    private final ForgotListener listener;
    private int code;

    @FXML
    private TextField userText;
    @FXML
    private TextField emailText;
    @FXML
    private Text errorText1;
    @FXML
    private Button okButton;
    @FXML
    private TextField codeText;
    @FXML
    private Text errorText2;
    @FXML
    private Button okButton1;

    public ForgotController(ForgotListener listener) {
        this.listener = listener;
    }

    @FXML
    public void initialize() {
        okButton.disableProperty().bind(Bindings.or(
                userText.textProperty().isEmpty(),
                emailText.textProperty().isEmpty()
        ));

        okButton1.disableProperty().bind(
                codeText.textProperty().isEmpty()
        );

        errorText1.managedProperty().bind(errorText1.visibleProperty());
        errorText2.managedProperty().bind(errorText2.visibleProperty());

        codeText.textProperty().addListener((observable,oldValue,newValue) -> {
            if (!newValue.matches("\\d*")) {
                codeText.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void okAction(ActionEvent event) {
        try {
            Player res = Connect4.getSingletonConnect4().getPlayer(userText.getText());
            if (res != null && res.getEmail().equals(emailText.getText())) {
                errorText1.setVisible(false);
                code = generateCode();
                showCode();
            } else {
                errorText1.setVisible(true);
            }
        } catch (Connect4DAOException ex) {
            Logger.getLogger(ForgotController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void verifyAction(ActionEvent event) {
        if (Integer.valueOf(codeText.getText()) == code) {
            try {
                errorText2.setVisible(false);
                Player res = Connect4.getSingletonConnect4().getPlayer(userText.getText());
                showPassword(res.getPassword());
            } catch (Connect4DAOException ex) {
                Logger.getLogger(ForgotController.class.getName()).log(Level.SEVERE, null, ex);
            }
            listener.onFinish();
        } else {
            errorText2.setVisible(true);
        }
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        listener.onFinish();
    }

    private void showCode() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Main.rb.getString("code"));
        alert.setHeaderText(Main.rb.getString("code_dialog_explanation"));
        alert.setContentText(String.valueOf(code));

        ButtonType buttonTypeOne = new ButtonType(Main.rb.getString("copy"));
        ButtonType buttonTypeOk = new ButtonType(Main.rb.getString("accept"), ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeOk);

        final Button copyButton = (Button) alert.getDialogPane().lookupButton(buttonTypeOne);
        copyButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    StringSelection stringSelection = new StringSelection(String.valueOf(code));
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    event.consume();
                }
        );

        alert.showAndWait();
    }

    private void showPassword(String password) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Main.rb.getString("your_pass"));
        alert.setHeaderText(Main.rb.getString("your_pass_explanation"));
        alert.setContentText(password);

        ButtonType buttonTypeOne = new ButtonType(Main.rb.getString("copy"));
        ButtonType buttonTypeOk = new ButtonType(Main.rb.getString("accept"), ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeOk);

        final Button copyButton = (Button) alert.getDialogPane().lookupButton(buttonTypeOne);
        copyButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    StringSelection stringSelection = new StringSelection(password);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    event.consume();
                }
        );

        alert.showAndWait();
    }

    public interface ForgotListener {

        void onFinish();
    }

}
