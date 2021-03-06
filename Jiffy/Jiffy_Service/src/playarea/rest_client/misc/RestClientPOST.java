package playarea.rest_client.misc;

import java.math.BigDecimal;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import playarea.rest_client.OwnSSLContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.Base64;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grade;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Subject;


public class RestClientPOST {

	
	public static void main(String[] args) throws Exception {
		
		String user = "horstblumen";
		String pass = "Christa";
		
		String uri = "/student";
		
		String mime = MediaType.APPLICATION_JSON;
		
		/* Noten-Objekt zum Versenden zusammenstellen */
		ObjectMapper jmapper = new ObjectMapper();
		Student student = new Student(null, "Harry Humbolder", "harryhumbolder4", null, "male", null, null, null);
		String data = jmapper.writeValueAsString(student);
		
		System.out.println(data);
		
		
		String passB64 = new String(Base64.encode("Christa".getBytes("utf-8")));
		
		
		Config cfg = Config.getInstance();
		
		SSLContext ctx = OwnSSLContext.getContext(cfg.ssl.path_ks, cfg.ssl.pass_ks,
				cfg.ssl.path_ts, cfg.ssl.pass_ts);

		HTTPSProperties httpsProp = new HTTPSProperties(new HostnameVerifier() {

			@Override
			public boolean verify(String host, SSLSession session) {
				
				// return session.getPeerHost().equalsIgnoreCase(host);
				
				return true;
			}
		}, ctx);
		
		
		ClientConfig config = new DefaultClientConfig();
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProp);
		
		WebResource wr = Client.create(config).resource(cfg.rest_endpoint.host + ":" + cfg.rest_endpoint.port);
		wr.addFilter(new HTTPBasicAuthFilter(user, pass));
		
		ClientResponse cresp = wr.path(uri).type(mime).entity(data).header("given-user-password", passB64).post(ClientResponse.class);
		
		if(cresp.getStatus() == 201) {

			System.out.println(cresp.getLocation());
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
			System.exit(1);
		}
		
		
		System.exit(0);
	}
}
