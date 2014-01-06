package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;

@Path("/student")
public class StudentResource extends Resource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postStudent(@Context HttpHeaders headers, String body) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		
		/* Um das Passwort nicht mit im Dokument zu versenden, wird es per HTTP-Feld überreicht */
		String givenPass = null;
		List<String> list = headers.getRequestHeader("given-user-password");
		
		if(list != null && list.size() == 1)
			givenPass = list.get(0);
		
		
		/* Daten weiter an den DB-Layer geben, damit dieser sie in die DB schreibt */
		DBLayer dbl = DBLayer.getInstance();
		Integer userId = dbl.postStudent(body, givenPass);
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(userId != null){
			return Response.status(201).header("Location", "/user/" + userId).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	@GET
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudent(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		DBLayer dbl = DBLayer.getInstance();
		String userStr = dbl.getStudent(userId); //dbl.getStudent(userId);
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(userStr != null){
			return Response.ok().entity(userStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	

	
}
