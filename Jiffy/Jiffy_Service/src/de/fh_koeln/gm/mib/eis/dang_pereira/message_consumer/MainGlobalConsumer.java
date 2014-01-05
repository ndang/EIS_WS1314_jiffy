package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer;

import java.io.FileInputStream;

import javax.net.SocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.OwnSSLSocketFactory;



public class MainGlobalConsumer {
	
	private MqttClient mqttClient;
	
	public MainGlobalConsumer() {
		
		/* Instanz des Config-Objekts beziehen */
		Config cfg = Config.getInstance();
		
		String	broker_host = cfg.broker.host;
		Integer	broker_port = cfg.broker.port;
		String	broker_user = cfg.broker.user;
		String	broker_pass = cfg.broker.pass;

		
		try {
			mqttClient = new MqttClient(broker_host + ":" + broker_port, "Jiffy Service Consumer", new MemoryPersistence());
			
			MqttConnectOptions mqttOpts = new MqttConnectOptions();
			mqttOpts.setUserName(broker_user);
			mqttOpts.setPassword(broker_pass.toCharArray());
			mqttOpts.setCleanSession(false);
			SocketFactory sf = OwnSSLSocketFactory.getSocketFactory(
											new FileInputStream(cfg.ssl.path_ks), cfg.ssl.pass_ks,
											new FileInputStream(cfg.ssl.path_ts), cfg.ssl.pass_ts);
			
			mqttOpts.setSocketFactory(sf);
			
			mqttClient.connect(mqttOpts);
			
			/* Alle "offiziellen" Topics subscriben, die auch in der DB abgelegt werden sollen */
			mqttClient.subscribe("*/official");
			
			/*
			 * Callback-Objekt angeben, der diverse Methoden implementiert, die aufgerufen werden,
			 *	wenn bestimmte Ereignisse eintreffen (einkommende Nachricht, Verbindungsverlust etc.) 
			 */
			mqttClient.setCallback(new EventCallback());
			
		} catch (MqttException e) {
			System.err.println("Connection failed!");
			System.err.println(e.getMessage());
		} catch  (Exception e) {
			System.err.println("SSLSocketFailure!");
		}
	}
	

	public static void main(String[] args) {
		
		/* Einstiegs- bzw. Startpunkt für den Message-Subscriber / -Consumer */
		
		new MainGlobalConsumer();
		
	}

}
