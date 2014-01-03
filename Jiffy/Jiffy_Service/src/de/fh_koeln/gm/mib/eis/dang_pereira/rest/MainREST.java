package de.fh_koeln.gm.mib.eis.dang_pereira.rest;

import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.DBHandler;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.IDataHandler;

public class MainREST {

	
	public MainREST() {
		/* TODO: Grizzly starten und Requests handlen */
		
		Config cfg = Config.getInstance();
		IDataHandler dh = DBHandler.getInstance();
		
		
		
		ArrayList<String> personen_arr = dh.getPersonsExample();
		
		for(String pers: personen_arr) {
			System.out.println(pers);
		}
	}
	
	
	public static void main(String[] args) {
		
		/* Einstiegs- bzw. Startpunkt für den REST-Endpoint */
		
		new MainREST();
	}
}
