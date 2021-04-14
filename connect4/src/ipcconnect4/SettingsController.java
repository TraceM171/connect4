package ipcconnect4;

import ipcconnect4.util.BiHashMap;
import ipcconnect4.view.IconButton;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private Spinner<String> langSpinner;
    @FXML
    private Label saveText;
    @FXML
    private IconButton saveIV;

    private BiHashMap<Locale, String> langs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLangs();
        initSpinner();

        saveIV.disableProperty().bind(Bindings.equal(
                Bindings.<Locale>createObjectBinding(() -> {
                    return langs.getFirstKey(langSpinner.getValue());
                }, langSpinner.valueProperty()
                ),
                Locale.getDefault()
        ));

        saveText.visibleProperty().bind(saveIV.disabledProperty().not());
    }

    @FXML
    private void saveAction(InputEvent event) {
        Main.changeLanguage(
                langs.getFirstKey(langSpinner.getValue())
        );
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
        langs.put(new Locale("en", "GB"), "English");
    }

    private void initSpinner() {
        SpinnerValueFactory<String> valueFactory
                = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(langs.values())
                );
        valueFactory.setValue(langs.get(Locale.getDefault()));
        langSpinner.setValueFactory(valueFactory);
    }

}
