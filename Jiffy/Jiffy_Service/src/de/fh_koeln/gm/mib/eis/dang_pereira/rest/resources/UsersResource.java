package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;

@Path("/users")
public class UsersResource extends Resource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		DBLayer dbl = DBLayer.getInstance();
		String userStr = dbl.getUsers();
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(userStr != null){
			return Response.ok().entity(userStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
}
