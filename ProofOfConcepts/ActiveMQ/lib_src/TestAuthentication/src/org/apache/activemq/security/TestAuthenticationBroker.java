
package org.apache.activemq.security;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;


public class TestAuthenticationBroker extends AbstractAuthenticationBroker {
	
	private String host = "localhost";
	private String database = "auth";
	private String table = "user_auth";
	private String username = "root";
	private String password = "password";
	
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	

    public TestAuthenticationBroker(Broker next, String host, String database, String table, String username, String password) {
        super(next);
        
        this.host = host;
        this.database = database;
        this.table = table;
        this.username = username;
        this.password = password;
    }


    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {

        SecurityContext s = context.getSecurityContext();
        if (s == null) {
            
            if (info.getUserName() != null && info.getPassword() != null) {
            	
        		try {
        			Class.forName("com.mysql.jdbc.Driver");
        		}
        		catch (ClassNotFoundException e) {
        			throw new Exception("JDBC-Driver für MySQL wurde nicht gefunden!");
        		}

        		try {
        			con = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.database, this.username, this.password);
        			stmt = con.createStatement();
        			rs = stmt.executeQuery("SELECT COUNT(user_id) FROM " + this.table + " WHERE name = '"+info.getUserName()+"' AND password = '"+info.getPassword()+"'");
        		}
        		catch(SQLException e) {
        			throw new Exception("Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden!");
        		}
            	
        		rs.next();
        		
            	if(rs.getString(1).equals("1")) {
            		s = new SecurityContext(info.getUserName()) {
                        @Override
                        public Set<Principal> getPrincipals() {
                            Set<Principal> groups = new HashSet<Principal>();
                            return groups;
                        }
                    };
            		}
                else {
                	throw new SecurityException("User name [" + info.getUserName() + "] or password is invalid.");
                }
            }
            else {
            	throw new SecurityException(
                        "User name [" + info.getUserName() + "] or password is invalid.");
            }

            context.setSecurityContext(s);
            securityContexts.add(s);
        }

        try {
            super.addConnection(context, info);
        } catch (Exception e) {
            securityContexts.remove(s);
            context.setSecurityContext(null);
            throw e;
        }
    }
}
