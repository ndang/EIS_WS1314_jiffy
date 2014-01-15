/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Ngoc-Anh
 */
public class JiffyController implements Initializable {

    @FXML Label lbl_start_msg;
    @FXML VBox vbox_start;
    @FXML Button btn_msg_send;
    @FXML TextArea textarea_msg;
    @FXML ToggleButton toggle_msg_relevance;
    /**
     * Initializes the controller class.
     */
    
    @FXML
    private void clear(ActionEvent event) {
        vbox_start.getChildren().clear();
        lbl_start_msg.setText("Keine Narichten.");
    }
    
    @FXML
    private void send(ActionEvent event) {
        String text = textarea_msg.getText();
        if (text.length() != 0){
            if (toggle_msg_relevance.isSelected()){
                System.out.println("Dies ist eine Eilmeldung.");
            }
            System.out.println(text + " wird versendet.");
            textarea_msg.setText("");
        }
        else{
            new Popup("Nachricht ist leer!");
        }
    }
    
    @FXML
    private void addIcon1(ActionEvent event) {
        String text = textarea_msg.getText();
        textarea_msg.setText(text + " Icon1 ");
    }
    
    @FXML
    private void addIcon2(ActionEvent event) {
        String text = textarea_msg.getText();
        textarea_msg.setText(text + " Icon2 ");
    }
    
    @FXML
    private void addIcon3(ActionEvent event) {
        String text = textarea_msg.getText();
        textarea_msg.setText(text + " Icon3 ");
    }
    
    @FXML
    private void addIcon4(ActionEvent event) {
        String text = textarea_msg.getText();
        textarea_msg.setText(text + " Icon4 ");
    }
    
    @FXML
    private void addIcon5(ActionEvent event) {
        String text = textarea_msg.getText();
        textarea_msg.setText(text + " Icon5 ");
    }
    
    @FXML
    private void addReceiver(ActionEvent event) {
        new Popup("Empänger hinzufügen");
    }
   
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        update();
    }    
    
    private void update(){
        int count = 0;
        // count = getMessages();
        ArrayList<Object> listmsg = new ArrayList<Object>();
        listmsg.add("hey");
        listmsg.add("I");
        listmsg.add("Just");
        listmsg.add("Met");
        listmsg.add("You");
        for (Object msg : listmsg){
            Label temp = new Label();
            temp.setText((String)msg);
            vbox_start.getChildren().add(temp);  
            count++;
        }
        lbl_start_msg.setText( count + " neue Nachricht(en).");
    }
    
}
