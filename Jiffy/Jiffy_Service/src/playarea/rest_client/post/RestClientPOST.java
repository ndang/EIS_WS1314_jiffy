package playarea.rest_client.post;

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
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;


public class RestClientPOST {

	
	public static void main(String[] args) throws Exception {
		
		String user = "horstblumen";
		String pass = "Christa";
		
		String uri = "/student";
		
		String mime = MediaType.APPLICATION_JSON;
		
		/* Student-Objekt zum Versenden zusammenstellen */
		ObjectMapper jmapper = new ObjectMapper();
		Student student = new Student(null, "Hans Peter2", "hanspeter2", "STUDENT", "male", null, null, null);
		String data = jmapper.writeValueAsString(student);
		
		System.out.println(data);
		
		
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
		
		/* Das gewählte Password */
		String password = "Christa";
		password = new String(Base64.encode(password.getBytes("utf-8")));
		
		/* Interessant hier: Das Password wird per Header mitgegeben, anstatt es im Payload mitzugeben -> Struktur sieht nämlich keine Password-Eigenschaft vor */
		ClientResponse cresp = wr.path(uri).type(mime).header("given-user-password", password).entity(data).post(ClientResponse.class);
		
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
