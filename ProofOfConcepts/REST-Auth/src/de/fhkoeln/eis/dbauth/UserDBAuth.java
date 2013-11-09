package de.fhkoeln.eis.dbauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import de.fhkoeln.eis.Config;


public class UserDBAuth {

	public static Connection con = null;
	public static Statement stmt = null;
	public static ResultSet rs = null;
	
	private UserDBAuth() {};

	public static boolean authUser(HashMap<String, String> creds) throws Exception {
		
		if(creds.size() != 2) // "user"- and "pass"-key
			return false;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			throw new Exception("JDBC-Driver für MySQL wurde nicht gefunden!");
		}

		try {
			if(con == null)
				con = DriverManager.getConnection("jdbc:mysql://" + Config.DB.host + "/" + Config.DB.name, Config.DB.user, Config.DB.pass);
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(user_id) FROM " + Config.DB.table + " WHERE name = '" + creds.get("user") + "' AND password = '" + creds.get("pass") + "'");
		}
		catch(SQLException e) {
			throw new Exception("Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden!");
		}
    	
		rs.next();
		
    	if(rs.getString(1).equals("1")) {
    		return true;
    	}
		
		return false;
	}
	
}
