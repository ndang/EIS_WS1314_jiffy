package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import UI.JiffyController;

public class Entry extends HBox{
	
	@FXML TabPane tabpane;
	
	private String sender;
	JiffyController jc;
	Entry self;
	
	Label lbl_text;
	ImageView ava;
	Button btn_answer;
	Button btn_delete;
	
	
	
	public Entry( JiffyController jc, String sender, String ava_location, String text ){
		
		// allgemeines
		super (10);
		this.sender = sender;
		this.computeMinHeight(200);
		this.jc = jc;
		this.self = this;
		
		// Nodes erstellen
	    Image image = new Image("template.jpg");
	    ava = new ImageView(image);
	    ava.setFitHeight(100);
	    ava.setFitWidth(100);
	    
	    this.lbl_text = new Label();
	    lbl_text.setText(text);
	    
	    this.btn_answer = new Button();
	    btn_answer.setText(">");
	    btn_answer.setDefaultButton(true);
	    
	    this.btn_delete = new Button();
	    btn_delete.setText("x");
	    btn_delete.setDefaultButton(true);
	    
	    final JiffyController jc1 = jc; 
	    
	    // Funktionalitäten
	    btn_answer.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	jc1.switchtab(null, 1);
	        }
	    });
	    
	    btn_delete.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	        	jc1.deleteEntry(self);
	        }
	    });
	    
	    // Nodes hinzufügen
	    this.getChildren().add(ava);
	    this.getChildren().add(lbl_text);
	    this.getChildren().add(btn_answer);
	    this.getChildren().add(btn_delete);

	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
