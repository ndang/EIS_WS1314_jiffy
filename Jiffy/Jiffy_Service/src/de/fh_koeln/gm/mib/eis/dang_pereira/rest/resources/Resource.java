package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.core.HttpHeaders;

import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

public class Resource {

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
