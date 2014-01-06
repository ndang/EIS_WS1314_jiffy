package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.DigestHelper;

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

			Integer id = rs.getInt(1);
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
	
	
	@Override
	public String getStudent(Integer userId) {
		String data = null;
		
		try {
			
			// TODO: latest-class
			String stmtStr = "SELECT u.user_id, u.name, u.username, u.type, u.gender, s.guardian_user_id ";
				stmtStr += "FROM User AS u LEFT JOIN Student s On u.user_id = s.user_id ";
				stmtStr += "WHERE u.user_id = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
		
			rs.next();

			Integer id = rs.getInt(1);
			String uri = "/user/" + id;
			String name = rs.getString(2);
			String username = rs.getString(3);
			String userType = rs.getString(4);
			String gender = rs.getString(5);
			Integer guardianId = rs.getInt(6);
			
			Id guardianRef = null;

			if(guardianId > 0) {
				guardianRef = new Id(guardianId, "/user/" + guardianId);
			}
			
			/* User-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
			Student student = new Student(new Id(id, uri), name, username, userType, gender, guardianRef, "/student/" + userId + "/grades", null);
			
			data = jmapper.writeValueAsString(student);
		
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
	public Integer postStudent(String data, String givenPass) {
		
		Integer newUserId = null;
		
		try {
			
			con.setAutoCommit(false);

			Student student = jmapper.readValue(data, Student.class);
			
			String name = student.getName();
			String username = student.getUsername();
			String type = student.getUserType();
			String gender = student.getGender();
			
			Integer userId = putUser(name, username, givenPass, type, gender);
			
			if(userId != null) {
			
				/* Einen Datensatz mit der selben ID in die Student-Tabelle einfügen */
				String stmtStr = "INSERT INTO Student (user_id, guardian_user_id) VALUES (?, NULL)";
				PreparedStatement stmt = con.prepareStatement(stmtStr);
				stmt.setInt(1, userId);
				
				if(stmt.executeUpdate() > 0) {
					newUserId = userId;
					con.commit();
				}
				else {
					con.rollback();
				}
			}
			else {
				con.rollback();
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Konnte JSON-Dokument nicht parsen: " + e.getMessage());
			e.printStackTrace();
		}
		
		return newUserId;
	}
	
	
	private Integer putUser(String name, String username, String password, String type, String gender) {
		
		Integer userId = null;
		
		if(password == null) {
			/* Wenn kein Password gegeben ist, dann nehme den Usernamen; das Passwort sollte aber immer gegeben werden */
			password = username;
		}
		
		/* Den Hash berechnen lassen */
		String pass_hash = DigestHelper.getHash(password, null);
		
		try {
			
			String stmtStr = "INSERT INTO User (name, username, pass_hash, type, gender) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(stmtStr, new String[]{"user_id"});
			stmt.setString(1, name);
			stmt.setString(2, username);
			stmt.setString(3, pass_hash);
			stmt.setString(4, type);
			stmt.setString(5, gender);
			
			if(stmt.executeUpdate() > 0) {
				ResultSet gk = stmt.getGeneratedKeys();
				
				if(gk != null && gk.next()) {
					userId = gk.getInt(1);
				}
				
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return userId;
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
