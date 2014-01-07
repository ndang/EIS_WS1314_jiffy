package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.DigestHelper;

public class DBLayer implements IDataLayer {

	private static DBLayer instance;
	public static Connection con = null;
	
	
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
	public User getUser(Integer userId) {
		
		User user = null;
		
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
			user = new User(new Id(id, uri), name, username, userType, gender);
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return user;
	}
	
	
	@Override
	public Topics getUserTopics(Integer userId) {

		Topics topics = new Topics();
		
		ArrayList<String> topicsList = new ArrayList<String>();
		
		/* Topic mit seiner eigenen ID */
		topicsList.add("official/"+userId);
		topicsList.add("private/"+userId);
		
		
		/* Je nachdem was für ein User-Typ (Guardian, Teacher) der Benutzer ist, müssen andere Topics vergeben werden */
		try {
			
			String stmtStr = "SELECT user_id, type FROM User WHERE user_id = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
		
			rs.next();
		
			String userType = rs.getString(2);
			
			switch(userType.toLowerCase()) {
				case "guardian":
					
					/* Der Erziehungsberechtigte muss ebenfalls die Topics seiner Kinder abonnieren wie auch das Chat-Topic (sofern Kontaktperson)*/
					
					String stmtGuardianStr = "SELECT language, is_contactperson FROM Guardian WHERE user_id = ?";
					
					PreparedStatement stmtGuardian = con.prepareStatement(stmtGuardianStr);
					stmtGuardian.setInt(1, userId);
					ResultSet rsGuardian = stmtGuardian.executeQuery();
				
					if(rsGuardian.next()) {
						String language			= rsGuardian.getString(1);
						Boolean isContactperson	= rsGuardian.getBoolean(2);
						
						/* Wenn der Benutzer eine Kontaktperson ist, abonniert er auch das Chat-Topic für seine Sprache */
						if(isContactperson.booleanValue() && language != null) {
							topicsList.add("chat/" + language);
						}
						
						/* Die Topics seiner Kinder und das ihrer aktuellen Klasse (nach Jahr) abonnieren, wenn Kinder existieren */
						
						String stmtChildrenClassStr = "SELECT s.user_id AS student_id, svc.class_id " +
								"FROM Student AS s " + 
									"LEFT OUTER JOIN Student_visits_Class AS svc ON s.user_id = svc.student_user_id " + 
								"WHERE (svc.class_id = " +
											"(SELECT svc2.class_id " +
												"FROM Student_visits_Class AS svc2 " + 
												"LEFT JOIN Class AS c ON svc2.class_id = c.class_id " + 
												"WHERE svc2.student_user_id = s.user_id ORDER BY c.year ASC LIMIT 1) " +
										"OR svc.class_id IS NULL) " + 
										"AND s.guardian_user_id = ?";
			
						PreparedStatement stmtChildrenClass = con.prepareStatement(stmtChildrenClassStr);
						stmtChildrenClass.setInt(1, userId);
						ResultSet rsChildrenClass = stmtChildrenClass.executeQuery();
						
						/* Alle Datensätze der eigenen Kinder durchgehen und die Topic-Namen zusammensetzen */
						while(rsChildrenClass.next()) {
							
							/* Topic für das Kind; es besitzt kein privates Topic */
							Integer studentId = rsChildrenClass.getInt(1);
							topicsList.add("official/" + studentId);
							
							
							Integer classId	= rsChildrenClass.getInt(2);
							
							/* Topics für die Klasse, die das Kind als letztes besucht (hat) */
							if(!rsChildrenClass.wasNull()) {
								topicsList.add("official/class_" + classId);
								topicsList.add("private/class_" + classId);
							}
								
						}
					}
					
					break;
				case "teacher":
					
					/* Die Lehrkraft bisher kein weiteres Topic; man kann es aber an dieser Stelle erweitern */
					
					break;
			}
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		topics.setTopics(topicsList);
		
		return topics;
	}
	
	
	
	@Override
	public Users getUsers() {
		
		Users users = null;
		
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
			
			users = new Users(userList);
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return users;
	}
	
	
	@Override
	public Student getStudent(Integer userId) {
		Student student = null;
		
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
			student = new Student(new Id(id, uri), name, username, userType, gender, guardianRef, "/student/" + userId + "/grades", null);
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return student;
	}
	
	
	@Override
	public Integer postStudent(Student student, String givenPass) {
		
		if(student == null)
			return null;
		
		Integer newUserId = null;
		
		try {
			
			con.setAutoCommit(false);

			String name = student.getName();
			String username = student.getUsername();
			String type = student.getUserType();
			String gender = student.getGender();
			
			Integer userId = putUser(name, username, givenPass, type, gender);
			
			if(userId != null) {
				// TODO: guardian_user_id setzen!
				
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
