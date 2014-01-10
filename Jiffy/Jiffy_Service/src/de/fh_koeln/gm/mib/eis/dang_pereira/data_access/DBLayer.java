package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Destination;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grade;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grades;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Subject;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Teacher;
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
		
		if(userId == null)
			return null;
		
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

		if(userId == null)
			return null;
		
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
		
		if(userId == null)
			return null;
		
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

			String name		= student.getName();
			String username	= student.getUsername();
			String gender	= student.getGender();
			Id guardian		= student.getGuardian(); 
			
			Integer userId = postUser(name, username, givenPass, "STUDENT", gender);
			
			if(userId != null) {
				// TODO: guardian_user_id setzen!

				String stmtStr = "";
				PreparedStatement stmt = null;
				
				/* Einen Datensatz mit der selben ID in die Student-Tabelle einfügen */
				stmtStr = "INSERT INTO Student (user_id, guardian_user_id) VALUES (?, ?)";
				stmt = con.prepareStatement(stmtStr);
				stmt.setInt(1, userId);
				
				/* Je nachdem ob die Id der Erziehungsberechtigten mitgegeben wurde, wird sie mit eingefügt oder nicht */
				if(guardian != null && guardian.getID() != null) {
					Integer guardianId = guardian.getID();
					stmt.setInt(2, guardianId);
				}
				else {
					stmt.setNull(2, 0);
				}
				
				
				if(stmt != null && stmt.executeUpdate() > 0) {
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
	public boolean putStudent(Integer userId, Student student, String givenPass) {
		
		if(student == null)
			return false;
		
		boolean status = false;
		
		try {
			
			con.setAutoCommit(false);
			
			String name		= student.getName();
			String username	= student.getUsername();
			String gender	= student.getGender();
			Id guardian		= student.getGuardian(); 
			
			boolean subStatus = putUser(userId, name, username, givenPass, gender);
			
			if(subStatus) {

				if(guardian != null && guardian.getID() != null) {
				
					/* Erziehungsberechtigten setzen */
					String stmtStr = "UPDATE Student SET guardian_user_id = ? WHERE user_id = ?";
					
					PreparedStatement stmt = con.prepareStatement(stmtStr);
					stmt.setInt(1, guardian.getID());
					stmt.setInt(2, userId);
					
					
					if(stmt != null && stmt.executeUpdate() > 0) {
						status = true;
						con.commit();
					}
				} else {
					status = true;
					con.commit();
				}
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
	public Integer postStudentGrade(Integer userId, Grade grade) {
		
		if(userId == null || grade == null)
			return null;
		
		Integer newGradeId = null;
		
		try {
			
			BigDecimal gradeValue	= grade.getValue();
			Integer gradeWeight		= grade.getGradeWeight();
			String comment			= grade.getComment();
			Integer subjectId		= null;
			
			/* Sicher gehen, dass man an die ID des Schulfaches kommt */
			if(grade.getSubject() != null && grade.getSubject().getSubject() != null) {
				subjectId = grade.getSubject().getSubject().getID();
			}
			
			/* Note in die DB einfügen */
			String stmtStr = "INSERT INTO Grade (grade, date_given, grade_weight, comment, schoolsubject_id, student_user_id) "+ 
								"VALUES (?, NOW() , ?, ?, ?, ?)";
			
			PreparedStatement stmt = con.prepareStatement(stmtStr, new String[]{"grade_id"});
			stmt.setBigDecimal(1, gradeValue);
			stmt.setInt(2, gradeWeight);
			stmt.setString(3, comment);
			stmt.setInt(4, subjectId);
			stmt.setInt(5, userId);
			
			/* Vergebene Id beziehen und als Rückgabewert setzen */
			if(stmt.executeUpdate() > 0) {
				ResultSet gk = stmt.getGeneratedKeys();
				
				if(gk != null && gk.next()) {
					newGradeId = gk.getInt(1);
				}
				
			}
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return newGradeId;
	}
	
	
	@Override
	public Grade getStudentGrade(Integer userId, Integer gradeId) {
		
		if(userId == null || gradeId == null)
			return null;
		
		Grade grade = null;
		
		try {
			
			/* Alle Notendetails (plus Fach und in dem unterrichtender Lehrer) beziehen */
			String stmtStr = "SELECT g.grade, g.date_given, g.grade_weight, g.comment, " +
										"ss.schoolsubject_id, ss.description, ss.teacher_user_id " + 
								"FROM Grade AS g LEFT JOIN Schoolsubject AS ss ON g.schoolsubject_id = ss.schoolsubject_id " +
								"WHERE g.student_user_id = ? AND g.grade_id = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, userId);
			stmt.setInt(2, gradeId);
			ResultSet rs = stmt.executeQuery();
		
			if(rs.next()) {

				BigDecimal value	= rs.getBigDecimal(1);
				String dateGiven	= rs.getString(2); 
				Integer gradeWeight	= rs.getInt(3);
				String comment		= rs.getString(4);
				Integer	subjectId	= rs.getInt(5);
				String description	= rs.getString(6);
				Integer teacherId	= rs.getInt(7);
				
				/* Zusammenstellen der Teil-Dokumente / -Objekte */
				Id gradeIdObj = new Id(gradeId, "/student/"+ userId + "/" +gradeId, null);
				Id subjectIdObj = new Id(subjectId, null, null);
				Id teacherIdObj = new Id(teacherId, "/teacher/"+ teacherId, new Destination("private/" + teacherId, "official/" + teacherId));
				
				Subject subject = new Subject(subjectIdObj, description, teacherIdObj);
				
				/* Grade-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
				grade = new Grade(gradeIdObj, value, dateGiven, gradeWeight, comment, subject);
			}
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return grade;
		
	}
	
	
	@Override
	public Grades getStudentGrades(Integer userId) {
		
		if(userId == null)
			return null;
		
		Grades userGrades = null;
		

		try {
			
			/* Alle Notendetails (plus Fach und in dem unterrichtender Lehrer) beziehen */
			String stmtStr = "SELECT g.grade_id, g.grade, g.date_given, g.grade_weight, g.comment, " +
										"ss.schoolsubject_id, ss.description, ss.teacher_user_id " + 
								"FROM Grade AS g LEFT JOIN Schoolsubject AS ss ON g.schoolsubject_id = ss.schoolsubject_id " +
								"WHERE g.student_user_id = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
		
			ArrayList<Grade> gradesList = new ArrayList<Grade>();
			
			while(rs.next()) {
				
				Integer gradeId		= rs.getInt(1);
				BigDecimal value	= rs.getBigDecimal(2);
				String dateGiven	= rs.getString(3); 
				Integer gradeWeight	= rs.getInt(4);
				String comment		= rs.getString(5);
				Integer	subjectId	= rs.getInt(6);
				String description	= rs.getString(7);
				Integer teacherId	= rs.getInt(8);
				
				/* Zusammenstellen der Teil-Dokumente / -Objekte */
				Id gradeIdObj = new Id(gradeId, "/student/"+ userId + "/" +gradeId, null);
				Id subjectIdObj = new Id(subjectId, null, null);
				Id teacherIdObj = new Id(teacherId, "/teacher/"+ teacherId, new Destination("private/" + teacherId, "official/" + teacherId));
				
				Subject subject = new Subject(subjectIdObj, description, teacherIdObj);
				
				/* Grade-Objekt erzeugen und es mit zuvor bezogenen Daten befüllen */
				Grade grade = new Grade(gradeIdObj, value, dateGiven, gradeWeight, comment, subject);
				
				gradesList.add(grade);
			}
			
			userGrades = new Grades();
			userGrades.setGrades(gradesList);
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return userGrades;
	}
	
	
	@Override
	public Users getGuardianChildren(Integer guardianId) {
		
		if(guardianId == null)
			return null;
		
		Users children = null;
		
		
		try {
			
			String stmtStr = "SELECT u.user_id, u.name, u.username, u.type, u.gender " +
								"FROM Student AS s LEFT OUTER JOIN User AS u ON s.user_id = u.user_id " + 
								"WHERE s.guardian_user_id = ? AND u.type = ?";
			PreparedStatement stmt = con.prepareStatement(stmtStr);
			stmt.setInt(1, guardianId);
			stmt.setString(2, "STUDENT");
			
			ResultSet rs = stmt.executeQuery();
			
			ArrayList<User> childrenList = new ArrayList<User>();
			
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
				childrenList.add(user);
			}
			
			children = new Users(childrenList);
			
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return children;
	}
	
	
	
	/* ######  messages  ###### */
	
	
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
			
			String subject			= msg.getMsgSubject(); /* Betreff */
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
	private Integer postUser(String name, String username, String password, String type, String gender) {
		
		Integer userId = null;
		
		if(password == null) {
			/* Wenn kein Password gegeben ist, dann nehme den Usernamen; das Passwort sollte aber immer gegeben werden */
			password = username;
		}
		
		/* Den Hash berechnen lassen */
		String passHash = DigestHelper.getHash(password, null);
		
		try {
			
			String stmtStr = "INSERT INTO User (name, username, pass_hash, type, gender) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(stmtStr, new String[]{"user_id"});
			stmt.setString(1, name);
			stmt.setString(2, username);
			stmt.setString(3, passHash);
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
	 * User-Datensatz aktualisieren (in Obertabelle)
	 * 
	 * @param name Name des Users
	 * @param username Benutezrname des Users
	 * @param password Password des Users
	 * @param type User-Type des Benutzers
	 * @param gender Geschlecht des Users
	 */
	private boolean putUser(Integer userId, String name, String username, String password, String gender) {
		
		if(userId == null)
			return false;
		
		boolean status = false;
		
		
		/* Den Hash berechnen lassen */
		String passHash = DigestHelper.getHash(password, null);
		
		try {
			
			/* Zuweisungs-Anteil */
			ArrayList<Object[]> fields = new ArrayList<Object[]>();
			
			if(name != null)
				fields.add(new Object[]{"name", name, "string"});
			
			if(username != null)
				fields.add(new Object[]{"username", username, "string"});
			
			if(passHash != null)
				fields.add(new Object[]{"pass_hash", passHash, "string"});
			
			if(gender != null)
			fields.add(new Object[]{"gender", gender, "string"});
			
			/* Where-Klausel-Anteil */
			ArrayList<Object[]> where = new ArrayList<Object[]>();
			
			where.add(new Object[]{"user_id", userId, "int"});
			
			/* SQL-Statement dynamisch zusammenbauen lassen */
			PreparedStatement stmt = buildDynUpdateStmt("User", fields, where);
			
			if(stmt != null && stmt.executeUpdate() > 0) {
				status = true;
			}
			
		
		} catch (SQLException e) {
			System.err.println("Fehler beim Absetzen des SQL-Statements: " + e.getMessage());
			e.printStackTrace();
		}
		
		return status;
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
	
	
	/**
	 * Ein dynamisches SQL-Insert-Statement bauen
	 * 
	 * @param table Tabellenname
	 * @param fields List von Feldern/Wert/Datentyp-Angaben
	 * @return Vorbereitetes Statement
	 */
	private PreparedStatement buildDynUpdateStmt(String table, ArrayList<Object[]> values, ArrayList<Object[]> wheres) {
		
		String stmtStr = "UPDATE " + table + " SET ";
		
		Iterator<Object[]> iter = values.iterator();
		int counter;
		for(counter = 0; iter.hasNext(); counter++) {
			if(counter == 0)
				stmtStr += (String)iter.next()[0] + " = ?";
			else
				stmtStr += ", " + (String)iter.next()[0] + " = ?";
		}
		stmtStr += " WHERE ";
		
		System.out.println(stmtStr);
		
		iter = wheres.iterator();
		for(counter = 0; iter.hasNext(); counter++) {
			if(counter == 0)
				stmtStr += (String)iter.next()[0] + " = ?";
			else
				stmtStr += "AND " + (String)iter.next()[0] + " = ?";
		}
		
		
		System.out.println(stmtStr);
		
		PreparedStatement stmt = null;
		
		try {
			stmt = con.prepareStatement(stmtStr);
		} catch (SQLException e) {
			System.err.println("Konnte Statement nicht vorbereiten: " + e.getMessage());
		}
		
		int index = 1;
		for(Object[] field: values) {
			setSQLFieldVal(stmt, field[1], index);
			index++;
		}
		
		for(Object[] where: wheres) {
			setSQLFieldVal(stmt, where[1], index);
			System.out.println(index + " - " + where[1]);
			index++;
		}
		
		
		System.out.println(stmt);
		
		return stmt;
	}
	
	
	private boolean setSQLFieldVal(PreparedStatement stmt, Object val, int index) {
		
		if(stmt == null || val == null)
			return false;
		
		boolean status = false;
		
		boolean isNull = (val == null) ? true: false;
		
		try {
			
			if(isNull) {
				stmt.setNull(index, 0);
			}
			else {	
				if(val instanceof String) {
					stmt.setString(index, (String)val);
				}
				
				if(val instanceof Integer) {
					stmt.setInt(index, (Integer)val);
				}
				
				if(val instanceof Boolean) {
					stmt.setBoolean(index, (Boolean)val);
				}
				
				if(val instanceof BigDecimal) {
					stmt.setBigDecimal(index, (BigDecimal)val);
				}
			}
		}
		catch(SQLException e) {
			System.err.println("Konnte Feldwert nicht setzen: " + e.getMessage());
		}
		
		return status;
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
