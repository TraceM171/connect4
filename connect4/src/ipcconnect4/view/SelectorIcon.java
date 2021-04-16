package ipcconnect4.view;

import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SelectorIcon extends VBox {

    @FXML
    private IconButton iconButton;

    @SuppressWarnings("LeakingThisInConstructor")
    public SelectorIcon() {
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
        bindActive();
        bindMouseEnter();
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

    private void bindActive() {
        String activeStyle = "-fx-background-radius: 25;"
                + "-fx-background-color: #c8bfe7;"
                + "-fx-border-color: #635e73;"
                + "-fx-border-radius: 25;"
                + "-fx-border-width:2;";
        String inactiveStyle = "-fx-background-radius: 25;"
                + "-fx-background-color: #c8bfe7;"
                + "-fx-border-color: #c8bfe7;"
                + "-fx-border-radius: 25;"
                + "-fx-border-width:2;";
        iconButton.activeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle(activeStyle);
                setBigShadow(false);
            } else {
                setStyle(inactiveStyle);
            }
        });
        setStyle(activeStyle);
        setBigShadow(false);
    }

    private void bindMouseEnter() {
        setOnMouseEntered((event) -> {
            if (!iconButton.isActive()) {
                setBigShadow(true);
            }
        });
        setOnMouseExited((event) -> {
            setBigShadow(false);
        });
    }

    private void setBigShadow(boolean big) {
        if (big) {
            setEffect(new DropShadow(13, 3, 3, Color.BLACK));
        } else {
            setEffect(new DropShadow(5, 2.5, 2.5, Color.GRAY));
        }
    }

    public final BooleanProperty activeProperty() {
        return iconButton.activeProperty();
    }

    public final boolean isActive() {
        return iconButton.isActive();
    }

    public final void setActive(boolean value) {
        iconButton.setActive(value);
    }
}
