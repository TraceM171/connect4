package ipcconnect4.ui.auth;

import ipcconnect4.Main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.Player;

public class AuthenticateController {

    private final int playerNumber;
    private Node lastContent;
    private boolean full;

    @FXML
    private HBox subscene;
    @FXML
    private HBox centerHB;

    public AuthenticateController(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    @FXML
    public void initialize() {
        full = playerNumber == 2;
        setLoginMode(new LoginController.LoginListener() {
            @Override
            public void onLogin(Player logedPlayer) {
                switch (playerNumber) {
                    case 1:
                        Main.player1 = logedPlayer;
                        Main.goToHome();
                        break;
                    case 2:
                        Main.player2 = logedPlayer;
                        close();
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
    private void ranksAction(MouseEvent event) {
        Main.showNYI();
    }
    
    @FXML
    public void settingsAction(MouseEvent event) {
        Main.showSettings( (Stage) subscene.getScene().getWindow());
    }

    private void setLoginMode(LoginController.LoginListener listener) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/resources/fxml/login.fxml"),
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
                    getClass().getResource("/resources/fxml/forgot.fxml"),
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
        if (full) {
            Platform.runLater(() -> {
                centerHB.getChildren().remove(subscene);
                centerHB.getScene().setRoot(new HBox(subscene));
            });
            full = false;
        }
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
    
    private void close() {
        Stage stage = (Stage) subscene.getScene().getWindow();
        stage.close();
    }

}
