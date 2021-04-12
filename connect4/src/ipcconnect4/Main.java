package ipcconnect4;

import DBAccess.Connect4DAOException;
import ipcconnect4.model.GameWithAI;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.ui.auth.AuthenticateController;
import ipcconnect4.ui.game.GameController;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class Main extends Application {

    private static final Locale DEF_LANG = new Locale("ca", "ES");

    public static Player player1, player2;
    public static ResourceBundle rb;
    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeLanguage(Locale locale) {
        startWithLanguage(locale);
    }

    private static void startWithLanguage(Locale locale) {
        try {
            Locale.setDefault(locale);
            rb = ResourceBundle.getBundle("ipcconnect4.bundles.Strings", Locale.getDefault());
//          goToAuthenticate(1);
            // TEST CODE BEGINS
            player1 = Connect4.getSingletonConnect4().getPlayer("nickName1");
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
                    Main.class.getResource("/ipcconnect4/view/settings.fxml"),
                    rb
            );
            Stage settingsStage = new Stage();
            settingsStage.setScene(new Scene(loader.load()));
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(owner);
            settingsStage.setTitle(rb.getString("app_name"));
            settingsStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/ipcconnect4/view/home.fxml"),
                    rb
            );
            stage.setScene(new Scene(loader.load()));
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
                    Main.class.getResource("/ipcconnect4/view/game.fxml"),
                    rb
            );
            loader.setController(controller);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToAuthenticate(int player) {
        stage.setScene(getAuthenticateScene(player));
    }

    public static Scene getAuthenticateScene(int player) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/ipcconnect4/view/authenticate.fxml"),
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

    @Override
    public void start(Stage stage) {
        try {
            Connect4.getSingletonConnect4().createDemoData(3, 3, 3);

            Main.stage = stage;
            startWithLanguage(DEF_LANG);
            stage.show();
        } catch (Connect4DAOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
