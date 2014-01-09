package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;

@Path("/users")
public class UsersResource extends Resource {
	
	
	public UsersResource() {
		/* 
		 * Konstruktor der Vaterklasse aufrufen, um Zugriff auf das DataLayer-Objekt und den Jackson ObjectMapper zu erhalten
		 */
		super();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		Users users = this.dbl.getUsers();
		
		String usersStr = null;
		
		try {
			usersStr = jmapper.writeValueAsString(users);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Studenten-Objekt nicht serialisieren: " + e.getMessage());
			e.printStackTrace();
		}
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(usersStr != null){
			return Response.ok().entity(usersStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
}
