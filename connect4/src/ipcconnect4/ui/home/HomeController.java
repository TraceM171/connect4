package ipcconnect4.ui.home;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import static ipcconnect4.Main.rb;
import static ipcconnect4.Main.stage;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.ui.auth.AuthenticateController;
import ipcconnect4.ui.auth.RegisterController;
import ipcconnect4.util.LocalPreferences;
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.SelectorIcon;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class HomeController {

    @FXML
    private Label nickNameT1;
    @FXML
    private Label pointsT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private Label pointsT2;
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
                showProfileEditor(Main.player1);
            });
            menuItem2.setOnAction((event) -> {
                LocalPreferences.getInstance().setPlayer1(Main.player2);
                LocalPreferences.getInstance().setPlayer2(null);
                Main.player1 = Main.player2;
                Main.player2 = null;
                if (Main.player1 == null) {
                    Main.goToAuthenticate(1);
                } else {
                    Main.goToHome();
                }
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
            pointsT1.setText(Main.formatWLang("points", Main.player1.getPoints()));
        } else {
            Platform.runLater(() -> Main.goToAuthenticate(1));
        }

        if (Main.player2 != null) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Editar perfil");
            MenuItem menuItem2 = new MenuItem("Cerrar sesión");

            menuItem1.setOnAction((event) -> {
                showProfileEditor(Main.player2);
            });
            menuItem2.setOnAction((event) -> {
                Main.player2 = null;
                LocalPreferences.getInstance().setPlayer2(null);
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
            pointsT2.setText(Main.formatWLang("points", Main.player2.getPoints()));
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Iniciar sesión");

            menuItem1.setOnAction((event) -> {
                Stage auth2 = new Stage();
                auth2.setScene(new Scene(Main.getAuthenticateScene(2)));
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
            pointsT2.setText(Main.formatWLang("points", -1));
        }
    }

    private void bindDifficulties() {
        diff1SI.selectedProperty().bind(Bindings.equal(difficulty, Difficulty.EASY));
        diff2SI.selectedProperty().bind(Bindings.equal(difficulty, Difficulty.NORMAL));
        diff3SI.selectedProperty().bind(Bindings.equal(difficulty, Difficulty.HARD));

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

    private void showProfileEditor(Player player) {
        Stage auth2 = new Stage();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/resources/fxml/register.fxml"),
                Main.rb
        );
        RegisterController controller = new RegisterController(new RegisterController.RegisterListener() {
            @Override
            public void onCancel() {
                auth2.close();
            }

            @Override
            public void onRegister(Player newPlayer) {
                try {
                    player.setEmail(newPlayer.getEmail());
                    player.setPassword(newPlayer.getPassword());
                    player.setAvatar(newPlayer.getAvatar());
                    player.setBirthdate(newPlayer.getBirthdate());
                    player.setPoints(newPlayer.getPoints());

                    auth2.close();
                    Main.goToHome();
                } catch (Connect4DAOException ex) {
                    Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, player);
        loader.setController(controller);
        Parent root;
        try {
            root = loader.load();
            auth2.setScene(new Scene(root));
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }

        auth2.initModality(Modality.WINDOW_MODAL);
        auth2.initOwner(stage);
        auth2.setTitle(rb.getString("app_name"));
        auth2.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/img/icon.png")));
        auth2.setHeight(500);
        auth2.setWidth(450);
        auth2.setResizable(false);
        auth2.showAndWait();
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

}
