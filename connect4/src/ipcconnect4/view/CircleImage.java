package ipcconnect4.view;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class CircleImage extends Circle {
    
    private Image image;
    
    @FXML
    private void initialize() {
        image = new Image("/avatars/default.png");
    }

    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
        updateImage();
    }
    
    private void updateImage() {
        double h = image.getHeight();
        double w = image.getWidth();
        double hP = h <= w ? 1 : h / w ;
        double wP = w <= h ? 1 : w / h ;
        double y = hP == 1 ? 0 : (1 - hP) / 2;
        double x = wP == 1 ? 0 : (1 - wP) / 2;
        setFill(new ImagePattern(image, x, y, wP, hP, true));
    }
}
