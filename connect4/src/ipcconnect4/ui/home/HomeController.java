package ipcconnect4.ui.home;

import ipcconnect4.Main;
import static ipcconnect4.Main.stage;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private ImageView avatarIV1;
    @FXML
    private Label nickNameT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private ImageView avatarIV2;

    @FXML
    public void initialize() {
        initTopBar();
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
                Main.goToAuthenticate(1);
            });

            contextMenu.getItems().addAll(menuItem1, menuItem2);

            EventHandler handler = (EventHandler<ContextMenuEvent>) (event) -> {
                contextMenu.show(avatarIV1, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarIV1, event.getScreenX(), event.getScreenY());
            };

            avatarIV1.setOnContextMenuRequested(handler);
            avatarIV1.setOnMouseClicked(handler1);
            nickNameT1.setOnContextMenuRequested(handler);
            nickNameT1.setOnMouseClicked(handler1);

            avatarIV1.setImage(Main.player1.getAvatar());
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
                contextMenu.show(avatarIV2, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarIV2, event.getScreenX(), event.getScreenY());
            };

            avatarIV2.setOnContextMenuRequested(handler);
            avatarIV2.setOnMouseClicked(handler1);
            nickNameT2.setOnContextMenuRequested(handler);
            nickNameT2.setOnMouseClicked(handler1);

            avatarIV2.setImage(Main.player2.getAvatar());
            nickNameT2.setText(Main.player2.getNickName());
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Iniciar sesión");

            menuItem1.setOnAction((event) -> {
                Stage settingsStage = new Stage();
                settingsStage.setScene(Main.getAuthenticateScene(2));
                settingsStage.initModality(Modality.WINDOW_MODAL);
                settingsStage.initOwner(stage);
                settingsStage.showAndWait();
                Main.goToHome();
            });

            contextMenu.getItems().addAll(menuItem1);

            EventHandler handler = (EventHandler<ContextMenuEvent>) (event) -> {
                contextMenu.show(avatarIV2, event.getScreenX(), event.getScreenY());
            };

            EventHandler handler1 = (EventHandler<MouseEvent>) (event) -> {
                contextMenu.show(avatarIV2, event.getScreenX(), event.getScreenY());
            };

            avatarIV2.setOnContextMenuRequested(handler);
            avatarIV2.setOnMouseClicked(handler1);
            nickNameT2.setOnContextMenuRequested(handler);
            nickNameT2.setOnMouseClicked(handler1);

            avatarIV2.setImage(new Image("/avatars/default.png"));
            nickNameT2.setText("?");
        }
    }

    @FXML
    private void settingsAction(MouseEvent event) {
        Main.showSettings( (Stage) nickNameT1.getScene().getWindow());
    }

    @FXML
    private void ranksAction(MouseEvent event) {
        Main.showNYI();
    }

}
