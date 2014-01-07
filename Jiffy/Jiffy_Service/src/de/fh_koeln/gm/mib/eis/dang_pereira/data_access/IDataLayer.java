package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;

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
	 * @return Die vom System gegebene ID
	 */
	public Integer postStudent(Student student, String givenPass);
	

}
