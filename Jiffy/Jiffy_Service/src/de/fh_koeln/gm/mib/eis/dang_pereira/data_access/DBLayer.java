package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;

public class DBLayer implements IDataLayer {

	private static DBLayer instance;
	public static Connection con = null;
	public static ObjectMapper jmapper = new ObjectMapper();
	
	
	/* Die Klasse sollte nur über die Klassenfunktion "getInstance" instanziiert werden */
	private DBLayer() {
		
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
	public static DBLayer getInstance() {
		if(instance == null) {
			instance = new DBLayer();
		}
		
		return instance;
	}
	
	
	
	@Override
	public String getUser(Integer userId) {
		
		String data = null;
		
		try {
			
			String stmtStr = "SELECT user_id, name, username, type, gender FROM User WHERE user_id = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
		
			rs.next();

			Integer id = Integer.valueOf(rs.getString(1));
			String uri = "/user/" + id;
			String name = rs.getString(2);
			String username = rs.getString(3);
			String userType = rs.getString(4);
			String gender = rs.getString(5);
			
			/* User-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
			User user = new User(new Id(id, uri), name, username, userType, gender);
			
			data = jmapper.writeValueAsString(user);
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			System.err.println("JSON-Dokument konnte nicht erzeugt werden: " + e.getMessage());
			e.printStackTrace();
		}
		
		return data;
	}
	
	
	
	@Override
	public String getUsers() {
		
		String data = null;
		
		try {
			
			String stmtStr = "SELECT user_id, name, username, type, gender FROM User";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			
			ResultSet rs = stmt.executeQuery();
			
			ArrayList<User> userList = new ArrayList<User>();
			
			while(rs.next()) {

				Integer id = Integer.valueOf(rs.getString(1));
				String uri = "/user/" + id;
				String name = rs.getString(2);
				String username = rs.getString(3);
				String user_type = rs.getString(4);
				String gender = rs.getString(5);
				
				/* User-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
				User user = new User(new Id(id, uri), name, username, user_type, gender);
				
				/* Das soeben erzeugte User-Objekt der Liste hinzufügen */
				userList.add(user);
			}
			
			Users users = new Users(userList);
			
			data = jmapper.writeValueAsString(users);
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			System.err.println("JSON-Dokument konnte nicht erzeugt werden: " + e.getMessage());
			e.printStackTrace();
		}
		
		return data;
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
