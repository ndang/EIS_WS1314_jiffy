package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class EventCallback implements MqttCallback {
	
	@Override
	public void connectionLost(Throwable t) {
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		
		String payload = new String(msg.getPayload());
		
		System.out.println(topic + " | " + payload);
		
	}

}
