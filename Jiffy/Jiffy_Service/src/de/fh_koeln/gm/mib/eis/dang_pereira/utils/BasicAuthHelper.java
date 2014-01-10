package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.core.util.Base64;

/**
 * Hilfsklasse, um die Anmeldedaten aus der Header-Eigenschaft "authorization" zu extrahieren
 * 
 * @author dang_pereira
 *
 */

public class BasicAuthHelper {

	/* Es soll keine Instanz dieser Klasse erzeugt werden können */
	private BasicAuthHelper() {};
	
	public static HashMap<String, String> extractAuthCreds(HttpHeaders headers) {
		
		if(headers == null)
			return null;
			
		List<String> list = headers.getRequestHeader("authorization");
		
		/* Wenn keine Autohization-Headereigenschaftgefunden wurde, dann gebe null zurück */
		if(list == null || list.size() != 1)
			return null;

		String auth = list.get(0);
		
		HashMap<String, String> creds = new HashMap<String, String>();
		
		/* Die Anmeldedaten im base64-Format extrahieren */
		auth = auth.substring("Basic ".length());
		
		/* Die Anmeldedaten im base64-Format dekodieren und aufsplitten */
        String[] values = new String(Base64.base64Decode(auth)).split(":");
		
        if(values.length != 2)
        	return null;
        
        creds.put("user", values[0]);
        creds.put("pass", values[1]);
        
		return creds;
		
	}
}
