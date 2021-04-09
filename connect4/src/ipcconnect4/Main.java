package ipcconnect4;

import DBAccess.Connect4DAOException;
import ipcconnect4.ui.auth.AuthenticateController;
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
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeLanguage(Locale locale) {
        stage.setScene(startWithLanguage(locale));
    }

    private static Scene startWithLanguage(Locale locale) {
        try {
            Locale.setDefault(locale);
            rb = ResourceBundle.getBundle("ipcconnect4.bundles.Strings", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/ipcconnect4/view/authenticate.fxml"),
                    rb
            );
            AuthenticateController controller = new AuthenticateController(1);
            loader.setController(controller);
            return new Scene(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void showNYI() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(Main.rb.getString("nyi"));
        alert.setHeaderText(Main.rb.getString("nyi_title"));
        alert.setContentText(Main.rb.getString("nyi_explanation"));

        alert.showAndWait();
    }

    public static void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/ipcconnect4/view/settings.fxml"),
                    rb
            );
            Stage settingsStage = new Stage();
            settingsStage.setScene(new Scene(loader.load()));
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            Connect4.getSingletonConnect4().createDemoData(3, 3, 3);

            Main.stage = stage;
            stage.setScene(startWithLanguage(DEF_LANG));
            stage.show();
        } catch (Connect4DAOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
