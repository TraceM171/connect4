package ipcconnect4.ui.rank;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import ipcconnect4.view.AutoCompleteTextField;
import ipcconnect4.view.AutoResizeTableView;
import ipcconnect4.view.CircleImage;
import java.net.URL;
import java.util.List;
import java.util.OptionalInt;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Connect4;
import model.Player;

public class RanksController implements Initializable {

    @FXML
    private CircleImage avatarFirst;
    @FXML
    private Label nicknameFirst;
    @FXML
    private Label pointsFirst;
    @FXML
    private AutoResizeTableView<Player> ranksTable;
    @FXML
    private TableColumn<Player, Integer> tcPos;
    @FXML
    private TableColumn<Player, Image> tcAvatar;
    @FXML
    private TableColumn<Player, String> tcNickName;
    @FXML
    private TableColumn<Player, Integer> tcPoints;
    @FXML
    private AutoCompleteTextField searchUserAC;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Init TableView
        tcPos.setCellValueFactory(cell -> new SimpleObjectProperty<>(getRanksData().indexOf(cell.getValue()) + 1));
        tcPos.setCellFactory(cell -> {
            return new TableCell<Player, Integer>() {
                private final ImageView posIcon = new ImageView();

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    posIcon.setFitHeight(30);
                    posIcon.setFitWidth(30);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Image icon = null;
                        String pos = null;
                        switch (item) {
                            case 1:
                                icon = new Image("/resources/img/first.png");
                                break;
                            case 2:
                                icon = new Image("/resources/img/seccond.png");
                                break;
                            case 3:
                                icon = new Image("/resources/img/third.png");
                                break;
                            default:
                                pos = item.toString();
                                break;
                        }
                        setText(pos);
                        posIcon.setImage(icon);
                        setGraphic(icon == null ? null : posIcon);
                    }
                }
            };
        });
        ranksTable.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> e.consume());
            return row;
        });
        tcAvatar.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getAvatar()));
        tcAvatar.setCellFactory(cell -> {
            return new TableCell<Player, Image>() {
                private final CircleImage avatar = new CircleImage();

                @Override
                protected void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);
                    avatar.setRadius(20);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        avatar.setImage(item);
                        setGraphic(avatar);
                    }
                }
            };
        });
        tcNickName.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getNickName()));
        tcPoints.setCellValueFactory(cell -> new SimpleObjectProperty(cell.getValue().getPoints()));
        ranksTable.setItems(getRanksData());

        // Init seacrch funtion
        searchUserAC.getEntries().addAll(
                getRanksData().<Player>stream()
                        .map(player -> player.getNickName())
                        .collect(Collectors.toList())
        );
        searchUserAC.setValidator(t -> {
            OptionalInt indexOpt = IntStream.range(0, getRanksData().size())
                    .filter(i -> searchUserAC.tf().getText().equals(ranksTable.getItems().get(i).getNickName()))
                    .findFirst();
            if (indexOpt.isPresent()) {
                ranksTable.getSelectionModel().select(indexOpt.getAsInt());
                ranksTable.animateScrollTo(indexOpt.getAsInt());
            } else {
                ranksTable.getSelectionModel().select(null);
            }
            return indexOpt.isPresent();
        });

        // Init winner section
        if (getRanksData().size() > 0) {
            avatarFirst.setImage(getRanksData().get(0).getAvatar());
            nicknameFirst.setText(getRanksData().get(0).getNickName());
            pointsFirst.setText(getRanksData().get(0).getPoints() + "");
        }
        
        Platform.runLater(() -> avatarFirst.requestFocus());
    }

    @FXML
    private void statsAction(MouseEvent event) {
        Main.goToStats(false);
    }

    @FXML
    private void backAction(MouseEvent event) {
        Main.returnFromRanks();
    }

    private ObservableList<Player> getRanksData() {
        try {
            List ranking = Connect4.getSingletonConnect4().getConnect4Ranking();
            return FXCollections.observableList(ranking);
        } catch (Connect4DAOException ex) {
            Logger.getLogger(RanksController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
