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
	public String getUser(Integer user_id);
}
