package ipcconnect4.ui.auth;

import ipcconnect4.Main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.Player;

public class AuthenticateController {

    private final int playerNumber;
    private Node lastContent;

    @FXML
    private HBox subscene;

    public AuthenticateController(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    @FXML
    public void initialize() {
        setLoginMode(new LoginController.LoginListener() {
            @Override
            public void onLogin(Player logedPlayer) {
                switch (playerNumber) {
                    case 1:
                        Main.player1 = logedPlayer;
                        break;
                    case 2:
                        Main.player2 = logedPlayer;
                        break;
                }
            }

            @Override
            public void onForgotAction() {
                setForgotMode(() -> {
                    contentGoBack();
                });
            }
        });
    }
    
    @FXML
    public void settingsAction(MouseEvent event) {
        Main.showSettings();
    }

    private void setLoginMode(LoginController.LoginListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ipcconnect4/view/login.fxml"),
                    Main.rb
            );
            LoginController controller = new LoginController(listener);
            loader.setController(controller);
            Parent root = loader.load();

            setContent(root);
        } catch (IOException ex) {
            Logger.getLogger(AuthenticateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setForgotMode(ForgotController.ForgotListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ipcconnect4/view/forgot.fxml"),
                    Main.rb
            );
            ForgotController controller = new ForgotController(listener);
            loader.setController(controller);
            Parent root = loader.load();

            setContent(root);
        } catch (IOException ex) {
            Logger.getLogger(AuthenticateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setContent(Node content) {
        ObservableList<Node> children = subscene.getChildren();
        if (children.size() > 0) {
            lastContent = children.get(0);
        }
        subscene.getChildren().clear();
        subscene.getChildren().add(content);
        HBox.setHgrow(content, Priority.ALWAYS);
        subscene.requestFocus();
    }

    private void contentGoBack() {
        if (lastContent != null) {
            setContent(lastContent);
        }
    }

}
