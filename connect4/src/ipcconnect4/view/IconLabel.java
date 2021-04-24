package ipcconnect4.view;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    public void setIcon(String icon) {
        ((ImageView) getGraphic()).setImage(new Image(icon));
    }

    public String getIcon() {
        return ((ImageView) getGraphic()).getImage().toString();
    }
}
