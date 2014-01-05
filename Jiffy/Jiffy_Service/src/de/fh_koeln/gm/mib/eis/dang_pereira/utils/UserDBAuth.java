package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;


public class UserDBAuth {

	public static Connection con = null;
	public static Statement stmt = null;
	public static ResultSet rs = null;
	
	private UserDBAuth() {};

	public static boolean authUser(HashMap<String, String> creds) throws Exception {
		
		if(creds == null || creds.size() != 2) // "user"- and "pass"-key
			return false;
		
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
		String dbTable		= cfg.mysql_db.table;
		String dbUserField	= cfg.mysql_db.userfield;
		String dbPassField	= cfg.mysql_db.passfield;
		String dbPassAlgo	= cfg.mysql_db.passalgo;
		
		/* Den Hashwert des Passworts erzeugen; es soll zudem der Standard Hash-Algorithmus verwendet werden (SHA-256) */
		String dbPassHash = DigestHelper.getHash(creds.get("pass"), dbPassAlgo);
		
		/* Wenn der Hash-Wert nicht erzeugt werden konnte, dann soll die Authentifizierung abgebrochen werden */
		if(dbPassHash == null)
			return false;
		
		try {
			if(con == null)
				con = DriverManager.getConnection(dbHost + "/" + dbName, dbUser, dbPass);
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM " + dbTable + " WHERE " + dbUserField + " = '" + creds.get("user") + "' AND " + dbPassField + " = '" + dbPassHash + "'");
		}
		catch(SQLException e) {
			throw new Exception("Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden: " + e.getMessage());
		}
    	
		if(rs != null) {
			rs.next();
			
	    	if(rs.getString(1).equals("1")) {
	    		return true;
	    	}
		}
		
		return false;
	}
	
}
