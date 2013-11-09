package de.fhkoeln.eis.utils;

import java.util.HashMap;

import com.sun.jersey.core.util.Base64;

public class BasicAuthHelper {

	public static HashMap<String, String> extractAuthCreds(String auth) {
		HashMap<String, String> creds = new HashMap<String, String>();
		
		auth = auth.substring("Basic ".length());
        String[] values = new String(Base64.base64Decode(auth)).split(":");
		
        creds.put("user", values[0]);
        creds.put("pass", values[1]);
        
		return creds;
		
	}
	
	public BasicAuthHelper() {};
}
