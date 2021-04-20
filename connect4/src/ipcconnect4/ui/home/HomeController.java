package ipcconnect4.ui.home;

import ipcconnect4.Main;
import static ipcconnect4.Main.rb;
import static ipcconnect4.Main.stage;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.SelectorIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Label nickNameT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private VBox pvpVB;
    @FXML
    private VBox blockpvpVB;
    @FXML
    private CircleImage avatarI1;
    @FXML
    private CircleImage avatarI2;
    @FXML
    private SelectorIcon diff1SI;
    @FXML
    private SelectorIcon diff2SI;
    @FXML
    private SelectorIcon diff3SI;

    private final ObjectProperty<Difficulty> difficulty
            = new SimpleObjectProperty<>(Main.lastAIdifficulty);

    @FXML
    public void initialize() {
        initTopBar();
        bindDifficulties();
        bindPvP();
    }

    private void initTopBar() {
        if (Main.player1 != null) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Editar perfil");
            MenuItem menuItem2 = new MenuItem("Cerrar sesión");

            menuItem1.setOnAction((event) -> {
                Main.showNYI();
            });
            menuItem2.setOnAction((event) -> {
                Main.player1 = null;
                Main.player2 = null;
                Main.goToAuthenticate(1);
            });

            contextMenu.getItems().addAll(menuItem1, menuItem2);

            EventHandler handler = (EventHandler<ContextMenuEvent>) (event) -> {
                contextMenu.show(avatarI1, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarI1, event.getScreenX(), event.getScreenY());
            };

            avatarI1.setOnContextMenuRequested(handler);
            avatarI1.setOnMouseClicked(handler1);

            avatarI1.setImage(Main.player1.getAvatar());
            nickNameT1.setText(Main.player1.getNickName());

        } else {
            Platform.runLater(() -> Main.goToAuthenticate(1));
        }

        if (Main.player2 != null) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Editar perfil");
            MenuItem menuItem2 = new MenuItem("Cerrar sesión");

            menuItem1.setOnAction((event) -> {
                Main.showNYI();
            });
            menuItem2.setOnAction((event) -> {
                Main.player2 = null;
                Main.goToHome();
            });

            contextMenu.getItems().addAll(menuItem1, menuItem2);

            EventHandler handler = (EventHandler<ContextMenuEvent>) (event) -> {
                contextMenu.show(avatarI2, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarI2, event.getScreenX(), event.getScreenY());
            };

            avatarI2.setOnContextMenuRequested(handler);
            avatarI2.setOnMouseClicked(handler1);

            avatarI2.setImage(Main.player2.getAvatar());
            nickNameT2.setText(Main.player2.getNickName());
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Iniciar sesión");

            menuItem1.setOnAction((event) -> {
                Stage auth2 = new Stage();
                auth2.setScene(Main.getAuthenticateScene(2));
                auth2.initModality(Modality.WINDOW_MODAL);
                auth2.initOwner(stage);
                auth2.setTitle(rb.getString("app_name"));
                auth2.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/img/icon.png")));
                auth2.setHeight(500);
                auth2.setWidth(450);
                auth2.setResizable(false);
                auth2.showAndWait();
                Main.goToHome();
            });

            contextMenu.getItems().addAll(menuItem1);

            EventHandler handler = (EventHandler<ContextMenuEvent>) (event) -> {
                contextMenu.show(avatarI2, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarI2, event.getScreenX(), event.getScreenY());
            };

            avatarI2.setOnContextMenuRequested(handler);
            avatarI2.setOnMouseClicked(handler1);

            avatarI2.setImage(new Image("/avatars/default.png"));
            nickNameT2.setText("?");
        }
    }

    private void bindDifficulties() {
        diff1SI.activeProperty().bind(Bindings.equal(difficulty, Difficulty.EASY));
        diff2SI.activeProperty().bind(Bindings.equal(difficulty, Difficulty.NORMAL));
        diff3SI.activeProperty().bind(Bindings.equal(difficulty, Difficulty.HARD));

        difficulty.addListener((observable, oldValue, newValue)
                -> Main.lastAIdifficulty = newValue
        );
    }

    private void bindPvP() {
        if (Main.player2 == null) {
            pvpVB.setDisable(true);
            blockpvpVB.setVisible(true);
        } else {
            pvpVB.setDisable(false);
            blockpvpVB.setVisible(false);
        }
    }

    @FXML
    private void difficultyAction(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        switch (source.getId()) {
            case "diff1SI":
                difficulty.set(Difficulty.EASY);
                break;
            case "diff2SI":
                difficulty.set(Difficulty.NORMAL);
                break;
            case "diff3SI":
                difficulty.set(Difficulty.HARD);
                break;
        }
    }

    @FXML
    private void settingsAction(MouseEvent event) {
        Main.showSettings((Stage) nickNameT1.getScene().getWindow());
    }

    @FXML
    private void startVSAIAction(MouseEvent event) {
        Main.startGameAI(Main.player1, difficulty.get());
    }

    @FXML
    private void startVSPlayerAction(MouseEvent event) {
        Main.startGame(Main.player1, Main.player2);
    }

    @FXML
    private void ranksAction(MouseEvent event) {
        Main.showNYI();
    }

    @FXML
    private void mouseEnteredAction(MouseEvent event) {
        Node source = ((Node) event.getSource());
        source.setEffect(new DropShadow(13, 3, 3, Color.BLACK));
    }

    @FXML
    private void mouseExitedAction(MouseEvent event) {
        Node source = ((Node) event.getSource());
        source.setEffect(new DropShadow(5, 2.5, 2.5, Color.GRAY));
    }

}
