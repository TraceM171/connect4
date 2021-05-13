package ipcconnect4.ui.rank;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import ipcconnect4.util.Animation;
import ipcconnect4.view.AutoCompleteTextField;
import ipcconnect4.view.AutoResizeTableView;
import ipcconnect4.view.CircleImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.Connect4;
import model.DayRank;
import model.Player;
import model.Round;

public class StatsController implements Initializable {

    private double bigSize;
    private final BooleanProperty needRefresh = new SimpleBooleanProperty(false);
    private final BooleanProperty filterChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty playerValid = new SimpleBooleanProperty(true);
    private final BooleanProperty dateValid = new SimpleBooleanProperty(true);
    private final ObservableList<Round> filteredRounds = FXCollections.observableArrayList();

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
    private ImageView refreshIcon;
    @FXML
    private AutoResizeTableView<Round> statsTable;
    @FXML
    private TableColumn<Round, LocalDateTime> dateCol;
    @FXML
    private TableColumn<Round, Player> winnerCol;
    @FXML
    private TableColumn<Round, Player> loserCol;
    @FXML
    private VBox graphicsRoot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        needRefresh.bind(filterChanged.and(playerValid).and(dateValid));
        initDrawer();
        initStatsTable();
        refreshData();
        Platform.runLater(() -> statsTable.requestFocus());
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
                .filter(i -> t.equals(getDB().getConnect4Ranking().get(i).getNickName()))
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
                resultsWin.setSelected(true);
                resultsLose.setSelected(true);
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
        resultsWin.disableProperty().bind(resultsLose.selectedProperty().not().or(playersCB.selectedProperty()));
        resultsLose.disableProperty().bind(resultsWin.selectedProperty().not().or(playersCB.selectedProperty()));
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
        needRefresh.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                new Animation(Animation.SLOW).appearingSpin(refreshIcon);
                refreshData();
            }
        });
    }

    private void initStatsTable() {
        dateCol.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getTimeStamp()));
        dateCol.setCellFactory(cell -> new TableCell<Round, LocalDateTime>() {
            private final Label dateLabel = new Label();

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setContentDisplay(ContentDisplay.TOP);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("HH:mm")));
                    dateLabel.setText(item.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault())));
                    setGraphic(dateLabel);
                }
            }
        });
        Callback playerCellFactory = cell -> new TableCell<Round, Player>() {
            private final CircleImage avatar = new CircleImage();

            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                avatar.setRadius(20);
                setContentDisplay(ContentDisplay.TOP);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getNickName());
                    avatar.setImage(item.getAvatar());
                    setGraphic(avatar);
                }
            }
        };
        Comparator<Player> playerComparator = (p1, p2)
                -> p1.getNickName().toLowerCase().compareTo(p2.getNickName().toLowerCase());
        winnerCol.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getWinner()));
        winnerCol.setCellFactory(playerCellFactory);
        winnerCol.setComparator(playerComparator);
        loserCol.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getLoser()));
        loserCol.setCellFactory(playerCellFactory);
        loserCol.setComparator(playerComparator);
        statsTable.setItems(filteredRounds);
    }

    private void refreshData() {
        refreshTable();
        refreshGraphics();
        filterChanged.setValue(false);
    }

    private void refreshTable() {
        // No filters
        TreeMap<LocalDate, List<Round>> roundsMap = getDB().getRoundsPerDay();
        // DATE filter
        SortedMap<LocalDate, List<Round>> fRoundsMap = roundsMap.subMap(
                dateIni.getValue(),
                true,
                dateFin.getValue(),
                true
        );
        // Map to list of Rounds
        List<Round> fRounds = concatenate(fRoundsMap.values().toArray(new List[1]));
        // PLAYER && RESULT filter
        if (!playersCB.isSelected()) {
            fRounds = fRounds.stream().filter((Round round)
                    -> (round.getWinner().getNickName().equals(playersATF.tf().getText()) && resultsWin.isSelected())
                    || (round.getLoser().getNickName().equals(playersATF.tf().getText()) && resultsLose.isSelected())
            ).collect(Collectors.toList());
        }
        // Update table
        Collections.reverse(fRounds);
        filteredRounds.setAll(fRounds);
    }

    private void refreshGraphics() {
        clearGraphics();
        if (playersCB.isSelected()) {
            addGraphic(totalGames());
        } else if (resultsWin.isSelected() && resultsLose.isSelected()) {
            addGraphic(playerWinLose(), playerSocial());
        }
    }

    private LineChart<String, Number> totalGames() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Rounds");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        final int[] biggest = {0};
        ObservableList<XYChart.Data<String, Integer>> cVals = FXCollections.observableArrayList();
        getDB().getRoundCountsPerDay().subMap(
                dateIni.getValue(),
                true,
                dateFin.getValue(),
                true
        ).forEach((LocalDate day, Integer rounds) -> {
            cVals.add(new XYChart.Data(day.toString(), rounds));
            biggest[0] = Math.max(rounds, biggest[0]);
        });
        yAxis.setUpperBound(biggest[0] + 2);

        XYChart.Series serie1 = new XYChart.Series(cVals);
        LineChart<String, Number> chart = new LineChart(xAxis, yAxis);
        chart.setTitle("Rounds per day");
        chart.setLegendVisible(false);
        chart.getData().add(serie1);
        return chart;
    }

    private StackedBarChart<String, Number> playerWinLose() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Rounds");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        final int[] biggest = {0};
        ObservableList<XYChart.Data<String, Integer>> cValsWin = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<String, Integer>> cValsLose = FXCollections.observableArrayList();
        getDB().getDayRanksPlayer(getDB().getPlayer(playersATF.tf().getText())).subMap(
                dateIni.getValue(),
                true,
                dateFin.getValue(),
                true
        ).forEach((LocalDate day, DayRank dr) -> {
            cValsWin.add(new XYChart.Data(day.toString(), dr.getWinnedGames()));
            cValsLose.add(new XYChart.Data(day.toString(), dr.getLostGames()));
            int total = dr.getWinnedGames() + dr.getLostGames();
            biggest[0] = Math.max(total, biggest[0]);
        });
        yAxis.setUpperBound(biggest[0] + 2);

        XYChart.Series serieW = new XYChart.Series(cValsWin);
        serieW.setName("Wins");
        XYChart.Series serieL = new XYChart.Series(cValsLose);
        serieL.setName("Losses");
        StackedBarChart<String, Number> chart = new StackedBarChart(xAxis, yAxis);
        chart.setTitle("Player Wins/Losses per day");
        chart.getData().addAll(serieW, serieL);
        return chart;
    }

    private BarChart<String, Number> playerSocial() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Opponents");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);

        final int[] biggest = {0};
        ObservableList<XYChart.Data<String, Integer>> cVals = FXCollections.observableArrayList();
        getDB().getDayRanksPlayer(getDB().getPlayer(playersATF.tf().getText())).subMap(
                dateIni.getValue(),
                true,
                dateFin.getValue(),
                true
        ).forEach((LocalDate day, DayRank dr) -> {
            cVals.add(new XYChart.Data(day.toString(), dr.getOponents()));
            biggest[0] = Math.max(dr.getOponents(), biggest[0]);
        });
        yAxis.setUpperBound(biggest[0] + 2);

        XYChart.Series serie1 = new XYChart.Series(cVals);
        serie1.setName("Opponents");
        BarChart<String, Number> chart = new BarChart(xAxis, yAxis);
        chart.setTitle("Player Opponents per day");
        chart.setLegendVisible(false);
        chart.getData().add(serie1);
        return chart;
    }

    private void addGraphic(Chart... graphics) {
        graphicsRoot.getChildren().addAll(graphics);
        for (Chart g : graphics) {
            VBox.setVgrow(g, Priority.ALWAYS);
        }
    }

    private void clearGraphics() {
        graphicsRoot.getChildren().clear();
    }

    private Connect4 getDB() {
        try {
            return Connect4.getSingletonConnect4();
        } catch (Connect4DAOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> List<T> concatenate(List<T>... lists) {
        return Stream.of(lists)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
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
