package ipcconnect4.view;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AutoCompleteTextField extends TextFieldValid {

    private final SortedSet<String> entries;
    private ContextMenu entriesPopup;

    public AutoCompleteTextField() {
        super();
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
        tf().textProperty().addListener((observableValue, s, s2) -> {
            if (tf().getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>();
                searchResult.addAll(entries.stream()
                        .filter(e -> e.toLowerCase().contains(tf().getText().toLowerCase()))
                        .collect(Collectors.toList()));
                if (entries.size() > 0) {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> {
            entriesPopup.hide();
        });

    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                tf().setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }
}
