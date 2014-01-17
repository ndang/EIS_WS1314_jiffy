package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.data_access;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers.OwnSSLContext;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs.Users;

public class RESTDataHandler {

	private static RESTDataHandler _instance = null;
	
	private ObjectMapper jmapper;
	
	private RESTDataHandler() {
		jmapper = new ObjectMapper();
	}
	
	
	public WebResource getWebResource() {
		Config cfg = Config.getInstance();
		
		SSLContext ctx = null;
		try {
			ctx = OwnSSLContext.getContext(cfg.ssl.path_ks, cfg.ssl.pass_ks,
					cfg.ssl.path_ts, cfg.ssl.pass_ts);
		} catch (Exception e) {
			System.err.println("SSL-Context-Fehler: " + e.getMessage());
		}

		HTTPSProperties httpsProp = new HTTPSProperties(new HostnameVerifier() {

			@Override
			public boolean verify(String host, SSLSession session) {
				
				// return session.getPeerHost().equalsIgnoreCase(host);
				
				return true;
			}
		}, ctx);
		
		
		ClientConfig config = new DefaultClientConfig();
		config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProp);
		
		String user = cfg.username;
		String pass = cfg.password;
		
		WebResource wr = Client.create(config).resource(cfg.rest_endpoint.host + ":" + cfg.rest_endpoint.port);
		wr.addFilter(new HTTPBasicAuthFilter(user, pass));
		
		System.out.println("Client initialisiert!");
		
		return wr;
	}
	
	
	public boolean available() {
		boolean connect = false;
		
		
		try {
			ClientResponse cresp = getWebResource().path("/").get(ClientResponse.class);
			connect = true;
		}
		catch(Exception e) {
			System.err.println("RESTEndpoint ist nicht verfügbar!" + e.getMessage());
		}
		
		return connect;
	}
	
	
	public static RESTDataHandler getInstance() {
		if(_instance == null) {
			_instance = new RESTDataHandler();
		}
		
		return _instance;
	}
	


	public User getUser(Integer userId) {
		User user = null;
		
Config cfg = Config.getInstance();
		
		String username = cfg.username;
		String uri = "/user/"+userId;
		
		ClientResponse cresp = getWebResource().path(uri).queryParam("username", username).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		if(cresp.getStatus() == 200) {
			String entity = cresp.getEntity(String.class);
			
			try {
				user = jmapper.readValue(entity, User.class);
			} catch (Exception e) {
				System.err.println("Fehler beim Marshalling: " +e.getMessage());
			}
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
		}
		
		return user;
	}
	
	
	
	public Topics getTopicsToSubscribe() {
		Topics topics = null;
		
		Config cfg = Config.getInstance();
		
		String username = cfg.username;
		String uri = "/users";
		
		ClientResponse cresp = getWebResource().path(uri).queryParam("username", username).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		if(cresp.getStatus() == 200) {
			String entity = cresp.getEntity(String.class);
			
			Users users;
			try {
				users = jmapper.readValue(entity, Users.class);
				User user = users.getUsers().get(0);
				
				if(user.getUser().getID() != null) {
					cfg.userId = user.getUser().getID();
					
					String topicsUri = "/user/" + cfg.userId + "/topics";
					
					ClientResponse crespTopics = getWebResource().path(topicsUri).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
					
					if(crespTopics.getStatus() == 200) {
						String entityTopics = crespTopics.getEntity(String.class);
						
						try {
							topics = jmapper.readValue(entityTopics, Topics.class);
						} catch (Exception e) {
							System.err.println("Fehler beim Marshalling: " +e.getMessage());
						}
					}
					else {
						System.err.println("Statuscode: " + crespTopics.getStatus());
					}
					
				}
				
			} catch (Exception e) {
				System.err.println("Fehler beim Marshalling: " +e.getMessage());
			}
		}
		else {
			System.err.println("Statuscode: " + cresp.getStatus());
		}
		
		return topics;
	}
	
}
