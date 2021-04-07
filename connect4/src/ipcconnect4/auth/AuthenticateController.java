/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ipcconnect4.auth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class AuthenticateController implements Initializable {

    @FXML
    private Pane subscene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/ipcconnect4/view/login.fxml"));
            
            subscene.getChildren().add(loginRoot);
        } catch (IOException ex) {
            Logger.getLogger(AuthenticateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
