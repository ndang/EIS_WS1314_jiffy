package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

@Path("/user")
public class UserResource {

	@GET
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers, @PathParam("user_id") Integer user_id) {
		
		if(userExists(headers)) {
			return Response.status(401).build();
		}
			
		DBLayer dbl = DBLayer.getInstance();
		String user_str = dbl.getUser(user_id);
		
		if(user_str != null){
			return Response.ok().entity(user_str).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	
	private boolean userExists(HttpHeaders headers) {
		
		boolean status = false;
		
		try {
			if(!UserDBAuth.authUser(BasicAuthHelper.extractAuthCreds(headers))) {
				status = true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
}
