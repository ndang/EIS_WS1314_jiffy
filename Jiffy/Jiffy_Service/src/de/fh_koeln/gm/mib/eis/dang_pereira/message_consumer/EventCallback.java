package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.IDataLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.GuardianMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.SchoolMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;


/**
 * Klasse, die Methoden implementiert, die Paho MQTT bei entsprechenden Ereignissen aufruft
 * 
 * @author dang_pereira
 *
 */
public class EventCallback implements MqttCallback {
	
	
	private ObjectMapper jmapper = new ObjectMapper();
	
	
	@Override
	public void connectionLost(Throwable t) {
		System.err.println("Verbindung ist verloren gegangen: " + t.getMessage());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		
		/* Die Central-Unit agiert nicht als Publisher, weshalb diese Methode irrelevant ist */
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMsg) throws Exception {
		
		/* Payload aus der Mqtt-Message extrahieren */
		String payload = new String(mqttMsg.getPayload());
		
		/* Unmarshalling durchführen lassen */
		Message msg = jmapper.readValue(payload, Message.class);
		
		
		/* Relevante Kriterien aus der Nachricht extrahieren, um sie besser einordnen zu können */
	
		String msgType	= msg.getMsgType();
		
		if(msgType == null) {
			System.err.println("Unbekannter Nachrichtentyp");
			System.out.println(msg);
			return;
		}
		
		Id fromUserId		= msg.getFromUserId();
		Id toUserId			= msg.getToUserId();
		
		if(fromUserId == null) {
			System.out.println("Kein TEACHER angegeben!");
			return;
		}
		
		String msgSubType = null;

		switch(msgType.toLowerCase()) {
			case "school":
				
				/* Von TEACHER an GUARDIAN */
				SchoolMsg schoolMsg = msg.getSchool();
				
				if(schoolMsg == null) {
					System.err.println("SchoolMsg-Body ist nicht gegeben!");
					return;
				}
				
				msgSubType = schoolMsg.getMsgSubType();
				
				if(msgSubType == null) {
					System.err.println("SchoolMsg-Body ist nicht gegeben!");
					return;
				}
				
				/* msgSubType in Kleinbuchstaben */
				String lcMsgSubType = msgSubType.toLowerCase();
				
				if(lcMsgSubType == "grade") {
					
					/* Nur TEACHER an GUARDIAN möglich, da sich auf eine Note bezogen wird */
					if(!UserDBAuth.comPartnersAreOfType(fromUserId.getID(), "TEACHER", toUserId.getID(), "GUARDIAN")) {
						System.err.println("Absender/Empfänger-Kombination ist nicht erlaubt!");
						return;
					}
				} else if(lcMsgSubType == "info"){
					
					/* Absender muss TEACHER sein; Empfänger muss nicht gegeben sein, da Nachricht evtl. Klassenbroadcast */
					if(!UserDBAuth.idIsOfUserType(fromUserId.getID(), "TEACHER")) {
						System.err.println("Absender ist kein Lehrer!");
						return;
					}
				}
				
				IDataLayer dbl;
				
				switch(lcMsgSubType) {
					case "grade":
						/* Notenbekanntgabe */
						
						if(msg.getSchool().getGrade() == null) {
							System.err.println("SchoolMsg/Grade-Body ist nicht gegeben!");
							return;
						}

						
						dbl = DBLayer.getInstance();
						
						if(dbl.placeSchoolGradeMsg(msg)){
							System.out.println("Notennachricht wurde erfolgreich eingepflegt!");
						} else {
							System.err.println("Notennachricht konnte nicht eingepflegt werden!");
						}
						
						break;
					case "info":
						/* Normale Infonachricht */
						
						if(msg.getSchool().getInfo() == null) {
							System.err.println("SchoolMsg/Info-Body ist nicht gegeben!");
							return;
						}
						
						
						dbl = DBLayer.getInstance();
						
						if(dbl.placeSchoolInfoMsg(msg)){
							System.out.println("Schulnachricht wurde erfolgreich eingepflegt!");
						} else {
							System.err.println("Schulnachricht konnte nicht eingepflegt werden!");
						}
						
						break;
					default:
						System.err.println("Unbekannter Unternachrichtentyp");
					
				}
				
				break;
			
			case "guardian":
				
				
				if(toUserId == null) {
					System.out.println("Kein TEACHER angegeben!");
					return;
				}
				
				if(!UserDBAuth.comPartnersAreOfType(fromUserId.getID(), "GUARDIAN", toUserId.getID(), "TEACHER")) {
					System.err.println("Absender/Empfänger-Kombination ist nicht erlaubt!");
					return;
				}
				
				
				/* Von GUARDIAN an TEACHER */
				GuardianMsg guardianMsg = msg.getGuardian();
				
				if(guardianMsg == null) {
					System.err.println("GuardianMsg-Body ist nicht gegeben!");
					return;
				}
				
				msgSubType = guardianMsg.getMsgSubType();
				
				if(msgSubType == null) {
					
					/* Eine normale Nachricht; als Fragenachricht verwendbar */
					
					dbl = DBLayer.getInstance();
					
					if(dbl.placeGuardianMsg(msg)){
						System.out.println("Erziehungsberechtigtennachricht wurde erfolgreich eingepflegt!");
					} else {
						System.err.println("Erziehungsberechtigtennachricht konnte nicht eingepflegt werden!");
					}
				}
				else {
					
					String lcGMsgSubType = msgSubType.toLowerCase();
					
					switch(lcGMsgSubType) {
						case "excuse":
							/* Entschuldigungsnachricht */
							
							if(msg.getGuardian().getExcuse() == null) {
								System.err.println("GuardianMsg/Excuse-Body ist nicht gegeben!");
								return;
							}
							
							dbl = DBLayer.getInstance();
							
							if(dbl.placeGuardianExcuseMsg(msg)){
								System.out.println("Entschuldigungsnachricht wurde erfolgreich eingepflegt!");
							} else {
								System.err.println("Entschuldigungsnachricht konnte nicht eingepflegt werden!");
							}
							
							break;
						default:
							System.err.println("Unbekannter Unternachrichtentyp");
					}
					
					
				}
				
				break;
			
			default:
				System.err.println("Unbekannter Nachrichtentyp!");
		}
		
	}

}
