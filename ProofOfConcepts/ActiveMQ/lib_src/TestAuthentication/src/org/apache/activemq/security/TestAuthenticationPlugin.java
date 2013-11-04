
package org.apache.activemq.security;

import java.util.List;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;


/**
 * A test authentication plugin
 *
 * @org.apache.xbean.XBean element="testAuthenticationPlugin"
 *                         description="Provides a test authentication plugin
 *                         configured with a username and a password"
 *
 *
 */

public class TestAuthenticationPlugin implements BrokerPlugin {
	
	private String host = "localhost";
	private String database = "auth";
	private String table = "user_auth";
	private String username = "root";
	private String password = "password";
	
    public TestAuthenticationPlugin() {
    }

    public TestAuthenticationPlugin(List<?> users) {
        this();
    }

    public Broker installPlugin(Broker parent) {
        TestAuthenticationBroker broker = new TestAuthenticationBroker(parent, host, database, table, username, password);
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
}
