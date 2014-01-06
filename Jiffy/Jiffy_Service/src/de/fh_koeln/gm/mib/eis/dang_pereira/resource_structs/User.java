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
	
	@JsonProperty("username")
	String username;
	
	@JsonProperty("user_type")
	String user_type;
	
	@JsonProperty("gender")
	String gender;
	

	public User() {}
	
	public User(Id user, String name, String username, String user_type, String gender) {
		this.user = user;
		this.name = name;
		this.username = username;
		this.user_type = user_type;
		this.gender = gender;
	}
	
	
	public Id getUser() {
		return this.user;
	}
	
	public void setUser(Id user) {
		this.user = user;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getUserType() {
		return this.user_type;
	}
	
	public void setUserType(String user_type) {
		this.user_type = user_type;
	}
	
	public String getGender() {
		return this.gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	protected String getFormattedData() {
		
		return  "\"user\": " + this.user + ",\r\n" +
				"\"name\": " + this.name + ",\r\n" +
				"\"username\": " + this.username + ",\r\n" +
				"\"user_type\": " + this.user_type + ",\r\n" +
				"\"gender\": " + this.gender;
	}
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + "\r\n" +
				"}";
	}
}
