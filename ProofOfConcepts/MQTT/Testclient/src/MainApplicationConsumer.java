
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainApplicationConsumer {

	public static void main(String[] args) {
		
        System.setProperty("javax.net.ssl.trustStore", "res/client.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("javax.net.ssl.keyStore", "res/broker.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
		
        
		MQTT mqtt = new MQTT();
		
		mqtt.setCleanSession(false);
		mqtt.setClientId("Consumer2");
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
		
		mqtt.setUserName("Peter2");
		mqtt.setPassword("Christa2");
		
		//BlockingConnection con = mqtt.blockingConnection();

		
		final CallbackConnection connection = mqtt.callbackConnection();
		
		connection.connect(new Callback<Void>() {
			
			@Override
			public void onSuccess(Void arg0) {
				
				System.out.println("Connected!");
				
				final String topicname = "test1";
				
				Topic[] topics = {new Topic(topicname, QoS.AT_LEAST_ONCE)};
				connection.subscribe(topics, new Callback<byte[]>() {

					@Override
					public void onFailure(Throwable arg0) {
					}

					@Override
					public void onSuccess(byte[] arg0) {
						System.out.println("Subcribed to \"" + topicname + "\"");
					}
					
				});
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				System.out.println("Nope!");
			}
		});
		
		connection.listener(new Listener() {
			
			@Override
			public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
				System.out.println("Topicname: " + topic.toString());
				System.out.println("Body: " + body.ascii());
				System.out.println();
				
				ack.run();
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				
			}
			
			@Override
			public void onDisconnected() {
				
			}
			
			@Override
			public void onConnected() {
				
			}
		});
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
