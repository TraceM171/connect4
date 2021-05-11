package ipcconnect4.view;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Function;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class TextFieldValid extends StackPane {

    public final BooleanProperty valid = new SimpleBooleanProperty(false);
    private Function<String, Boolean> validator;
    private boolean autoValidate = false;
    private Tooltip tt = new Tooltip();

    @FXML
    private TextField textField;
    @FXML
    private ImageView errorIV;

    @SuppressWarnings("LeakingThisInConstructor")
    public TextFieldValid() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/resources/fxml/text_field_valid.fxml"));
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
        tf().textProperty().addListener((observable, oldValue, newValue) -> {
            if (autoValidate) {
                validate();
            }
        });

        getStyleClass().add("text-field-valid");

        Tooltip.install(errorIV, tt);
        hackTooltipStartTiming(tt);
        tt.setFont(Font.font("14px"));
        tt.setWrapText(true);
        tt.setMaxWidth(300);

        valid.addListener((observable, oldValue, newValue) -> {
            errorIV.setVisible(!newValue);
            if (newValue) {
                tf().setPadding(new Insets(5, 10, 5, 10));
            } else {
                tf().setPadding(new Insets(5, 26, 5, 10));
            }
            pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !newValue);
        });

        validate();
    }

    public void setValidator(Function<String, Boolean> validator) {
        this.validator = validator;
    }

    public boolean getAutoValidate() {
        return autoValidate;
    }

    public void setAutoValidate(boolean autoValidate) {
        this.autoValidate = autoValidate;
    }

    public String getErrorMsg() {
        return tt.getText();
    }

    public void setErrorMsg(String errorMsg) {
        tt.setText(errorMsg);
    }

    public String getPromptText() {
        return tf().getPromptText();
    }

    public void setPromptText(String promptText) {
        tf().setPromptText(promptText);
    }

    public void validate() {
        valid.setValue((validator != null  && validator.apply(tf().getText()))
                || tf().getText().isEmpty());
    }
    
    public double getTextSize() {
        return tf().getFont().getSize();
    }
    
    public void setTextSize(double size) {
        tf().setFont(new Font(size));
    }

    public final  TextField tf() {
        return textField;
    }

    private static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(0)));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
        }
    }
}
