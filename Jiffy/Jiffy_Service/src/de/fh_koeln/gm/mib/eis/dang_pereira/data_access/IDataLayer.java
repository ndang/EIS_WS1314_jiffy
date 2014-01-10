package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grade;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grades;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;


/**
 * Schnittstelle, um den möglichen und unkomplizierten Austausch des Datenhandlers zu gewährleisten
 * 
 * @author dang_pereira
 *
 */
public interface IDataLayer {

	/**
	 * Daten eines Users anhand seiner ID zurückgeben
	 * 
	 * @param user_id ID des Users
	 * @return JSON-Dokument als User-Objekt
	 */
	public User getUser(Integer userId);
	
	/**
	 * Alle für einen Benutzer relevanten Topics zurückgeben
	 * 
	 * @param userId Benutzer-Id
	 * @return JSON-Dokuemnt als Topics-Objekt
	 */
	public Topics getUserTopics(Integer userId);
	
	
	/**
	 * Alle User eines Systems zurückgeben 
	 * 
	 * @return JSON-Dokument als Users-Objekt
	 */
	public Users getUsers();
	
	
	/**
	 * Daten über einen Schüler beziehen
	 * 
	 * @param userId ID des Schülers
	 * @return Das JSON-Dokument als Student-Objekt
	 */
	public Student getStudent(Integer userId);
	
	
	/**
	 * Neuen Schüler erzeugen
	 * 
	 * @param data Student-Objekt
	 * @param givenPass Das zu verwendende Passwort
	 * @return Die vom System gegebene ID
	 */
	public Integer postStudent(Student student, String givenPass);
	
	
	/**
	 * Daten des Schülers aktualisieren
	 * 
	 * @param userId Id des User-Datensatzes, welches aktualisiert werden soll
	 * @param student Student-Objekt
	 * @param givenPass Das zu verwendende Passwort
	 * @return Aktualisieren war erfolgreich (true) / nicht erfolgreich (false)
	 */
	public boolean putStudent(Integer userId, Student student, String givenPass);
	
	
	/**
	 * Schüler eine neue Note geben
	 * 
	 * @param userId Id des Schülers
	 * @param grade JSON-Dokument als Grade-Objekt
	 * @return Die vom System gegebenen ID
	 */
	public Integer postStudentGrade(Integer userId, Grade grade);
	
	
	/**
	 * Notendetails zurückgeben
	 * 
	 * @param userId Id des Schülers dessen Note zurückgegeben werden soll
	 * @param gradeId Id der Note, deren Details zurückgegeben werden soll
	 * @return JSON-Dokument als Grade-Objekt
	 */
	public Grade getStudentGrade(Integer userId, Integer gradeId);
	
	
	/**
	 * Liste aller Noten, samt allen ihren Details, zurückgeben 
	 * 
	 * @param userId Id des Schülers dessen Noten zurückgegeben werden soll
	 * @return JSON-Dokument als Grades-Objekt
	 */
	public Grades getStudentGrades(Integer userId);
	
	
	/**
	 * Alle dem Erziehungsberechtigten zugeordneten Kinder zurückgeben
	 * 
	 * @param guardianId ID des Erziehungsberechtigten
	 * @return JSON-Dokument als Users-Objekt
	 */
	public Users getGuardianChildren(Integer guardianId);
	
	
	
	/*  #####   Messages   #####  */
	
	/**
	 * Benotungsnachricht in die Datenbank einpflegen
	 * 
	 * @param msg JSON-Dokument als Objekt
	 * @return Erolgreich eingepflegt oder nicht
	 */
	public boolean placeSchoolGradeMsg(Message msg);
	
	
	/**
	 * Infonachricht in die Datenbank einpflegen
	 * 
	 * @param msg JSON-Dokument als Objekt
	 * @return Erolgreich eingepflegt oder nicht
	 */
	public boolean placeSchoolInfoMsg(Message msg);
	
	
	/**
	 * Normale Erziehungsberechtigtennachricht in die Datenbank einpflegen
	 * 
	 * @param msg JSON-Dokument als Objekt
	 * @return Erolgreich eingepflegt oder nicht
	 */
	public boolean placeGuardianMsg(Message msg);
	
	
	/**
	 * Entschuldigungsnachricht in die Datenbank einpflegen
	 * 
	 * @param msg JSON-Dokument als Objekt
	 * @return Erolgreich eingepflegt oder nicht
	 */
	public boolean placeGuardianExcuseMsg(Message msg);
	
	
	/**
	 * Benotung akzeptieren
	 * 
	 * @param msg JSON-Dokument als Objekt
	 * @return Erolgreich eingepflegt oder nicht
	 */
	public boolean approveStudentGrade(Message msg);
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public boolean approveExcuseMsg(Message msg);
	
}
