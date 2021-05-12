package ipcconnect4.view;

import ipcconnect4.util.Animation;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AutoResizeTableView<S> extends TableView<S> {

    private final StringProperty sizes = new SimpleStringProperty("");

    public AutoResizeTableView() {
        super();
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        sizes.addListener(observable -> {
            List<TableColumn<S, ?>> cols = getColumns();
            double[] perc = parseSizes();
            for (int i = 0; i < cols.size(); i++) {
                cols.get(i).setMaxWidth(1f * Integer.MAX_VALUE * perc[i]);
            }
        });
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
