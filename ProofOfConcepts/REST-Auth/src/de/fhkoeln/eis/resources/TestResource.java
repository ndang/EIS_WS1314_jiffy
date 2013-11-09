package de.fhkoeln.eis.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fhkoeln.eis.dbauth.UserDBAuth;
import de.fhkoeln.eis.utils.BasicAuthHelper;


@Path("/")
public class TestResource {

        
        public TestResource() {
        }
        
        @GET
        public Response getUsers(@Context HttpHeaders headers) {
            
        	List<String> list = headers.getRequestHeader("authorization");
        	
        	if(list != null && list.size() > 0) {
	            try {
					if(UserDBAuth.authUser(BasicAuthHelper.extractAuthCreds(list.get(0)))) {
						return Response.ok().entity("Brisante Daten!").type(MediaType.TEXT_PLAIN).build();
					}
					
				} catch (Exception e) { }
        	}
            
        	return Response.status(401).type(MediaType.TEXT_PLAIN).build();
                        
        }
        
}
