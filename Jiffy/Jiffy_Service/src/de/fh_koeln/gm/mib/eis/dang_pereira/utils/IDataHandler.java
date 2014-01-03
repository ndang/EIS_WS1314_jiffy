package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.util.ArrayList;

/**
 * Schnittstelle, um den möglichen und unkomplizierten Austausch des Datenhandlers zu gewährleisten
 * 
 * @author dang_pereira
 *
 */
public interface IDataHandler {

	/**
	 * Beispielmethode
	 * @return List von Datensätzen, wobei alle Datensatzfelder miteinander konkateniert sind
	 */
	public ArrayList<String> getPersonsExample();
		
}
