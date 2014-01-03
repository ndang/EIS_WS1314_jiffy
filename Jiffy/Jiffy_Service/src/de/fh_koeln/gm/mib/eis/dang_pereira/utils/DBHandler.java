package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;

public class DBHandler implements IDataHandler {

	private static DBHandler instance;
	public static Connection con = null;
	
	
	/* Die Klasse sollte nur über die Klassenfunktion "getInstance" instanziiert werden */
	private DBHandler() {
		
		Config cfg = Config.getInstance();

		
		String db_host = cfg.mysql_db.host;
		String db_name = cfg.mysql_db.name;
		String db_user = cfg.mysql_db.user;
		String db_pass = cfg.mysql_db.pass;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			System.err.println("Klasse wurde nicht gefunden: " + "com.mysql.jdbc.Driver");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			con = DriverManager.getConnection(db_host+"/"+db_name, db_user, db_pass);
		}
		catch(SQLException e) {
			System.err.println("Konnte die Verbindung zur Datenbank nicht herstellen: jdbc:mysql://"+db_host+"/"+db_name);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	
    /**
     * Instanz der DBHandler-Klasse holen (Singleton)
     * 
     * @return Instanz der DBHandler-Klasse
     */
	public static DBHandler getInstance() {
		if(instance == null) {
			instance = new DBHandler();
		}
		
		return instance;
	}
	
	
	@Override
	public ArrayList<String> getPersonsExample() {

		ArrayList<String> arr_list = new ArrayList<String>();
		
		
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT person_id, name, benutzername, pass_hash FROM Person");
			
			while(rs.next()) {
				
				String id = rs.getString(1);
				String name = rs.getString(2);
				String username = rs.getString(3);
				String passhash = rs.getString(4);
				
				arr_list.add(id + " | " + name + " | " + username + " | " + passhash);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return arr_list;
	}
	
	
	
	public void finalize() {
		/* Die Datenbankverbindung sollte geschlossen werden, wenn der Garbage-Collector aktiv wird */
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("Konnte die Verbindung zur DB nicht schließen!");
			e.printStackTrace();
		}
	}
}
