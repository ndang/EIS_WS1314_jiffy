package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.core.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

public abstract class Resource {
	
	protected ObjectMapper jmapper = new ObjectMapper();
	
	
	/**
	 * Überprüfen, ob der Benutzer in der Datenbank gefunden wird und somit Zugriff auf das System hat
	 * 
	 * @param headers Die Übermittelten HTTP-Header
	 * @return Benutzer existiert (true) oder Benutzer existiert nicht (false)
	 */
	protected boolean userExists(HttpHeaders headers) {
		
		boolean status = false;
		
		try {
			if(UserDBAuth.authUser(BasicAuthHelper.extractAuthCreds(headers))) {
				status = true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return status;
	}
	
	
	
}
