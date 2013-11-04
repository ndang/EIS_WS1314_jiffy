
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainApplicationProducer {

	public static void main(String[] args) {
		
        System.setProperty("javax.net.ssl.trustStore", "res/client.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.keyStore", "res/broker.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
		
        
		MQTT mqtt = new MQTT();
		
		mqtt.setCleanSession(false);
		mqtt.setClientId("Producer");
		mqtt.setConnectAttemptsMax(1);
		
		try {
	        
			//mqtt.setHost("localhost", 1883);
			mqtt.setHost("ssl://localhost:8883");
			
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
	        mqtt.setSslContext(ctx);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mqtt.setUserName("Peter");
		mqtt.setPassword("Christa");
		
		BlockingConnection con = mqtt.blockingConnection();
		
		
		try {
			con.connect();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		Scanner in = new Scanner(System.in);
		
		System.out.print("Eingabe: ");
		String input = in.nextLine();
		
		while(!input.equalsIgnoreCase("exit")) {
			try {
				con.publish("test1", input.getBytes(), QoS.EXACTLY_ONCE, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.print("Eingabe: ");
			input = in.nextLine();
		}
		
		in.close();
	}
	
}
