package de.fh_koeln.gm.mib.eis.dang_pereira;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {
	
	private static Config instance;
	
	@JsonProperty("mysql_db")
	public MySQL_DB mysql_db = new MySQL_DB();
	
	@JsonProperty("rest_endpoint")
	public REST_Endpoint rest_endpoint = new REST_Endpoint();
	
	@JsonProperty("broker")
	public Broker broker = new Broker();
	
	@JsonProperty("ssl")
	public SSL ssl = new SSL();
	
    
    /* Die Klasse sollte nur über die Klassenfunktion "getInstance" instanziiert werden können */
    private Config() {}
    
    /**
     * Konfigurationsdatenklasse für die MySQL Datenbank
     * 
     * @author dang_pereira
     */
	public class MySQL_DB {
		@JsonProperty("host")
		public String host = "jdbc:mysql://localhost";
		
		@JsonProperty("name")
		public String name = "jiffy";
		
		@JsonProperty("user")
		public String user = "root";
		
		@JsonProperty("pass")
		public String pass = "12345";
		
		@JsonProperty("table")
		public String table = "User";
		
		@JsonProperty("user_field")
		public String userfield = "username";
		
		@JsonProperty("pass_field")
		public String passfield = "pass_hash";
		
		@JsonProperty("pass_algo")
		public String passalgo = "SHA-256";
	}
	
    /**
     * Konfigurationsdatenklasse für den REST-Endpoint
     * 
     * @author dang_pereira
     */
	public class REST_Endpoint {
		@JsonProperty("host")
		public String host = "https://localhost";
		
		@JsonProperty("port")
		public Integer port = 8080;
	}
	
    /**
     * Konfigurationsdatenklasse für den Message Broker (Apache ActiveMQ)
     * 
     * @author dang_pereira
     */
	public class Broker {
		@JsonProperty("host")
		public String host = "ssl://localhost";
		
		@JsonProperty("port")
		public Integer port = 8883;
		
		@JsonProperty("user")
		public String user = "jiffy_service";
		
		@JsonProperty("pass")
		public String pass = "12345";
	}
	
    /**
     * Konfigurationsdatenklasse für die Erzeugung der SSL-Kontexte
     * 
     * @author dang_pereira
     */
	public class SSL {
		@JsonProperty("path_ks")
		public String path_ks = "broker.ks";
		
		@JsonProperty("pass_ks")
		public String pass_ks = "koelnerdom";
		
		@JsonProperty("path_ts")
		public String path_ts = "client.ts";
		
		@JsonProperty("pass_ts")
		public String pass_ts = "koelnerdom";
	}
    
    
    /**
     * Instanz der Config-Klasse holen (Singleton)
     * 
     * @return Instanz der Config-Klasse
     */
    public static Config getInstance() {
    	if(instance == null) {
    		ObjectMapper m = new ObjectMapper();
    		try {
    			/* Externe Konfigurationsdatei einlesen und ein Konfigurationsobjekt mit diesen neuen Werten erzeugen */
				instance = m.readValue(new File(new File("."), "config.json"), Config.class);
			} catch ( IOException e) {
				
				System.err.println("Konfigurationsdatei konnte nicht gelesen oder geparst werden: config.json");
				
				/* Standardwerte nehmen */
				instance = new Config();
			}
    	}
    	
    	return instance;
    }
    
}
