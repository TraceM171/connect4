package ipcconnect4.view;

import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SelectorIcon extends VBox {

    private static final String SELECTED_CLASS = "big-button-sel-selected";
    private static final String UNSELECTED_CLASS = "big-button-sel-unselected";

    @FXML
    private ImageView iconButton;
    @FXML
    private Label label;

    private final BooleanProperty selected;

    @SuppressWarnings("LeakingThisInConstructor")
    public SelectorIcon() {
        selected = new SimpleBooleanProperty(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/resources/fxml/selector_icon.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(getClass().getClassLoader());
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        selected.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getStyleClass().add(SELECTED_CLASS);
                getStyleClass().remove(UNSELECTED_CLASS);
                ColorAdjust ca = new ColorAdjust();
                ca.setSaturation(0);
                iconButton.setEffect(ca);
            } else {
                getStyleClass().add(UNSELECTED_CLASS);
                getStyleClass().remove(SELECTED_CLASS);
                ColorAdjust ca = new ColorAdjust();
                ca.setSaturation(-0.85);
                iconButton.setEffect(ca);
            }
        });
        selected.setValue(false);
    }

    public void setIcon(String icon) {
        try {
            iconButton.setImage(new Image(icon));
        } catch (Exception e) {

        }
    }

    public String getIcon() {
        return iconButton.getImage().toString();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public String getText() {
        return label.getText();
    }

    public final BooleanProperty selectedProperty() {
        return selected;
    }

    public final boolean isSelected() {
        return selected.get();
    }

    public final void setSelected(boolean value) {
        selected.set(value);
    }
}
