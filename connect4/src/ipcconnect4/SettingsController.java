package ipcconnect4;

import ipcconnect4.util.BiHashMap;
import ipcconnect4.util.LocalPreferences;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private ChoiceBox<String> langSpinner;
    @FXML
    private Label saveText;
    @FXML
    private ImageView saveIV;
    @FXML
    private Slider darkSwitch;
    @FXML
    private ImageView lightIV;
    @FXML
    private ImageView darkIV;
    @FXML
    private Node root;

    private BiHashMap<Locale, String> langs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLangs();
        initLangsSpinner();
        initDarkMode();

        saveIV.disableProperty().bind(
                Bindings.equal(
                        Bindings.<Locale>createObjectBinding(()
                                -> langs.getFirstKey(langSpinner.getSelectionModel().getSelectedItem()),
                                langSpinner.valueProperty()
                        ), Locale.getDefault()
                ).and(
                        Bindings.createBooleanBinding(()
                                -> (darkSwitch.getValue() == 1) == (LocalPreferences.getInstance().getIsDarkMode()),
                                darkSwitch.valueProperty()
                        )
                )
        );

        saveText.visibleProperty().bind(saveIV.disabledProperty().not());
        Platform.runLater(() -> root.requestFocus());
    }

    @FXML
    private void saveAction(InputEvent event) {
        Main.changeLanguage(
                langs.getFirstKey(langSpinner.getValue())
        );
        Main.changeIsDarkMode(
                darkSwitch.getValue() == 1
        );
        Main.reset();
        close();
    }

    @FXML
    private void cancelAction(InputEvent event) {
        close();
    }

    private void close() {
        Stage stage = (Stage) saveIV.getScene().getWindow();
        stage.close();
    }

    private void initLangs() {
        langs = new BiHashMap<>();
        langs.put(new Locale("ca", "ES"), "Català");
        langs.put(new Locale("es", "ES"), "Español");
        langs.put(new Locale("en", "US"), "English");
    }

    private void initLangsSpinner() {
        langSpinner.getItems().setAll(langs.values());
        langSpinner.getSelectionModel().select(langs.get(Locale.getDefault()));
    }

    private void initDarkMode() {
        darkSwitch.setValue(LocalPreferences.getInstance().getIsDarkMode() ? 1 : 0);

        lightIV.imageProperty().bind(Bindings
                .when(Bindings.createBooleanBinding(
                        () -> darkSwitch.getValue() == 1,
                        darkSwitch.valueProperty()
                ))
                .then(new Image("/resources/img/light_mode_disabled.png"))
                .otherwise(new Image("/resources/img/light_mode_enabled.png"))
        );
        darkIV.imageProperty().bind(Bindings
                .when(Bindings.createBooleanBinding(
                        () -> darkSwitch.getValue() == 1,
                        darkSwitch.valueProperty()
                ))
                .then(new Image("/resources/img/dark_mode_enabled.png"))
                .otherwise(new Image("/resources/img/dark_mode_disabled.png"))
        );
    }
}
