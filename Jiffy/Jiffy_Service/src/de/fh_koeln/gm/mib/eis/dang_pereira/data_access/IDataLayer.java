package de.fh_koeln.gm.mib.eis.dang_pereira.data_access;


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
	 * @return JSON-Dokument als String
	 */
	public String getUser(Integer userId);
	
	
	/**
	 * Alle User eines Systems zurückgeben 
	 * 
	 * @return JSON-Dokument mit allen Userdaten
	 */
	public String getUsers();
	
	
	/**
	 * Daten über einen Schüler beziehen
	 * 
	 * @param userId ID des Schülers
	 * @return JSON-Dokument als String
	 */
	public String getStudent(Integer userId);
	
	
	/**
	 * Neuen Schüler erzeugen
	 * 
	 * @param data Valides JSON-Dokument
	 * @return Die vom System gegebene ID
	 */
	public Integer postStudent(String data, String givenPass);
	

}
