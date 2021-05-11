package ipcconnect4.ui.rank;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import ipcconnect4.util.Animation;
import ipcconnect4.view.AutoCompleteTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Connect4;
import model.Player;

public class StatsController implements Initializable {

    private double bigSize;
    private final BooleanProperty filterChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty playerValid = new SimpleBooleanProperty(true);
    private final BooleanProperty dateValid = new SimpleBooleanProperty(true);

    @FXML
    private Pane smallRoot;
    @FXML
    private Pane filterRoot;
    @FXML
    private VBox bigRoot;
    @FXML
    private ImageView smallDatesI;
    @FXML
    private ImageView smallResultsI;
    @FXML
    private ImageView smallPlayersI;
    @FXML
    private ImageView bigDatesI;
    @FXML
    private ImageView bigResultsI;
    @FXML
    private ImageView bigPlayersI;
    @FXML
    private CheckBox playersCB;
    @FXML
    private AutoCompleteTextField playersATF;
    @FXML
    private VBox playersBase;
    @FXML
    private CheckBox resultsWin;
    @FXML
    private CheckBox resultsLose;
    @FXML
    private HBox smallDateBase;
    @FXML
    private HBox smallPlayersBase;
    @FXML
    private VBox dateBase;
    @FXML
    private DatePicker dateIni;
    @FXML
    private DatePicker dateFin;
    @FXML
    private ImageView filterRefresh;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDrawer();
    }

    private void initDrawer() {
        // Common change notifier
        ChangeListener changed = (o, d, n) -> filterChanged.set(true);
        // Small transition
        bigSize = bigRoot.getPrefWidth();
        // Small init
        smallPlayersI.imageProperty().bind(bigPlayersI.imageProperty());
        smallResultsI.imageProperty().bind(bigResultsI.imageProperty());
        smallDatesI.imageProperty().bind(bigDatesI.imageProperty());
        // Players section
        playersATF.disableProperty().bind(playersCB.selectedProperty());
        playersATF.disableProperty().bind(playersCB.selectedProperty());
        playersATF.getEntries().addAll(
                getDB().getConnect4Ranking().<Player>stream()
                        .map(player -> player.getNickName())
                        .collect(Collectors.toList())
        );
        playersATF.setValidator(t -> IntStream.range(0, getDB().getConnect4Ranking().size())
                .filter(i -> playersATF.tf().getText().equals(getDB().getConnect4Ranking().get(i).getNickName()))
                .findFirst().isPresent());
        playersATF.valid.addListener((observable, oldValue, newValue) -> {
            playersBase.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !newValue);
            smallPlayersBase.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), !newValue);
            playerValid.set(newValue);
        });
        playersCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                playersBase.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), false);
                playerValid.set(true);
                playersATF.valid.set(true);
            } else {
                playersATF.validate();
            }
        });
        bigPlayersI.imageProperty().bind(Bindings
                .when(playersCB.selectedProperty())
                .then(new Image("/resources/img/group.png"))
                .otherwise(new Image("/resources/img/one.png"))
        );
        playersCB.selectedProperty().addListener(changed);
        playersATF.tf().textProperty().addListener(changed);
        // Result section
        resultsWin.disableProperty().bind(resultsLose.selectedProperty().not());
        resultsLose.disableProperty().bind(resultsWin.selectedProperty().not());
        bigResultsI.imageProperty().bind(Bindings
                .when(resultsWin.selectedProperty())
                .then(Bindings
                        .when(resultsLose.selectedProperty())
                        .then(new Image("/resources/img/res-both.png"))
                        .otherwise(new Image("/resources/img/res-win.png"))
                )
                .otherwise(new Image("/resources/img/res-lose.png"))
        );
        resultsWin.selectedProperty().addListener(changed);
        resultsLose.selectedProperty().addListener(changed);
        // Date section
        dateFin.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty
                        || date.compareTo(LocalDate.now()) > 0
                        || (dateIni.getValue() != null && dateIni.getValue().compareTo(date) > 0)
                );
            }
        });
        dateIni.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty
                        || date.compareTo(LocalDate.now()) > 0
                        || (dateFin.getValue() != null && dateFin.getValue().compareTo(date) < 0)
                );
            }
        });
        dateFin.setValue(LocalDate.now());
        dateIni.setValue(dateFin.getValue().minusDays(10));
        ChangeListener<LocalDate> dateList = (observable, oldValue, newValue) -> {
            boolean status = dateIni.getValue() == null
                    || dateFin.getValue() == null
                    || dateIni.getValue().isAfter(dateFin.getValue());
            dateBase.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), status);
            smallDateBase.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), status);
            dateValid.set(!status);
        };
        dateIni.valueProperty().addListener(dateList);
        dateFin.valueProperty().addListener(dateList);
        dateIni.valueProperty().addListener(changed);
        dateFin.valueProperty().addListener(changed);
        // Refresh button
        filterRefresh.visibleProperty().bind(
                filterChanged.and(playerValid).and(dateValid)
        );
    }

    private Connect4 getDB() {
        try {
            return Connect4.getSingletonConnect4();
        } catch (Connect4DAOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void refresh() {

    }

    @FXML
    private void backAction(MouseEvent event) {
        Main.returnFromRanks();
    }

    @FXML
    private void rankAction(MouseEvent event) {
        Main.goToRanks(false);
    }

    @FXML
    private void showFilterAction(MouseEvent event) {
        new Animation(Animation.NORMAL).drawerShow(filterRoot, bigRoot, bigSize);
    }

    @FXML
    private void hideFilterAction(MouseEvent event) {
        new Animation(Animation.NORMAL).drawerHide(filterRoot, bigRoot, smallRoot);
    }
}
