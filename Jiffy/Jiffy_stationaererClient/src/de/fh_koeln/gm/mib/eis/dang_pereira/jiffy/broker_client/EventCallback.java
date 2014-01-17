package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import UI.JiffyController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;


/**
 * Klasse, die Methoden implementiert, die Paho MQTT bei entsprechenden Ereignissen aufruft
 * 
 * @author dang_pereira
 *
 */
public class EventCallback implements MqttCallback {
	
	private JiffyController jc = null;
	
	
	public EventCallback(JiffyController jc) {
		this.jc = jc;
	}
	
	
	@Override
	public void connectionLost(Throwable t) {
		System.err.println("Verbindung ist verloren gegangen: " + t.getMessage());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMsg) throws Exception {
		
		/* Payload aus der Mqtt-Message extrahieren */
		String payload = new String(mqttMsg.getPayload());
		
		/* An den JiffyController weiterreichen */
		
		this.jc.handleMessage(topic, payload);
		
	}

}
