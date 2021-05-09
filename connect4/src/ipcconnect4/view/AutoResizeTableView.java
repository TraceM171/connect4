package ipcconnect4.view;

import ipcconnect4.util.Animation;
import java.util.Arrays;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class AutoResizeTableView<S> extends TableView<S> {

    private final StringProperty sizes = new SimpleStringProperty("");

    public AutoResizeTableView() {
        super();
        sizes.addListener(obsetvable -> autoResize());
    }

    private void autoResize() {
        final double[] widths = parseSizes();// define the relational width of each column 

        // whether the first column should be fixed
        final boolean fixFirstColumm = true;

        // fix the first column width when table width is lower than:
        final float fixOnTableWidth = 360; //pixels 

        // calulates sum of widths
        float sum = 0;
        for (double i : widths) {
            sum += i;
        }

        // calculates the fraction of the first column proportion in relation to the sum of all column proportions
        double firstColumnProportion = widths[0] / sum;

        // calculate the fitting fix width for the first column, you can change it by your needs, but it jumps to this width
        final double firstColumnFixSize = fixOnTableWidth * firstColumnProportion;

        // set the width to the columns
        for (int i = 0; i < widths.length; i++) {
            getColumns().get(i).prefWidthProperty().bind(widthProperty().subtract(13).multiply((widths[i] / sum)));
            // ---------The exact width-------------^-------------^
            if (fixFirstColumm) {
                if (i == 0) {
                    widthProperty().addListener((observable, oldTableWidth, newTableWidth) -> {
                        if (newTableWidth.intValue() <= fixOnTableWidth) {
                            // before you can set new value to column width property, need to unbind the autoresize binding
                            getColumns().get(0).prefWidthProperty().unbind();
                            getColumns().get(0).prefWidthProperty().setValue(firstColumnFixSize);
                        } else if (!getColumns().get(0).prefWidthProperty().isBound()) {
                            // than readd the autoresize binding if condition table.width > x
                            getColumns().get(0).prefWidthProperty()
                                    .bind(widthProperty().multiply(firstColumnProportion));
                        }
                    });
                }
            }
        }
    }

    private double[] parseSizes() {
        return Arrays.stream(sizes.get().split(",\b?")).mapToDouble(Double::valueOf).toArray();
    }

    public String getSizes() {
        return sizes.get();
    }

    public void setSizes(String sizes) {
        this.sizes.set(sizes);
    }

    public void animateScrollTo(int index) {
        ScrollBar vBar = (ScrollBar) lookup(".scroll-bar:vertical");
        new Animation(Animation.NORMAL).listScrollTo(vBar, getItems().size(), index);
    }
}
