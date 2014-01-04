package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Ignoriere Eigenschaften, die im JSON-Dokuemnt existieren, aber nich in der Klasse */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	@JsonProperty("user")
	Id user;
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("user_type")
	String user_type;
	
	@JsonProperty("gender")
	String gender;
	
	
	protected String getFormattedData() {
		
		return  "\"user\": " + this.user + ",\r\n" +
				"\"name\": " + this.name + ",\r\n" +
				"\"user_type\": " + this.user_type + ",\r\n" +
				"\"gender\": " + this.gender;
	}
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + "\r\n" +
				"}";
	}
	
}
