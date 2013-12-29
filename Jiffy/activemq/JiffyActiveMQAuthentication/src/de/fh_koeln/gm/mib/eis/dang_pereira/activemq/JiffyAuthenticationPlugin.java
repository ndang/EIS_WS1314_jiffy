
package de.fh_koeln.gm.mib.eis.dang_pereira.activemq;

import java.util.List;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;


/**
 * jiffy authentication plugin
 *
 * @org.apache.xbean.XBean element="jiffyAuthenticationPlugin"
 *                         description="Provides a authentication plugin
 *                         configured with an username and a password"
 *
 *
 */

public class JiffyAuthenticationPlugin implements BrokerPlugin {

    /* Variablen mit den Standardwerten
     *  Die Werte werden von ActiveMQ beim Starten durch die in der Konfigurationsdatei gefundenen Werte ersetzt
     */	
	private String host = "localhost";
	private String database = "auth";
	private String table = "user_auth";
	private String usernameField = "username";
	private String passHashField = "passh_hash";
	private String username = "root";
	private String password = "password";
	private String hashAlgorithm = "SHA-256";
	
    public JiffyAuthenticationPlugin() {
    }

    public JiffyAuthenticationPlugin(List<?> users) {
        this();
    }

    public Broker installPlugin(Broker parent) {
        /* Erzeugen des AuthenticationBrokers mit der Übergabe der aus der Konfiguartionsdatei unter anderem ausgelesenen DB-Anmeldedaten */
        JiffyAuthenticationBroker broker = new JiffyAuthenticationBroker(parent, host, database, table, username, password,
        																	hashAlgorithm, usernameField, passHashField);
        return broker;
    }
    
    

    public void setHost(String host) {
    	this.host = host;
    }
    
    public String getHost() {
    	return this.host;
    }
    
    
    public void setDatabase(String database) {
    	this.database = database;
    }
    
    public String getDatabase() {
    	return this.database;
    }
    
    public void setTable(String table) {
    	this.table = table;
    }
    
    public String getTable() {
    	return this.table;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public String getPassword() {
    	return this.password;
    }
    
    public void setHashAlgorithm(String hash_algorithm) {
    	this.hashAlgorithm = hash_algorithm;
    }
    
    public String getHashAlgorithm() {
    	return this.hashAlgorithm;
    }
    
    public void setUsernameField(String usernameField) {
    	this.usernameField = usernameField;
    }
    
    public String getUsernameField() {
    	return this.usernameField;
    }
    
    public void setPassHashField(String passHashField) {
    	this.passHashField = passHashField;
    }
    
    public String getPassHashField() {
    	return this.passHashField;
    }
}
