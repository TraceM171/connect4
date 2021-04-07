package ipcconnect4.auth;

import DBAccess.Connect4DAOException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Connect4;

public class AuthenticateController {

    private final int playerNumber;

    @FXML
    private HBox subscene;

    public AuthenticateController(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    @FXML
    public void initialize() {
        switch (playerNumber) {
            case 1:
                setLoginMode((logedPlayer) -> {
                    HomeController.player1 = logedPlayer;
                });
                break;
            case 2:
                setLoginMode((logedPlayer) -> {
                    HomeController.player2 = logedPlayer;
                });
                break;
        }
    }

    private void setLoginMode(LoginController.LoginListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ipcconnect4/view/login.fxml"));
            LoginController controller = new LoginController(listener);
            loader.setController(controller);
            Parent root = loader.load();

            Connect4.getSingletonConnect4().createDemoData(3, 3, 3);

            subscene.getChildren().add(root);
            HBox.setHgrow(root, Priority.ALWAYS);

        } catch (IOException | Connect4DAOException ex) {
            Logger.getLogger(AuthenticateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
