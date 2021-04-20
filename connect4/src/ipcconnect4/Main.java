package ipcconnect4;

import DBAccess.Connect4DAOException;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.ui.auth.AuthenticateController;
import ipcconnect4.ui.game.GameController;
import ipcconnect4.util.Animations;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class Main extends Application {

    public static final Locale DEF_LANG = new Locale("ca", "ES");

    public static Player player1, player2;
    public static Difficulty lastAIdifficulty = Difficulty.EASY;
    public static ResourceBundle rb;
    public static Stage stage;
    public static Pane root;

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeLanguage(Locale locale) {
        startWithLanguage(locale);
    }

    public static void startWithLanguage(Locale locale) {
        try {
            Locale.setDefault(locale);
            rb = ResourceBundle.getBundle("resources.bundles.Strings", Locale.getDefault());
//          goToAuthenticate(1);
            // TEST CODE BEGINS
            player1 = Connect4.getSingletonConnect4().getPlayer("nickName1");
            player2 = Connect4.getSingletonConnect4().getPlayer("nickName2");
            goToHome();
            // TEST CODE ENDS
            stage.setTitle(rb.getString("app_name"));
        } catch (Connect4DAOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void showNYI() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(Main.rb.getString("nyi"));
        alert.setHeaderText(Main.rb.getString("nyi_title"));
        alert.setContentText(Main.rb.getString("nyi_explanation"));

        alert.showAndWait();
    }

    public static void showSettings(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/settings.fxml"),
                    rb
            );
            Stage settingsStage = new Stage();
            settingsStage.setScene(new Scene(loader.load()));
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(owner);
            settingsStage.setTitle(rb.getString("config_title"));
            settingsStage.setResizable(false);
            Image iconImage = new Image(Main.class.getResourceAsStream("/resources/img/settings.png"));
            settingsStage.getIcons().add(iconImage);
            settingsStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/home.fxml"),
                    rb
            );
            changeContent(loader.load());
            stage.setMinHeight(650);
            stage.setMinWidth(900);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void startGame(Player P1, Player P2) {
        startGame(new GameController(P1, P2));
    }

    public static void startGameAI(Player P1, Difficulty difficulty) {
        startGame(new GameController(P1, difficulty));
    }

    private static void startGame(GameController controller) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/game.fxml"),
                    rb
            );
            loader.setController(controller);
            changeContent(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToAuthenticate(int player) {
        changeContent(getAuthenticateScene(player).getRoot());
        stage.setMinHeight(650);
        stage.setMinWidth(700);
    }

    public static Scene getAuthenticateScene(int player) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/authenticate.fxml"),
                    rb
            );
            AuthenticateController controller = new AuthenticateController(player);
            loader.setController(controller);
            return new Scene(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void changeContent(Node newRoot) {
        Node oldRoot = null;
        if (root.getChildren().size() > 0) {
            oldRoot = root.getChildren().get(0);
        }
        Animations.fadeIn(root, oldRoot, newRoot);
    }

    @Override
    public void start(Stage stage) {
        try {
            Connect4.getSingletonConnect4();
            // TEST CODE BEGINS
            if (Connect4.getSingletonConnect4().getPlayer("nickName1") == null) {
                Connect4.getSingletonConnect4().createDemoData(3, 3, 3);
            }
            // TEST CODE ENDS
        } catch (Connect4DAOException e) {
            System.err.print(e);
        }
        Main.stage = stage;
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/main.fxml"),
                    rb
            );
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/img/icon.png")));
        stage.show();
    }

    @FXML
    private void initialize() {
        startWithLanguage(DEF_LANG);
    }

}
