package playarea.rest_client.image;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import playarea.rest_client.OwnSSLContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;


public class RestClientImageDELETE {

	
	public static void main(String[] args) throws Exception {
		
		String user = "Peter";
		String pass = "Christa";
		
		String uri = "/user/3/image";
		
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
		
		ClientResponse cresp = wr.path(uri).delete(ClientResponse.class);
		
		if(cresp.getStatus() == 204) {
			System.out.println("Die Ressource wurde erfolgreich gelöscht!");
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
			System.exit(1);
		}
		
		
		System.exit(0);
	}
}
