package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Destination;
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
			Destination dest = new Destination("private/" + id, "official/" + id);
			user = new User(new Id(id, uri, dest), name, username, userType, gender);
			
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
							
							/* Topic für das Kind */
							Integer studentId = rsChildrenClass.getInt(1);
							topicsList.add("official/" + studentId);
							topicsList.add("private/" + studentId);
							
							
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
				Destination dest = new Destination("private/" + id, "official/" + id);
				User user = new User(new Id(id, uri, dest), name, username, user_type, gender);
				
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
				Destination dest = new Destination("private/" + guardianId, "official/" + guardianId);
				guardianRef = new Id(guardianId, "/user/" + guardianId, dest);
			}
			
			/* User-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
			Destination dest = new Destination("private/" + id, "official/" + id);
			student = new Student(new Id(id, uri, dest), name, username, userType, gender, guardianRef, "/student/" + userId + "/grades", null);
			
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
				} else {
					con.rollback();
				}
			} else {
				con.rollback();
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(newUserId == null) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		return newUserId;
	}
	
	
	
	@Override
	public boolean placeSchoolGradeMsg(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		try {
			
			con.setAutoCommit(false);
			
			String subject = msg.getMsgSubject();
			String msgText = msg.getMsgText();
			String sendDate = msg.getMsgSendDate();
			String type = "GRADEMSG";
			
			Integer msgId = placeSchoolMsg(subject, msgText, sendDate, type);
			
			if(msgId != null) {
				
				Integer gradeId = msg.getSchool().getGrade().getID();
				
				/* Einen Datensatz mit der selben ID in die Student-Tabelle einfügen */
				String gradeStmtStr = "INSERT INTO Grademessage (school_message_id, approved, grade_id) " + 
										"VALUES (?, 0, ?)";
				PreparedStatement gradeStmt = con.prepareStatement(gradeStmtStr);
				gradeStmt.setInt(1, msgId);
				gradeStmt.setInt(2, gradeId);
				
				if(gradeStmt.executeUpdate() > 0) {
					
					Integer fromTeacherId = msg.getFromUserId().getID();
					Integer toGuardianId = msg.getToUserId().getID();
					
					/* Zuordnung der Nachricht zu der Kommunikationspartner-Kombination */
					String relationStmtStr = "INSERT INTO Teacher_to_Guardian (school_message_id, from_teacher_user_id, to_guardian_user_id) " +
												"VALUES (?, ?, ?)";
					PreparedStatement relationStmt = con.prepareStatement(relationStmtStr);
					relationStmt.setInt(1, msgId);
					relationStmt.setInt(2, fromTeacherId);
					relationStmt.setInt(3, toGuardianId);
					
					if(relationStmt.executeUpdate() > 0) {
						/* Die Nachricht wurde erfolgreich hinzugefügt! */
						con.commit();
						status = true;
					}
				} else {
					con.rollback();
				}
			} else {
				con.rollback();
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		return status;
	}
	
	
	@Override
	public boolean placeSchoolInfoMsg(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		try {
			
			con.setAutoCommit(false);
			
			String subject = msg.getMsgSubject();
			String msgText = msg.getMsgText();
			String sendDate = msg.getMsgSendDate();
			String type = "INFOMSG";
			
			Integer msgId = placeSchoolMsg(subject, msgText, sendDate, type);
			
			if(msgId != null) {
				
				Boolean classBroadcast	= msg.getSchool().getInfo().getClassBroadcast();
				String descDate			= msg.getSchool().getInfo().getDescDate();
				
				/* Einen Datensatz mit der selben ID in die Student-Tabelle einfügen */
				String gradeStmtStr = "INSERT INTO Informationnote (school_message_id, description_date) " + 
										"VALUES (?, ?)";
				PreparedStatement gradeStmt = con.prepareStatement(gradeStmtStr);
				gradeStmt.setInt(1, msgId);
				gradeStmt.setString(2, descDate);
				
				if(gradeStmt.executeUpdate() > 0) {
					
					/* Überprüfen, ob es sich um ein Klassenbroadcast handelt, oder einer Nachricht an einen gezielten Guardian */
					if(!classBroadcast.booleanValue()) {
						
						/* Gezielten Guardian */
						Integer fromTeacherId = null;
						if(msg.getFromUserId() != null){
							fromTeacherId = msg.getFromUserId().getID();
						}
						
						Integer toGuardianId = null;
						if(msg.getToUserId() != null){
							toGuardianId = msg.getToUserId().getID();
						} else {
							System.err.println("Kein Zielbenutzer angegeben!");
							con.rollback();
							return false;
						}
						
						/* Zuordnung der Nachricht zu der Kommunikationspartner-Kombination */
						String relationStmtStr = "INSERT INTO Teacher_to_Guardian (school_message_id, from_teacher_user_id, to_guardian_user_id) " +
													"VALUES (?, ?, ?)";
						PreparedStatement relationStmt = con.prepareStatement(relationStmtStr);
						relationStmt.setInt(1, msgId);
						relationStmt.setInt(2, fromTeacherId);
						relationStmt.setInt(3, toGuardianId);
						
						if(relationStmt.executeUpdate() > 0) {
							
							/* Die Nachricht wurde erfolgreich hinzugefügt! */
							con.commit();
							status = true;
						}
					} else {
						
						/* Klassenbroadcast; einfachheitshalber kein Datensatz in der Beziehungstabelle; evtl. aber später mal nötig */
						con.commit();
						status = true;
					}
				} else {
					con.rollback();
				}
			} else {
				con.rollback();
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		return status;
	}
	
	
	@Override
	public boolean placeGuardianMsg(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		try {
			
			con.setAutoCommit(false);
			
			String subject			= msg.getMsgSubject();
			String msgText			= msg.getMsgText();
			String sendDate			= msg.getMsgSendDate();
			String type				= null;
			Integer toTeacherId		= msg.getToUserId().getID();
			Integer fromGuardianId	= msg.getFromUserId().getID();
			
			
			Integer msgId = placeGuardianMsg(subject, msgText, sendDate, type, toTeacherId, fromGuardianId);
			
			if(msgId != null) {
				
				/* Normale Erziehungsberechtigtennachricht wurde erfolgreich eingefügt */
				con.commit();
				status = true;
					
			} else {
				con.rollback();
			}
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		return status;
		
	}
	
	
	
	@Override
	public boolean placeGuardianExcuseMsg(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		try {
			
			con.setAutoCommit(false);
			
			String subject			= msg.getMsgSubject();
			String msgText			= msg.getMsgText();
			String sendDate			= msg.getMsgSendDate();
			String type				= "EXCUSEMSG";
			Integer toTeacherId		= msg.getToUserId().getID();
			Integer fromGuardianId	= msg.getFromUserId().getID();
			
			
			Integer msgId = placeGuardianMsg(subject, msgText, sendDate, type, toTeacherId, fromGuardianId);
			
			if(msgId != null) {
				
				String dateFrom	= msg.getGuardian().getExcuse().getDateFrom();
				String dateTo	= msg.getGuardian().getExcuse().getDateTo();
				
				/* Zuordnung der Nachricht zu der Kommunikationspartner-Kombination */
				String relationStmtStr = "INSERT INTO Excusemessage (guardian_message_id, date_from, date_to, approved) " +
											"VALUES (?, ?, ?, 0)";
				PreparedStatement relationStmt = con.prepareStatement(relationStmtStr);
				relationStmt.setInt(1, msgId);
				relationStmt.setString(2, dateFrom);
				relationStmt.setString(3, dateTo);
				
				if(relationStmt.executeUpdate() > 0) {
					
					/* Die Entschuldigungsnachricht wurde erfolgreich hinzugefügt! */
					con.commit();
					status = true;
				}
					
			} else {
				con.rollback();
			}
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		return status;
		
	}
	
	
	
	@Override
	public boolean approveStudentGrade(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		
		try {
			
			con.setAutoCommit(false);
			
			Integer toTeacherId		= msg.getToUserId().getID();
			Integer fromGuardianId	= msg.getFromUserId().getID();
			Integer gradeId			= msg.getGuardian().getGradeAck().getID();
			
			/* Benotungsnachricht zur Kenntnis setzen (approved = true); mit zusätzlicher Überprüfung bestehender TEACHER zu GUARDIAN-Beziehung und Grade-ID */
			String stmtStr = "UPDATE (Grademessage AS gm LEFT JOIN Schoolmessage AS sm ON gm.school_message_id = sm.school_message_id) " + 
											"LEFT JOIN Teacher_to_Guardian AS ttg ON ttg.school_message_id = gm.school_message_id " +
										"SET gm.approved = 1 " +
										"WHERE ttg.from_teacher_user_id = ? AND ttg.to_guardian_user_id = ? AND gm.grade_id = ?";
			
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, toTeacherId);
			stmt.setInt(2, fromGuardianId);
			stmt.setInt(3, gradeId);
			
			if(stmt.executeUpdate() > 0) {
				
				/* Benotungsnachricht wurde zur Kenntnis genommen! */
				con.commit();
				status = true;
			}
					
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		
		return status;
	}
	
	
	@Override
	public boolean approveExcuseMsg(Message msg) {
		
		if(msg == null)
			return false;
		
		boolean status = false;
		
		
		try {
			
			con.setAutoCommit(false);
			
			Integer fromTeacherId	= msg.getFromUserId().getID();
			Integer toGuardianId	= msg.getToUserId().getID();
			String dateFrom			= msg.getSchool().getExcuseAck().getDateFrom();
			String dateTo			= msg.getSchool().getExcuseAck().getDateTo();
			
			/* Akzeptieren einer Entschuldigung (approved = true); nur wenn die gegebenen Zeiten übereinstimmen */
			String stmtStr = "UPDATE Excusemessage AS em LEFT JOIN Guardianmessage AS gm ON em.guardian_message_id = gm.guardian_message_id " +
							"SET em.approved = 1 " +
							"WHERE gm.to_teacher_user_id = ? AND gm.from_guardian_user_id = ? AND em.date_from = ? AND em.date_to = ?";
		
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, fromTeacherId);
			stmt.setInt(2, toGuardianId);
			stmt.setString(3, dateFrom);
			stmt.setString(4, dateTo);
				
			
			if(stmt.executeUpdate() > 0) {
				
				/* Entschuldigung wurde zur Kenntnis genommen und akzeptiert! */
				con.commit();
				status = true;
			}
					
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		if(!status) {
			try {
				con.rollback();
			} catch (SQLException e) {
				System.err.println("Konnte Transaktion nicht zurückrollen!" + e.getMessage());
			}
		}
		
		
		return status;
	}
	
	
	
	/*  ##############  helper  ##############  */
	
	
	
	/**
	 * User-Datensatz einfügen (in Obertabelle)
	 * 
	 * @param name Name des Users
	 * @param username Benutezrname des Users
	 * @param password Password des Users
	 * @param type User-Type des Benutzers
	 * @param gender Geschlecht des Users
	 * @return Dem Benutzer vergebene ID
	 */
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
	
	
	/**
	 * Schulnachrichtdatensatz einfügen (in Obertabelle)
	 * 
	 * @param subject Betreff
	 * @param msgText Nachrichtentext
	 * @param sendDate ABsendedatum
	 * @param type Nachrichtentyp
	 * @return Id, die dem neuen Nachrichtendatensatz vergeben wurde
	 */
	private Integer placeSchoolMsg(String subject, String msgText, String sendDate, String type) {
		
		Integer msgId = null;
		
		try {
			
			String stmtStr = "INSERT INTO Schoolmessage (subject, message, send_date, type) VALUES (?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(stmtStr, new String[]{"school_message_id"});
			stmt.setString(1, subject);
			stmt.setString(2, msgText);
			stmt.setString(3, sendDate);
			stmt.setString(4, type);
			
			if(stmt.executeUpdate() > 0) {
				ResultSet gk = stmt.getGeneratedKeys();
				
				if(gk != null && gk.next()) {
					msgId = gk.getInt(1);
				}
				
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return msgId;
	}
	
	
	/**
	 * Erzeihungsberechtigtennachrichtdatensatz einfügen (in Obertabelle)
	 * 
	 * @param subject Betreff
	 * @param msgText Nachrichtentext
	 * @param sendDate Absendedatum
	 * @param type Nachrichtentyp
	 * @param to_t_user_id Id des Lehrers (Empfänger)
	 * @param from_g_user_id Id des Erziehungsberechtigten (Absender)
	 * @return Id, die dem neuen Nachrichtendatensatz vergeben wurde
	 */
	private Integer placeGuardianMsg(String subject, String msgText, String sendDate, String type, Integer toTUserId, Integer fromGUserId) {
		
		Integer msgId = null;
		
		try {
			
			String stmtStr = "INSERT INTO Guardianmessage (subject, message, send_date, type, to_teacher_user_id, from_guardian_user_id) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(stmtStr, new String[]{"guardian_message_id"});
			stmt.setString(1, subject);
			stmt.setString(2, msgText);
			stmt.setString(3, sendDate);
			stmt.setString(4, type);
			stmt.setInt(5, toTUserId);
			stmt.setInt(6, fromGUserId);
			
			if(stmt.executeUpdate() > 0) {
				ResultSet gk = stmt.getGeneratedKeys();
				
				if(gk != null && gk.next()) {
					msgId = gk.getInt(1);
				}
				
			}
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return msgId;
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
