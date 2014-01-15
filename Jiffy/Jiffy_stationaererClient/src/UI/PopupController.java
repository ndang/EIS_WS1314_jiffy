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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Ngoc-Anh
 */
public class PopupController implements Initializable {
    @FXML
    private Label lbl_text;
    @FXML
    private Button btn_fine;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void close(ActionEvent event) {
        Stage stage = (Stage) btn_fine.getScene().getWindow();
        stage.close();
    }
    
}
