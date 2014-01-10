package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;


public class UserDBAuth {

	private static Connection _connection = null;
	
	private UserDBAuth() {};
	
	/**
	 * In der Datenbank überprüfen, ob der Benutzer mit den Anmeldedaten existiert
	 * 
	 * @param creds Benutzername und Passwort
	 * @return Benutzer existiert oder er existiert nicht
	 * @throws Exception Es konnte keine Verbindung zur DB aufgebaut werden
	 */
	public static boolean authUser(HashMap<String, String> creds) throws Exception {
		
		if(creds == null || creds.size() != 2) // "user"- and "pass"-key
			return false;
		
		Config cfg = Config.getInstance();
		
		String dbTable		= cfg.mysql_db.table;
		String dbUserField	= cfg.mysql_db.userfield;
		String dbPassField	= cfg.mysql_db.passfield;
		String dbPassAlgo	= cfg.mysql_db.passalgo;
		
		/* Den Hashwert des Passworts erzeugen; es soll zudem der Standard Hash-Algorithmus verwendet werden (SHA-256) */
		String dbPassHash = DigestHelper.getHash(creds.get("pass"), dbPassAlgo);
		
		/* Wenn der Hash-Wert nicht erzeugt werden konnte, dann soll die Authentifizierung abgebrochen werden */
		if(dbPassHash == null)
			return false;
		
		Connection con = getDBConnection();
		
		String stmtStr = "SELECT COUNT(*) FROM " + dbTable + " WHERE " + dbUserField + " = ? AND " + dbPassField + " = ?";
		PreparedStatement stmt = con.prepareStatement(stmtStr);
		stmt.setString(1, creds.get("user"));
		stmt.setString(2, dbPassHash);
		ResultSet rs = stmt.executeQuery();
	
		if(rs != null) {
			rs.next();
			
	    	if(rs.getInt(1) == 1) {
	    		return true;
	    	}
		}
		
		return false;
	}
	
	/**
	 * Überprüfen, ob der Username zur ID gehört
	 * 
	 * @param username Benutzername
	 * @param userId ID
	 * @return Status darüber, ob sie in Betiehung stehen oder nicht
	 * @throws Exception Es konnte keine Verbindung zur DB aufgebaut werden
	 */
	public static boolean usernameBelongsToId(String username, Integer userId) throws Exception {
		
		Config cfg = Config.getInstance();
		
		String dbTable		= cfg.mysql_db.table;
		String dbUserField	= cfg.mysql_db.userfield;
		
		
		Connection con = getDBConnection();
		
		
		String stmtStr = "SELECT COUNT(*) FROM " + dbTable + " WHERE user_id = ? AND " + dbUserField + " = ?";
		PreparedStatement stmt = con.prepareStatement(stmtStr);
		stmt.setInt(1, userId);
		stmt.setString(2, username);
		ResultSet rs = stmt.executeQuery();
	
		if(rs != null) {
			rs.next();
			
	    	if(rs.getInt(1) == 1) {
	    		return true;
	    	}
		}
		
		return false;
	}
	
	
	/**
	 * Überprüfen, ob der Benutzer mit der ID von einem bestimmten Typ ist
	 * 
	 * @param userId Benutzer-ID
	 * @param userType User-Typ als String (GUARDIAN, TEACHER, STUDENT)
	 * @return Benutzer mit der ID ist von dem Typ (true) oder nicht (false)
	 * @throws Exception SQL-Statement konnte nicht ausgeführt werden
	 */
	public static boolean idIsOfUserType(Integer userId, String userType) throws Exception {
		
		Config cfg = Config.getInstance();
		
		String dbTable = cfg.mysql_db.table;
		
		Connection con = getDBConnection();
		
		
		String stmtStr = "SELECT COUNT(user_id) FROM " + dbTable + " WHERE user_id = ? AND type = ?";
		PreparedStatement stmt = con.prepareStatement(stmtStr);
		stmt.setInt(1, userId);
		stmt.setString(2, userType);
		ResultSet rs = stmt.executeQuery();
	
		if(rs != null) {
			rs.next();
			
	    	if(rs.getInt(1) == 1) {
	    		return true;
	    	}
		}
		
		return false;
	}
	
	
	/**
	 * Überprüfen, ob zwei Datensätze mit den jeweiligen ID/Type-Kombinationen existieren
	 * 
	 * @param userId ID des ersten Benutzers
	 * @param userType User-Type des ersten Benutzers
	 * @param userId2 ID des zweiten Benutzers
	 * @param userType2 User-Type des zweiten Benutzers
	 * @return Zwei Datensätze mit den Kombinationen existieren (true) / existieren nicht (false)
	 * @throws Exception
	 */
	public static boolean comPartnersAreOfType(Integer userId, String userType, Integer userId2, String userType2) throws Exception {
		
		Config cfg = Config.getInstance();
		
		String dbTable = cfg.mysql_db.table;
		
		Connection con = getDBConnection();
		
		
		String stmtStr = "SELECT COUNT(user_id) FROM " + dbTable + " WHERE (user_id = ? AND type = ?) OR (user_id = ? AND type = ?)";
		PreparedStatement stmt = con.prepareStatement(stmtStr);
		stmt.setInt(1, userId);
		stmt.setString(2, userType);
		stmt.setInt(3, userId2);
		stmt.setString(4, userType2);
		ResultSet rs = stmt.executeQuery();
	
		if(rs != null) {
			rs.next();
			
			/* Wenn zwei Datensätze von zwei unterschiedlichen Benutzern gefunden wurden */
	    	if(rs.getInt(1) == 2) {
	    		return true;
	    	}
		}
		
		return false;
	}
	
	
	
	/**
	 * Singeleton-Funktion zur Herstellung einer Verbdindung zur DB
	 * 
	 * @return Den Verbindungshandler
	 * @throws Exception Verbindung konnte nicht hergestellt werden
	 */
	private static Connection getDBConnection() throws Exception {
		
		if(_connection == null) {
		
			try {
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch (ClassNotFoundException e) {
				throw new Exception("JDBC-Driver für MySQL wurde nicht gefunden!");
			}
			
			Config cfg = Config.getInstance();
			
			String dbHost		= cfg.mysql_db.host;
			String dbName		= cfg.mysql_db.name;
			String dbUser		= cfg.mysql_db.user;
			String dbPass		= cfg.mysql_db.pass;
			
			try {
				if(_connection == null)
					_connection = DriverManager.getConnection(dbHost + "/" + dbName, dbUser, dbPass);
			}
			catch(SQLException e) {
				throw new Exception("Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden: " + e.getMessage());
			}
		}
		
		return _connection;
		
	}
	
}
