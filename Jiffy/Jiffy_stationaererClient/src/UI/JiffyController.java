/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client.BrokerClient;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client.EventCallback;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.data_access.RESTDataHandler;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.Entry;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.LocalMessage;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.InfoMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.SchoolMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.User;
import javafx.application.Platform;
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
	
	private LocalMessage msgSelected;
	
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
	public TabPane tabpane;

	// start-tab
	@FXML
	public Label lbl_start_msg;
	@FXML
	public VBox vbox_start;
	@FXML
	public ToggleButton toggle_msg_relevance;

	@FXML
	public Label lbl_start_msg1;
	@FXML
	public Label lbl_start_msg2;
	@FXML
	public Label lbl_start_msg3;

	// send-tab
	@FXML
	public Button btn_msg_send;
	@FXML
	public TextArea textarea_msg;
	@FXML
	public Label lbl_msg_received;

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

		if(msgSelected == null)
			return;
		
		String text = textarea_msg.getText();
		
		int relevance = 0;
		
		if (text.length() != 0) {
			if (toggle_msg_relevance.isSelected()) {
				System.out.println("Dies ist eine Eilmeldung.");
				relevance = 1;
			}
			//System.out.println(text + " wird versendet.");
			
			
			Config cfg = Config.getInstance();
			
			
			/* Nachricht konstruieren */
			
			Message msg = new Message();
			msg.setMsgType("school");
			msg.setFromUserId(new Id(cfg.userId, "/user/" + cfg.userId));
			msg.setToUserId(new Id(msgSelected.getUserId(), "/user/" + msgSelected.getUserId()));
			msg.setMsgText(text);
			
			SchoolMsg s = new SchoolMsg("info", null, new InfoMsg(true, "2014-01-17 22:11:30"), null);
			
			msg.setSchool(s);
			msg.setMsgRelevance(relevance);
			msg.setMsgSubject("Nachricht");
			msg.setMsgUUID(UUID.randomUUID().toString());
			
			/* Nachricht marshallen und veröffentlichen */
			String payload;
			try {
				payload = jmapper.writeValueAsString(msg);
				bc.publishToTopic("private/" + msgSelected.getUserId(), payload);
				textarea_msg.setText("");
			} catch (JsonProcessingException e) {
				System.err.println("Konnte Nachricht nicht verschicken: " + e.getMessage());
			}
			
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

		/*
		ArrayList<Object> listmsg = new ArrayList<Object>();
		listmsg.add("hey");
		listmsg.add("I");
		listmsg.add("Just");

		for (Object msg : listmsg) {
			entryCount++;
			vbox_start.getChildren().add(
					new Entry(this, "ich", "du", msg.toString()));
		}
		*/

		updateStart();
	}

	private void updateStart() {
		
		int count = 0;
		
		for(LocalMessage lm: msgList) {
			if(lm.getUnread())
				count ++;
		}
		
		this.entryCount = count;
		
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

	
	
	public void setAsUnread(LocalMessage lm) {
		for(LocalMessage lmEntry: msgList) {
			if(lmEntry.equals(lm))
				lmEntry.setUnread(false);
		}
		
		updateStart();
	}
	
	
	public void setMsgSelected(LocalMessage lm) {
		this.msgSelected = lm;
	}
	
	
	public void handleMessage(String topic, String payload) {
		
		final LocalMessage lm = new LocalMessage();
		
		Message msg;
		try {
			msg = jmapper.readValue(payload, Message.class);
			
			lm.setMsgStruct(msg);
			
			Integer userId = msg.getFromUserId().getID();
			User u = rdh.getUser(userId);
			
			lm.setUserId(userId);
			lm.setUnread(true);
			lm.setName(u.getName());
			lm.setText(msg.getMsgText());
			
			msgList.add(lm);
			
			entryCount++;
			
			System.out.println("Message added!");
			
			Platform.runLater(new Runnable() {
		      @Override public void run() {
		    	  vbox_start.getChildren().add(0, new Entry(self, lm, "ich", lm.getName(), lm.getText()));
		    	  updateStart();
		      }
		    });
			
			
		} catch (Exception e) {
			System.err.println("Fehler beim Marshalling: " + e.getMessage());
		}
	}
	
}