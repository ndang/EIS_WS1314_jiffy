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

		boolean connected = false;
		
		try {
			
			System.out.print("Consumer startet...  ");
			
			mqttClient = new MqttClient(broker_host + ":" + broker_port, "Jiffy Service Consumer", new MemoryPersistence());
			
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
			
			/* Alle "offiziellen" Topics subscriben, die auch in der DB abgelegt werden sollen */
			mqttClient.subscribe("official/*");
			
			/*
			 * Callback-Objekt angeben, der diverse Methoden implementiert, die aufgerufen werden,
			 *	wenn bestimmte Ereignisse eintreffen (einkommende Nachricht, Verbindungsverlust etc.) 
			 */
			mqttClient.setCallback(new EventCallback());
			
			connected = true;
			
		} catch (MqttException e) {
			System.out.println();
			System.err.println("Verbindung konnte nicht aufgebaut werden: " + e.getMessage());
		} catch (Exception e) {
			System.out.println();
			System.err.println("Fehler bei der Initialisierung des SSL-Kontextes: " + e.getMessage());
		}
		
		if(connected) {
			System.out.println("erfolgreich gestartet!");
		}
		else {
			System.err.println("Consumer konnte nicht gestartet werden!");
			System.exit(1);
		}
	}
	

	public static void main(String[] args) {
		
		/* Einstiegs- bzw. Startpunkt für den Message-Subscriber / -Consumer */
		
		new MainGlobalConsumer();
		
	}

}
