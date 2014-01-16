package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {
	
	public final static String TAG = "Jiffy-Client";
	public final static String NOTIFY_LED_COLOR = "#ff0000ff";
	public static String ACTIVITY_ACTIVE_FILTER = "de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activity_active";
	public static String SERVICE_CON_STATUS_FILTER = "de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service_con_status";
	public static String SERVICE_SUBSCRIBE_TOPICS_FILTER = "de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service_subscribe_topics";
	public static String MESSAGE_AVAIL_FILTER = "de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.message_avail";
	
	private static Context ctx;
	private static Config instance;
	
	@JsonProperty("rest_endpoint")
	public REST_Endpoint rest_endpoint = new REST_Endpoint();
	
	@JsonProperty("broker")
	public Broker broker = new Broker();
	
	@JsonProperty("ssl")
	public SSL ssl = new SSL();
	
    
    /* Die Klasse sollte nur über die Klassenfunktion "getInstance" instanziiert werden können */
    private Config() {}
    
	
    /**
     * Konfigurationsdatenklasse für den REST-Endpoint
     * 
     * @author dang_pereira
     */
	public class REST_Endpoint {
		@JsonProperty("host")
		public String host = "https://192.168.43.166";
		
		@JsonProperty("port")
		public Integer port = 8080;
		
		
		public String getHost() {
			return this.host;
		}
		
		public void setHost(String host) {
			SharedPreferences settings = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("rest_endpoint.host", host);
			editor.commit();
			
			this.host = host;
		}
		
		public Integer getPort() {
			return this.port;
		}
		
		public void setPort(Integer port) {
			SharedPreferences settings = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("rest_endpoint.port", port);
			editor.commit();
			
			this.port = port;
		}
		
	}
	
    /**
     * Konfigurationsdatenklasse für den Message Broker (Apache ActiveMQ)
     * 
     * @author dang_pereira
     */
	public class Broker {
		@JsonProperty("host")
		public String host = "ssl://192.168.43.166";
		
		@JsonProperty("port")
		public Integer port = 8883;
		
		
		public String getHost() {
			return this.host;
		}
		
		public void setHost(String host) {
			SharedPreferences settings = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("broker.host", host);
			editor.commit();
			
			this.host = host;
		}
		
		public Integer getPort() {
			return this.port;
		}
		
		public void setPort(Integer port) {
			SharedPreferences settings = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("broker.port", port);
			editor.commit();
			this.port = port;
		}
		
	}
	
    /**
     * Konfigurationsdatenklasse für die Erzeugung der SSL-Kontexte
     * 
     * @author dang_pereira
     */
	public class SSL {
		@JsonProperty("pass_ks")
		public String pass_ks = "koelnerdom";

		@JsonProperty("pass_ts")
		public String pass_ts = "koelnerdom";
	}
    
    
    /**
     * Instanz der Config-Klasse holen; Mitgabe des App-Kontext, um an die Konfigurationsdatei zu kommen (Singleton)
     * 
     * @return Instanz der Config-Klasse
     */
	public static Config getInstance(Context givenCtx) {
		ctx = givenCtx;
		
		return getInstance();
	}
	
	
    /**
     * Instanz der Config-Klasse holen (Singleton)
     * 
     * @return Instanz der Config-Klasse
     */
    public static Config getInstance() {
    	if(ctx == null)
    		return null;
    	
    	if(instance == null) {
    		ObjectMapper m = new ObjectMapper();
    		
    		try {
    			/* Externe Konfigurationsdatei einlesen und ein Konfigurationsobjekt mit diesen neuen Werten erzeugen */
				instance = m.readValue(ctx.getResources().openRawResource(R.raw.config), Config.class);
    		} catch ( IOException e) {
				
				System.err.println("Konfigurationsdatei konnte nicht gelesen oder geparst werden: config.json");
				
				/* Standardwerte nehmen */
				instance = new Config();
			}
			
    		
			/* Evtl. existierenden Konfigurationsänderungen beziehen und Anstelle der Standardwerte setzen */
			SharedPreferences sprefs = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
			
			if(sprefs.contains("rest_endpoint.host"))
				instance.rest_endpoint.host = sprefs.getString("rest_endpoint.host", instance.rest_endpoint.host);
			
			if(sprefs.contains("rest_endpoint.port"))
				instance.rest_endpoint.port = Integer.valueOf(sprefs.getInt("rest_endpoint.port", instance.rest_endpoint.port));
			
			if(sprefs.contains("broker.host"))
				instance.broker.host = sprefs.getString("broker.host", instance.broker.host);
			
			if(sprefs.contains("broker.port"))
				instance.broker.port = Integer.valueOf(sprefs.getInt("broker.port", instance.broker.port));
    	}
    	
    	return instance;
    }
    
}
