package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.broker_client;

import java.io.FileInputStream;

import javax.net.SocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.OwnSSLSocketFactory;


public class BrokerClient {

	
	private static BrokerClient _instance = null;
	private MqttClient mqttClient = null;
	private boolean connected = false;
	
	private BrokerClient() {
	}
	
	
	public boolean isConnected() {
		return this.connected;
	}
	
	
	public boolean connect(String username , String password) {
		
		/* Instanz des Config-Objekts beziehen */
		Config cfg = Config.getInstance();
		
		String	broker_host = cfg.broker.host;
		Integer	broker_port = cfg.broker.port;
		String	broker_user = username;
		String	broker_pass = password;

		try {
			
			System.out.print("Broker-Client startet...  ");
			
			
			System.out.println(broker_host + ":" + broker_port);
			
			mqttClient = new MqttClient(broker_host + ":" + broker_port, username, new MemoryPersistence());
			
			/* Alle MQTT relevanten Konfigurationseinstellungen vornehmen */
			MqttConnectOptions mqttOpts = new MqttConnectOptions();
			mqttOpts.setUserName(broker_user); /* Standard: jiffy_service */
			mqttOpts.setPassword(broker_pass.toCharArray()); /* Standard: 12345 */
			mqttOpts.setCleanSession(false);
			SocketFactory sf = OwnSSLSocketFactory.getSocketFactory(
											new FileInputStream(cfg.ssl.path_ks), cfg.ssl.pass_ks,
											new FileInputStream(cfg.ssl.path_ts), cfg.ssl.pass_ts);
			
			mqttOpts.setSocketFactory(sf);
			
			mqttClient.connect(mqttOpts);
			
			/*
			 * Callback-Objekt angeben, der diverse Methoden implementiert, die aufgerufen werden,
			 *	wenn bestimmte Ereignisse eintreffen (einkommende Nachricht, Verbindungsverlust etc.) 
			 */

			this.connected = true;
			
		} catch (MqttException e) {
			System.out.println();
			System.err.println("Verbindung konnte nicht aufgebaut werden: " + e.getMessage());
		} catch (Exception e) {
			System.out.println();
			System.err.println("Fehler beim Vorbereiten der Verbindungsherstellung zum Broker: " + e.getMessage());
		}
		
		return this.connected;
	}
	
	
	public void setCallback(EventCallback ec) {
		mqttClient.setCallback(ec);
	}
	
	
	public boolean publishToTopic(String topic, String payload) {
		
		boolean status = false;
		
		MqttMessage msg = new MqttMessage(payload.getBytes());
		try {
			mqttClient.publish(topic, msg);
			status = true;
		} catch (MqttException e) {
			System.err.println("Konnte Nachricht nicht versenden: " + e.getMessage());
		}
		
		return status;
	}
	
	
	public boolean subscribeTopic(String topicname) {
		boolean status = false;

		try {
			mqttClient.subscribe(topicname);
			System.out.println("Topic abonniert: " + topicname);
			status = true;
		} catch (MqttException e) {
			System.err.println("Konnte Topic nicht abonnieren: " + e.getMessage());
		}
		
		return status;
	}
	
	
	public static BrokerClient getInstance() {
		
		if(_instance == null) {
			_instance = new BrokerClient();
		}
		
		return _instance;
	}
	
}
