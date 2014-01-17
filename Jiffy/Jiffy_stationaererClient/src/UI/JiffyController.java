/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client.BrokerClient;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client.EventCallback;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.data_access.RESTDataHandler;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.Entry;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.LocalMessage;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 * 
 * @author dang_pereira
 */
public class JiffyController implements Initializable {
	
	private ArrayList<LocalMessage> msgList = new ArrayList<LocalMessage>();
	
	final RESTDataHandler rdh = RESTDataHandler.getInstance();
	final BrokerClient bc = BrokerClient.getInstance();
	
	private final JiffyController self = this;
	private ObjectMapper jmapper = new ObjectMapper();
	
	public JiffyController() {
		
		/* Erstmal alle nötigen Topics abonnieren;
    	 * muss in einem eigenen Thread laufen, um den UI-Thread nicht zu blockieren */
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
	        	//ArrayList<String> topics = rdh.getTopicsToSubscribe().getTopics();
	        	
				
				Topics topics = rdh.getTopicsToSubscribe();
				if(topics != null) {
		        	for(String topic: topics.getTopics()) {
		        		bc.subscribeTopic(topic);
		        	}
				}
	        	
	        	bc.setCallback(new EventCallback(self));
			}
		}).start();
    	
	}
	
	// allgemein
	private int entryCount;
	@FXML
	TabPane tabpane;

	// start-tab
	@FXML
	Label lbl_start_msg;
	@FXML
	VBox vbox_start;
	@FXML
	ToggleButton toggle_msg_relevance;

	@FXML
	Label lbl_start_msg1;
	@FXML
	Label lbl_start_msg2;
	@FXML
	Label lbl_start_msg3;

	// send-tab
	@FXML
	Button btn_msg_send;
	@FXML
	TextArea textarea_msg;
	@FXML
	Label lbl_msg_received;

	@FXML
	private void clear(ActionEvent event) {
		vbox_start.getChildren().clear();
		lbl_start_msg.setText("Keine Narichten.");
	}

	@FXML
	private void delete(ActionEvent event) {
		System.out.println("Nachricht wird gel�scht.");
	}

	@FXML
	private void send(ActionEvent event) {
		String text = textarea_msg.getText();
		if (text.length() != 0) {
			if (toggle_msg_relevance.isSelected()) {
				System.out.println("Dies ist eine Eilmeldung.");
			}
			System.out.println(text + " wird versendet.");
			textarea_msg.setText("");
		} else {
			new Popup("Nachricht ist leer!");
		}
	}

	@FXML
	public void switchtab(ActionEvent event, int tab) {
		SingleSelectionModel<Tab> selectionModel = tabpane.getSelectionModel();
		selectionModel.select(tab);

		switch (tab) {
		case 1:
			updateMsg();
			break;
		case 2:
			
			break;
		case 3:
			;
			break;
		case 4:
			
			break;
		default:
			break;
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
		entryCount = 0;
		vbox_start.setSpacing(10);

		ArrayList<Object> listmsg = new ArrayList<Object>();
		listmsg.add("hey");
		listmsg.add("I");
		listmsg.add("Just");

		for (Object msg : listmsg) {
			entryCount++;
			vbox_start.getChildren().add(
					new Entry(this, "ich", "du", msg.toString()));
		}

		updateStart();
	}

	private void updateStart() {
		lbl_start_msg.setText(entryCount + " neue Nachricht(en).");
	}

	private void updateMsg() {
		String text = "Nachricht auf welche geantwortet wird.";
		lbl_msg_received.setText(text);
	}

	final public void deleteEntry(Entry entry) {
		entryCount--;
		vbox_start.getChildren().remove(entry);
		updateStart();
	}

	
	
	public void handleMessage(String topic, String payload) {
		
		LocalMessage lm = new LocalMessage();
		
		Message msg;
		try {
			msg = jmapper.readValue(payload, Message.class);
			
			lm.setMsgStruct(msg);
			
			Integer userId = msg.getFromUserId().getID();
			User u = rdh.getUser(userId);
			
			lm.setUserId(userId);
			lm.setName(u.getName());
			lm.setText(msg.getMsgText());
			
			msgList.add(lm);
			System.out.println("Message added!");
			
		} catch (Exception e) {
			System.err.println("Fehler beim Marshalling: " + e.getMessage());
		}
	}
	
}