/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Ngoc-Anh
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField lbl_name;
    @FXML
    private PasswordField lbl_pw;
    
    
    @FXML
    private void login(ActionEvent event) {
        
        String name = lbl_name.getText();
        String pw = lbl_pw.getText();
        
        boolean loginValid = true; // TODO: Benutzername + Passwort überprüfen
        if ( loginValid ){
            new Jiffy();
            //new Popup("Login successed");
            System.out.println("You're logged in, "+ name +" !");
            Stage stage = (Stage) lbl_name.getScene().getWindow();
            stage.close();
        }
        else{
            new Popup("Login Failed");
            //System.err.println("Login failed!");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
