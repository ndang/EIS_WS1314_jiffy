
package de.fh_koeln.gm.mib.eis.dang_pereira.activemq;

import java.security.MessageDigest;
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
import org.apache.activemq.security.AbstractAuthenticationBroker;
import org.apache.activemq.security.SecurityContext;


public class JiffyAuthenticationBroker extends AbstractAuthenticationBroker {
	
	/* Variablen mit den Standardwerten */
	private String host = "localhost";
	private String database = "auth";
	private String table = "user_auth";
	private String usernameField = "username";
	private String passHashField = "pass_hash";
	private String username = "root";
	private String password = "password";
	private String hashAlgorithm = "SHA-256";
	
	/* Helpervariablen für die Initiierung einer Verbindung,
	 *  dem Erstellen eines Statements und dem Entgegennehmen der zurückgegebenen Datensätze
	 */
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	
    public JiffyAuthenticationBroker(Broker next, String host, String database, String table, String username, String password,
    										String hash_algorithm, String username_field, String pass_hash_field) {
        super(next);
        
        this.host = host;
        this.database = database;
        this.table = table;
        this.usernameField = username_field;
        this.passHashField = pass_hash_field;
        this.username = username;
        this.password = password;
        this.hashAlgorithm = hash_algorithm;
    }


    /**
     * Methode die bei jedem Verbindungsaufbau eines Clients aufgerufen wird
     */
    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {

        SecurityContext s = context.getSecurityContext();
        if (s == null) {
            
        	/* Für den Abgleich mit Datensätzen in der Datenbank, muss der Benutzername und das Passwort gegeben sein */
            if (info.getUserName() != null && info.getPassword() != null) {
            	
            	
            	/* Bildung des Haswerts des gegebenen Passworts,
            	 * da das Passwort in der Datenbank ebenfalls nur als Hashwert geführt wird
            	 * */
            	String passHash = "";
    			
    			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
    			md.update(info.getPassword().getBytes("UTF-8"));
    			byte[] mdbytes = md.digest();
    			
    			StringBuffer sb = new StringBuffer();
    	        for (int i = 0; i < mdbytes.length; i++) {
    	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    	        }
    	        
    	        passHash = sb.toString();
    			
            	
        		try {
        			/* Laden des JDBC-Treibers */
        			Class.forName("com.mysql.jdbc.Driver");
        		}
        		catch (ClassNotFoundException e) {
        			throw new Exception("JDBC-Driver für MySQL wurde nicht gefunden!");
        		}

        		try {
        			/* Verbindungsaufbau mit der Datenbank mit den aus der Konfiguartionsdatei gelesenen Anmeldeinformationen */
        			con = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.database, this.username, this.password);
        			
        			/* Erstellung des Statements, mit dem überprüft wird, ob es einen Datensatz mit der Benutzername-Passworthash-Komination
        			 *	in der entsprechenden Benutzertabelle existiert
        			 */
        			stmt = con.createStatement();
        			String stmt_str = "SELECT COUNT(*) FROM " + this.table + " ";
        				stmt_str += " WHERE " + usernameField + " = '" + info.getUserName() + "' ";
        				stmt_str += 	" AND " + passHashField + " = '" + passHash + "'";
        			rs = stmt.executeQuery(stmt_str);
        		}
        		catch(SQLException e) {
        			throw new Exception("Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden!");
        		}
            	
        		rs.next();
        		
        		/* Wenn nur ein Datensatz gefunden wird, dann soll die Anmeldung erfolgreich zuende geführt werden */
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
