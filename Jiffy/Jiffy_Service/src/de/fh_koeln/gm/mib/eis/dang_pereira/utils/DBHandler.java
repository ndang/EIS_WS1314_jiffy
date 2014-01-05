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
