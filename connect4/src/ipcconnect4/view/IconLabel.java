package ipcconnect4.view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class IconLabel extends Label {

    @SuppressWarnings("LeakingThisInConstructor")
    public IconLabel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/resources/fxml/icon_label.fxml"));
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
        setBigShadow(false);
        bindMouseEnter();
    }

    public void setIcon(String icon) {
        ((IconButton) getGraphic()).setImage(new Image(icon));
    }

    public String getIcon() {
        return ((IconButton) getGraphic()).getImage().toString();
    }

    private void bindMouseEnter() {
        setOnMouseEntered((event) -> {
            setBigShadow(true);
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
}
