package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.core.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.IDataLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

public abstract class Resource {
	
	protected IDataLayer dbl;
	protected ObjectMapper jmapper;
	
	
	public Resource() {
		this.dbl = DBLayer.getInstance();
		this.jmapper = new ObjectMapper();
	}
	
	
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
	
	/**
	 * 
	 * 
	 * @param userId ID die überprüft werden soll
	 * @param username Benutzername des zu Überprüfenden Benutzers
	 * @return Benutzername gehört zur ID (true) oder Benutzername gehört nicht zur ID (false)
	 */
	protected boolean usernameBelongsToId(String username, Integer userId) {
		
		boolean status = false;
		
		try {
			if(UserDBAuth.usernameBelongsToId(username, userId)) {
				status = true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return status;
	}
	
}
