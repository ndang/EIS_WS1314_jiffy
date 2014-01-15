/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 *
 * @author Ngoc-Anh
 */
public class Jiffy extends Stage{
    
    private Stage stage;
    private Label nachrichten; 
    
    public Jiffy(){
        stage = this;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Jiffy.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(Jiffy.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Scene scene = new Scene(root);
        stage.setTitle("jiffy");
        stage.setScene(scene);
        stage.show();
    }  
}
