package ipcconnect4;

import DBAccess.Connect4DAOException;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.ui.auth.AuthenticateController;
import ipcconnect4.ui.game.GameController;
import ipcconnect4.util.Animation;
import ipcconnect4.util.LocalPreferences;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class Main extends Application {

    public static Player player1, player2;
    public static Difficulty lastAIdifficulty = Difficulty.EASY;
    public static ResourceBundle rb;
    public static String styleSheet;
    public static Stage stage;
    public static Pane root;
    private static Node lastContent;

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeLanguage(Locale locale) {
        LocalPreferences.getInstance().setLang(locale);
        Locale.setDefault(locale);
    }

    public static void changeIsDarkMode(boolean isDark) {
        LocalPreferences.getInstance().setIsDarkMode(isDark);
        if (isDark) {
            styleSheet = Main.class
                    .getResource("/resources/styles/dark.css")
                    .toExternalForm();
        } else {
            styleSheet = Main.class
                    .getResource("/resources/styles/light.css")
                    .toExternalForm();
        }
    }

    public static void reset() {
        rb = ResourceBundle.getBundle("resources.bundles.Strings", Locale.getDefault());
        if (player1 == null) {
            goToAuthenticate(1);
        } else {
            goToHome();
        }
        stage.setTitle(rb.getString("app_name"));
        root.getStylesheets().clear();
        root.getStylesheets().add(styleSheet);
    }

    public static String formatWLang(String resourceId, Object... params) {
        String pattern = rb.getString(resourceId);
        MessageFormat formatter = new MessageFormat(pattern, Locale.getDefault());
        return formatter.format(params);
    }

    public static void showNYI() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(Main.rb.getString("nyi"));
        alert.setHeaderText(Main.rb.getString("nyi_title"));
        alert.setContentText(Main.rb.getString("nyi_explanation"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().clear();
        dialogPane.getStylesheets().add(styleSheet);
        dialogPane.getStyleClass().add("dialog");

        Image iconImage = new Image(Main.class.getResourceAsStream("/resources/img/icon.png"));
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(iconImage);

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
            settingsStage.getScene().getRoot().getStylesheets().clear();
            settingsStage.getScene().getRoot().getStylesheets().add(styleSheet);
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
            changeContent(loader.load(), false, "fadeIn");
            stage.setMinHeight(650);
            stage.setMinWidth(900);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToRanks(boolean logBack) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/ranks.fxml"),
                    rb
            );

            changeContent(loader.load(), logBack, (logBack ? "slideFromTop" : "fadeIn"));
            stage.setMinHeight(650);
            stage.setMinWidth(900);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void goToStats(boolean logBack) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/stats.fxml"),
                    rb
            );

            changeContent(loader.load(), logBack, (logBack ? "slideFromTop" : "fadeIn"));
            stage.setMinHeight(650);
            stage.setMinWidth(900);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void returnFromRanks() {
        changeToBack("slideToTop");
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
            changeContent(loader.load(), false, "fadeIn");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void goToAuthenticate(int player) {
        changeContent(getAuthenticateScene(player), false, "fadeIn");
        stage.setMinHeight(650);
        stage.setMinWidth(700);
    }

    public static Parent getAuthenticateScene(int player) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/resources/fxml/authenticate.fxml"),
                    rb
            );
            AuthenticateController controller = new AuthenticateController(player);
            loader.setController(controller);
            return loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void changeContent(Parent newRoot, boolean logBack, String animation) {
        Node oldRoot = null;
        if (root.getChildren().size() > 0) {
            oldRoot = root.getChildren().get(0);
        }
        if (logBack) {
            lastContent = oldRoot;
        }
        newRoot.getStylesheets().clear();
        newRoot.getStylesheets().add(styleSheet);
        switch (animation) {
            case "slideFromTop":
                new Animation(Animation.NORMAL).slideFromTop(root, oldRoot, newRoot);
                break;
            case "slideToTop":
                new Animation(Animation.NORMAL).slideToTop(root, oldRoot, newRoot);
                break;
            default:
                new Animation(Animation.NORMAL).fadeIn(root, oldRoot, newRoot);
        }
    }
    
    private static void changeToBack(String animation) {
        if (lastContent != null) {
            changeContent((Parent) lastContent, false, animation);
            lastContent = null;
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            Connect4.getSingletonConnect4();
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
        reset();
    }

}
