package ipcconnect4.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class IconButton extends ImageView {

    private final BooleanProperty active;
    private boolean shadow = true;
    
    private boolean lstatus;
    private ColorAdjust lca;

    public IconButton() {
        super();
        setAccessibleRole(AccessibleRole.BUTTON);
        active = new SimpleBooleanProperty();

        disableProperty().addListener((observable) -> updateDisabled());
        active.addListener((observable) -> updateActive());

        setActive(true);
        setDisabled(false);
    }

    public boolean getShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        redoUpdate();
    }
    
    public final BooleanProperty activeProperty() {
        return active;
    }

    public final boolean isActive() {
        return active.get();
    }

    public final void setActive(boolean value) {
        active.set(value);
    }

    private void updateActive() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-0.85);
        updateColor(!isActive(), colorAdjust);
    }

    private void updateDisabled() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.65);
        updateColor(isDisable(), colorAdjust);
    }

    private void updateColor(boolean status, ColorAdjust ca) {
        lstatus = status;
        lca = ca;
        if (shadow) {
            if (status) {
                ca.setInput(getDropShadow());
                setEffect(ca);
            } else {
                setEffect(getDropShadow());
            }
        } else {
            if (status) {
                setEffect(ca);
            } else {
                setEffect(null);
            }
        }
    }
    
    private void redoUpdate() {
        updateColor(lstatus, lca);
    }

    private static DropShadow getDropShadow() {
        return new DropShadow(5, 2.5, 2.5, Color.GRAY);
    }

}