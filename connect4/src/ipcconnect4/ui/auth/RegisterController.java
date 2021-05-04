package ipcconnect4.ui.auth;

import ipcconnect4.view.CircleImage;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Player;
import sun.font.TextLabel;

public class RegisterController {

    private final RegisterListener listener;
    private boolean firstfocus = true;

    @FXML
    private Node root;
    @FXML
    private TextField userText;
    @FXML
    private TextField emailText;
    @FXML
    private TextField passTextMask;
    @FXML
    private TextField passTextMask1;
    @FXML
    private TextField passTextUnmask;
    @FXML
    private TextField passTextUnmask1;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView okButton;
    @FXML
    private ImageView passMaskIV;
    @FXML
    private CircleImage avatar;
    @FXML
    private Text errorUser;
    @FXML
    private Text errorEmail;
    @FXML
    private Text errorPass;
    @FXML
    private Text errorRepe;
    @FXML
    private Text errorDate;

    public RegisterController(RegisterListener listener) {
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
        passTextMask1.textProperty().bindBidirectional(passTextUnmask1.textProperty());
        passTextMask1.visibleProperty().bind(passTextMask.visibleProperty());
        passTextMask1.managedProperty().bind(passTextMask1.visibleProperty());
        passTextUnmask1.visibleProperty().bind(passTextUnmask.visibleProperty());
        passTextUnmask1.managedProperty().bind(passTextUnmask1.visibleProperty());

        errorUser.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !Player.checkNickName(userText.getText()) && !userText.getText().isEmpty(),
                userText.textProperty()
        ));
        errorEmail.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !Player.checkEmail(emailText.getText()) && !emailText.getText().isEmpty(),
                emailText.textProperty()
        ));
        errorPass.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !Player.checkPassword(passTextMask.getText()) && !passTextMask.getText().isEmpty(),
                passTextMask.textProperty()
        ));
        errorRepe.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !passTextMask.getText().equals(passTextMask1.getText())
                && !passTextMask.getText().isEmpty()
                && !passTextMask1.getText().isEmpty(),
                passTextMask.textProperty(),
                passTextMask1.textProperty()
        ));
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
    private void okAction(ActionEvent event) {

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
        }
    }

    public interface RegisterListener {

        void onFinish();
    }

}
