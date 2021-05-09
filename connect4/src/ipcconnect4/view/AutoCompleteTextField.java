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

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.
 *
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextFieldValid {

    /**
     * The existing autocomplete entries.
     */
    private final SortedSet<String> entries;
    /**
     * The Pos used to select an entry.
     */
    private ContextMenu entriesPopup;

    /**
     * Construct a new AutoCompleteTextField.
     */
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

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() {
        return entries;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
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
