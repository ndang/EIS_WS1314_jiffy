package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

@Path("/guardian")
public class GuardianResource extends Resource {

	public GuardianResource() {
		/* 
		 * Konstruktor der Vaterklasse aufrufen, um Zugriff auf das DataLayer-Objekt und den Jackson ObjectMapper zu erhalten
		 */
		super();
	}
	
	@GET
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		// Nicht implementiert, da für Prototyp nicht notwendig
		
		return Response.status(404).build();
	}
	
	
	@GET
	@Path("/{user_id}/children")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGuardianChildren(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		
		/* Nur ein Erziehungsberechtigter kann Kinder haben */
		if(!idIsOfUserType(userId, "GUARDIAN")) {
			return Response.status(404).build();
		}
		
		
		/* Daten weiter an den DB-Layer geben, damit dieser sie in die DB schreibt */
		Users children = this.dbl.getGuardianChildren(userId);
		
		
		String childrenStr = null;
		
		try {
			childrenStr = jmapper.writeValueAsString(children);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Users-Objekt (Children) nicht serialisieren: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		/* Wenn die Beziehung hergestellt wurde, dann soll der richtige Status zurück gegeben werden */
		if(children != null){
			return Response.ok().entity(childrenStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
}
