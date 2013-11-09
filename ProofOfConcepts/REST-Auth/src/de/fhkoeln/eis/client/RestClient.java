package de.fhkoeln.eis.client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import de.fhkoeln.eis.Config;

public class RestClient {

	
	public static void main(String[] args) throws Exception {
		
		String user = "Peter";
		String pass = "Christa";
		
		
		
		SSLContext ctx = OwnSSLContext.getContext(Config.Security.pathKS, Config.Security.passKS,
				Config.Security.pathTS, Config.Security.passTS);

		HTTPSProperties httpsProp = new HTTPSProperties(new HostnameVerifier() {

			@Override
			public boolean verify(String host, SSLSession session) {
				
				// return session.getPeerHost().equalsIgnoreCase(host);
				
				return true;
			}
		}, ctx);
		
		
		ClientConfig config = new DefaultClientConfig();
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProp);
		
		WebResource wr = Client.create(config).resource(Config.hostname + ":" + Config.port);
		wr.addFilter(new HTTPBasicAuthFilter(user, pass));
		
		ClientResponse cresp = wr.path("/").type(MediaType.TEXT_PLAIN).get(ClientResponse.class);
		
		if(cresp.getStatus() == 200) {
			String entity = cresp.getEntity(String.class);
			
			System.out.println(entity);
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
			System.exit(1);
		}
		
		
		System.exit(0);
	}
}
