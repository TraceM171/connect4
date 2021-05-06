package ipcconnect4.ui.auth;

import DBAccess.Connect4DAOException;
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.PassFieldValid;
import ipcconnect4.view.TextFieldValid;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class RegisterController {

    private final RegisterListener listener;

    private String avatarPath = "";

    @FXML
    private TextFieldValid userText;
    @FXML
    private TextFieldValid emailText;
    @FXML
    private PassFieldValid passTextMask;
    @FXML
    private PassFieldValid passTextMask1;
    @FXML
    private TextFieldValid passTextUnmask;
    @FXML
    private TextFieldValid passTextUnmask1;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView okButton;
    @FXML
    private ImageView passMaskIV;
    @FXML
    private CircleImage avatar;
    @FXML
    private Text errorDate;

    public RegisterController(RegisterListener listener) {
        this.listener = listener;
    }

    @FXML
    private void initialize() {
        okButton.disableProperty().bind(
                userText.valid.not()
                        .or(emailText.valid.not())
                        .or(passTextMask.valid.not())
                        .or(passTextUnmask.valid.not())
                        .or(errorDate.visibleProperty())
                        .or(userText.tf().textProperty().isEmpty())
                        .or(emailText.tf().textProperty().isEmpty())
                        .or(passTextMask.tf().textProperty().isEmpty())
                        .or(passTextUnmask.tf().textProperty().isEmpty())
                        .or(datePicker.valueProperty().isNull())
        );

        passTextMask.tf().textProperty().bindBidirectional(passTextUnmask.tf().textProperty());
        passTextUnmask.visibleProperty().bind(passTextMask.visibleProperty().not());
        passTextMask.managedProperty().bind(passTextMask.visibleProperty());
        passTextUnmask.managedProperty().bind(passTextUnmask.visibleProperty());

        passTextMask1.tf().textProperty().bindBidirectional(passTextUnmask1.tf().textProperty());
        passTextMask1.visibleProperty().bind(passTextMask.visibleProperty());
        passTextMask1.managedProperty().bind(passTextMask1.visibleProperty());
        passTextUnmask1.visibleProperty().bind(passTextUnmask.visibleProperty());
        passTextUnmask1.managedProperty().bind(passTextUnmask1.visibleProperty());

        userText.setValidator(Player::checkNickName);
        emailText.setValidator(Player::checkEmail);
        passTextMask.setValidator(Player::checkPassword);
        passTextUnmask.setValidator(Player::checkPassword);
        passTextMask.tf().textProperty().addListener((observable) -> {
            passTextMask1.validate();
            passTextUnmask1.validate();
        });
        passTextMask1.setValidator((t) -> {
            String well = passTextMask.tf().getText();
            return well.equals(t);
        });
        passTextUnmask1.setValidator((t) -> {
            String well = passTextMask.tf().getText();
            return well.equals(t);
        });

        errorDate.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    LocalDate localDate = datePicker.getValue();
                    if (localDate == null) {
                        return false;
                    }
                    Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                    Calendar picked = Calendar.getInstance();
                    picked.setTimeInMillis(instant.toEpochMilli());
                    picked.add(Calendar.YEAR, 12);
                    Calendar now = Calendar.getInstance();
                    return !picked.before(now);
                }, datePicker.valueProperty()
        ));
    }

    @FXML
    private void okAction(InputEvent event) {
        try {
            Connect4 db = Connect4.getSingletonConnect4();
            if (db.exitsNickName(userText.tf().getText())) {
                userText.valid.set(false);
                userText.setErrorMsg("Usuario ja existente");
                return;
            }
            if (avatarPath.equals(CircleImage.DEF_IMG_PATH)) {
                db.registerPlayer(
                        userText.tf().getText(),
                        emailText.tf().getText(),
                        passTextMask.tf().getText(),
                        datePicker.getValue(),
                        0
                );
            } else {
                db.registerPlayer(
                        userText.tf().getText(),
                        emailText.tf().getText(),
                        passTextMask.tf().getText(),
                        avatar.getImage(),
                        datePicker.getValue(),
                        0
                );
            }

            listener.onFinish();

        } catch (Connect4DAOException ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cancelAction(InputEvent event) {
        listener.onFinish();
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

    @FXML
    private void chooseImage(InputEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a new profile picture");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().addAll(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            avatar.setImage(new Image(file.toURI().toString()));
            avatarPath = file.toURI().toString();
        }
    }

    public interface RegisterListener {

        void onFinish();
    }

}
