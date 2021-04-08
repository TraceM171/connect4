package ipcconnect4;

import ipcconnect4.auth.AuthenticateController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Connect4;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        Connect4.getSingletonConnect4().createDemoData(3, 3, 3);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ipcconnect4/view/authenticate.fxml"));
        AuthenticateController controller = new AuthenticateController(2);
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void showNYI() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("NYI");
        alert.setHeaderText("No implementat");
        alert.setContentText("Aquesta funció encara no ha sigut implementada!");

        alert.showAndWait();
    }

}
