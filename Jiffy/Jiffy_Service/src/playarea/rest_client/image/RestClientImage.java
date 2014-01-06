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


public class RestClientImage {

	
	public static void main(String[] args) throws Exception {
		
		String user = "Peter";
		String pass = "Christa";
		
		String uri = "/user/3/image";
		
		String mime = "image/jpeg"; //MediaType.APPLICATION_JSON;
		
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
		
		/* Beispiel-Datum (man muss mit dem GMT aufpassen, da man in Deutschland bei GMT+1 liegt) */
		ClientResponse cresp = wr.path(uri).accept(mime).header("If-Modified-Since", "Mon, 05 Jan 2014 23:16:00 GMT").get(ClientResponse.class);
		
		if(cresp.getStatus() == 200) {
			InputStream entity = cresp.getEntityInputStream();
			
			byte[] data = new byte[(int)cresp.getLength()];
			DataInputStream dataIs = new DataInputStream(entity);
			dataIs.readFully(data);
			
			System.out.println(new String(data));
			
			//FileOutputStream fos = new FileOutputStream(new File("test.jpg"));
			
			//fos.write(data);
			//fos.close();
			
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
			System.exit(1);
		}
		
		
		System.exit(0);
	}
}
