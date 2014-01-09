package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.fh_koeln.gm.mib.eis.dang_pereira.data_access.DBLayer;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;

@Path("/user")
public class UserResource extends Resource {

	public UserResource() {
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
		
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		User user = this.dbl.getUser(userId);
		
		String userStr = null;
		
		try {
			userStr = jmapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Studenten-Objekt nicht serialisieren: " + e.getMessage());
			e.printStackTrace();
		}
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(userStr != null){
			return Response.ok().entity(userStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	@GET
	@Path("/{user_id}/topics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserTopics(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		

		String username = BasicAuthHelper.extractAuthCreds(headers).get("user");
		
		if(!usernameBelongsToId(username, userId)) {
			return Response.status(401).build();
		}
		
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		Topics topics = this.dbl.getUserTopics(userId);
		
		String topicsStr = null;
		
		try {
			topicsStr = jmapper.writeValueAsString(topics);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Studenten-Objekt nicht serialisieren: " + e.getMessage());
			e.printStackTrace();
		}
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(topicsStr != null){
			return Response.ok().entity(topicsStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	
	@GET
	@Path("/{user_id}/image")
	@Produces("image/jpeg")
	public Response getUserImage(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Grafiken werden zur Demonstration nicht in der Datenbank, sondern auf der Festplatte aufbewart */
		File file = new File("profile_images/" + userId +".jpg");
		
		/* Der Inhalt der Datei kann nur zurückgegeben werden, wenn sie auch existiert */
		if(file.exists()) {
			
			/* Die Standardannahme ist, dass die Datei geändert wurde, sofern nicht genau überprüft */
			boolean fileModified = true;
			
			/* "If-Modified-Since"-Headerfeld auswerten */
			List<String> list = headers.getRequestHeader("if-modified-since");
			
			if(list != null && list.size() == 1 && list.get(0) != null) {

				/* Siehe RFC 2616 */
				SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
				try {
					Date headerDate = format.parse(list.get(0));
					Date fileDate = new Date(file.lastModified());
					
					/* Eigentliche Zeitüberprüfung */
					if(fileDate.before(headerDate))
						fileModified = false;
					
				} catch (ParseException e) {
					fileModified = true;
					
					System.err.println("Konnte Datum nicht parsen: " + e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			/* Datei wurde seit dem letzten Mal nicht geändert, darum wird der Status-Code 301 gesendet */
			if(!fileModified)
				return Response.status(304).build();
			
			/* Ansonsten wird die Datei ausgelesen und ihr Inhalt zurückgegeben */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				
				ImageIO.write(ImageIO.read(file), "jpeg", baos);
				byte[] imgData = baos.toByteArray();

				return Response.ok().entity(imgData).type("image/jpeg").build();
				
			} catch (IOException e) {
				System.err.println("Konnte Datei nicht lesen: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return Response.status(404).build();
	}
	
	
	
	
	@PUT
	@Path("/{user_id}/image")
	@Produces("image/jpeg")
	public Response setUserImage(@Context HttpHeaders headers, @PathParam("user_id") Integer userId, InputStream body) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}

		/* "Content-Length"-Headerfeld beziehen */
		List<String> list = headers.getRequestHeader("content-length");
		
		if(list != null && list.size() == 1) {
			
			/* Datenlänge parsen, um ein passend großes byte-Array zu initialisieren */
			int contenLength = Integer.parseInt(list.get(0));
			
			if(body != null) {
				byte[] data = new byte[contenLength];
				DataInputStream dataIs = new DataInputStream(body);
				
				boolean success = false;
				
				try {
					dataIs.readFully(data);
					
					/* Übergebene Daten in die Datei schreiben */
					File f = new File("profile_images/" + userId + ".jpg");
					f.delete();
					f.createNewFile();
					
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(data);
					fos.close();
					
					success = true;

				} catch (IOException e) {
					System.out.println("Konnte Daten nicht schreiben: " + e.getMessage());
					e.printStackTrace();
				}
				
				if(success)
					return Response.status(201).build();
			}
		}
		
		return Response.status(404).build();
	}
	
	
	@DELETE
	@Path("/{user_id}/image")
	public Response deleteUserImage(@Context HttpHeaders headers, @PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
	
		File f = new File("profile_images/" + userId + ".jpg");
		
		/* Überprüfen, ob die Datei überhaupt existiert, um sie löschen zu können */
		if(f.exists()) {
			
			/* Datei löschen */
			f.delete();
			
			/* Sicher gehen, dass die Datei wirklich gelöscht wurde */
			if(!f.exists())
				return Response.status(204).build();
		}

		return Response.status(404).build();
	}
	
}
