package ipcconnect4.ui.auth;

import ipcconnect4.Main;
import static ipcconnect4.Main.styleSheet;
import ipcconnect4.util.Animations;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Player;

public class AuthenticateController {

    private final int playerNumber;
    private Node lastContent;
    private boolean full;

    @FXML
    private StackPane subscene;
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
        Main.showSettings((Stage) subscene.getScene().getWindow());
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

            setContent(root, false);
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

            setContent(root, false);
        } catch (IOException ex) {
            Logger.getLogger(AuthenticateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setContent(Parent content, boolean isBack) {
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
        content.getStylesheets().clear();
        content.getStylesheets().add(styleSheet);
        if (isBack) {
            Animations.fadeIn(subscene, lastContent, content);
        } else {
            Animations.fadeIn(subscene, lastContent, content);
        }
        HBox.setHgrow(children.get(0), Priority.ALWAYS);
        
        subscene.requestFocus();
    }
    
    private void contentGoBack() {
        if (lastContent != null) {
            setContent((Parent) lastContent, true);
        }
    }

    private void close() {
        Stage stage = (Stage) subscene.getScene().getWindow();
        stage.close();
    }

}
