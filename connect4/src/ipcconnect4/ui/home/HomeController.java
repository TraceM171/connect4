package ipcconnect4.ui.home;

import ipcconnect4.Main;
import static ipcconnect4.Main.rb;
import static ipcconnect4.Main.stage;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.IconButton;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
    private CircleImage avatarI1;
    @FXML
    private CircleImage avatarI2;
    @FXML
    private VBox diff1VB;
    @FXML
    private IconButton diff1IB;
    @FXML
    private VBox diff2VB;
    @FXML
    private IconButton diff2IB;
    @FXML
    private VBox diff3VB;
    @FXML
    private IconButton diff3IB;

    private final ObjectProperty<Difficulty> difficulty
            = new SimpleObjectProperty<>(Difficulty.EASY);

    @FXML
    public void initialize() {
        initTopBar();
        bindDifficulties();
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
            nickNameT1.setOnContextMenuRequested(handler);
            nickNameT1.setOnMouseClicked(handler1);

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
            nickNameT2.setOnContextMenuRequested(handler);
            nickNameT2.setOnMouseClicked(handler1);

            avatarI2.setImage(Main.player2.getAvatar());
            nickNameT2.setText(Main.player2.getNickName());
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Iniciar sesión");

            menuItem1.setOnAction((event) -> {
                Stage settingsStage = new Stage();
                settingsStage.setScene(Main.getAuthenticateScene(2));
                settingsStage.initModality(Modality.WINDOW_MODAL);
                settingsStage.initOwner(stage);
                settingsStage.setTitle(rb.getString("app_name"));
                settingsStage.showAndWait();
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
            nickNameT2.setOnContextMenuRequested(handler);
            nickNameT2.setOnMouseClicked(handler1);

            avatarI2.setImage(new Image("/avatars/default.png"));
            nickNameT2.setText("?");
        }
    }

    private void bindDifficulties() {
        String activeStyle = "-fx-background-radius: 25;"
                + "-fx-background-color: #c8bfe7;"
                + "-fx-border-color: #635e73;"
                + "-fx-border-radius: 25;"
                + "-fx-border-width:2;";
        String inactiveStyle = "-fx-background-radius: 25;"
                + "-fx-background-color: #c8bfe7;"
                + "-fx-border-color: #c8bfe7;"
                + "-fx-border-radius: 25;"
                + "-fx-border-width:2;";
        diff1VB.styleProperty().bind(Bindings.when(diff1IB.activeProperty())
                .then(activeStyle)
                .otherwise(inactiveStyle));
        diff2VB.styleProperty().bind(Bindings.when(diff2IB.activeProperty())
                .then(activeStyle)
                .otherwise(inactiveStyle));
        diff3VB.styleProperty().bind(Bindings.when(diff3IB.activeProperty())
                .then(activeStyle)
                .otherwise(inactiveStyle));
        
        diff1IB.activeProperty().bind(Bindings.equal(difficulty, Difficulty.EASY));
        diff2IB.activeProperty().bind(Bindings.equal(difficulty, Difficulty.NORMAL));
        diff3IB.activeProperty().bind(Bindings.equal(difficulty, Difficulty.HARD));
    }

    @FXML
    private void difficultyAction(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        switch (source.getId()) {
            case "diff1VB":
                difficulty.set(Difficulty.EASY);
                break;
            case "diff2VB":
                difficulty.set(Difficulty.NORMAL);
                break;
            case "diff3VB":
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
        Main.startGameAI(Main.player1, Difficulty.HARD);
    }

    @FXML
    private void startVSPlayerAction(MouseEvent event) {
        Main.startGame(Main.player1, Main.player2);
    }

    @FXML
    private void ranksAction(MouseEvent event) {
        Main.showNYI();
    }

}
