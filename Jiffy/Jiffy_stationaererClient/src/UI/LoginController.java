/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client.BrokerClient;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.data_access.RESTDataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author dang_pereira
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
        
        boolean loginValid = false;
        
        /* Überprüfen, ob der REST-Endpoint und der Broker überhaupt erreichbar sind */
        final RESTDataHandler rdh = RESTDataHandler.getInstance();
        final BrokerClient bc = BrokerClient.getInstance();
        if(rdh.available() && bc.connect(name, pw)) {
        	
        	Config cfg = Config.getInstance();
        	cfg.username = name;
        	cfg.password = pw;
        	loginValid = true;
        	
            if ( loginValid ){
                new Jiffy();
                System.out.println("You're logged in as, "+ name +" !");
                Stage stage = (Stage) lbl_name.getScene().getWindow();
                stage.close();
            }
            else{
            	new Popup("Login Failed");
            }
        	
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
