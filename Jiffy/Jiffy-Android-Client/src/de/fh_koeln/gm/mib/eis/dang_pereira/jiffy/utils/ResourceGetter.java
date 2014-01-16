package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_client.HTTPCallback;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_client.RestAsyncTask;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.Users;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;


public class ResourceGetter {
	
	private ContextWrapper cw;
	private HashMap<String, String> headers;
	private ResourceGetter self;

	public ResourceGetter(ContextWrapper cw, HashMap<String, String> headers) {
		this.cw = cw;
		this.headers = headers;
		this.self = this;
	}
	
	
	public void getTopicsToSubscribe(String username, final ResourceCallback callback) {

		final ObjectMapper jmapper = new ObjectMapper();
		
		String filteredUserPath = "/users?username="+username;
		
		/* Request -> Liste mit dem gefilterten User (man selbst) */
		new RestAsyncTask().execute(this.cw, "GET", filteredUserPath, new HTTPCallback() {
			
			@Override
			public void handle(Bundle resp) {
				if(resp == null)
					return;
				
				if(			resp.containsKey("statuscode")
						&&	resp.getInt("statuscode") == 200
						&&	resp.containsKey("data")) {
					
					try {
						Users users = jmapper.readValue(resp.getByteArray("data"), Users.class);
						User user = users.getUsers().get(0); 
						
						final String topicsURL = "/user/" + user.getUser().getID() + "/topics";
						
						/* Sub-Request ->  */
						new RestAsyncTask().execute(self.cw, "GET", topicsURL, new HTTPCallback() {
							
							@Override
							public void handle(Bundle resp) {
								
								if(resp == null)
									return;
								
								if(resp.containsKey("data")) {
									byte[] data = resp.getByteArray("data");
									Log.d(Config.TAG, new String(data));
									
									ObjectMapper jmapper = new ObjectMapper();
									Topics topics = null;
									try {
										topics = jmapper.readValue(data, Topics.class);
									} catch (Exception e) {
										Log.e(Config.TAG, "Konnte JSON-Dokument nicht parsen: " + e.getMessage());
									}
									
									if(topics != null) {
										ArrayList<String> givenTopicsToSubscribe = topics.getTopics();
										
										if(givenTopicsToSubscribe != null) {
											String[] topicsToSubsribe = givenTopicsToSubscribe.toArray(new String[givenTopicsToSubscribe.size()]);
											Bundle bdl = new Bundle();
											bdl.putStringArray("topicsToSubscribe", topicsToSubsribe);
											callback.receive(true, bdl);
										}
									}
									
								}
							}
						}, headers);
						
						
					} catch (Exception e) {
						Log.e(Config.TAG, "Konnte JSON-Dokument nicht parsen: " + e.getMessage());
					}
				} else {
					Log.e(Config.TAG, "Konnte Ressource nicht beziehen!");
				}
			}
		}, headers);

	}
}
