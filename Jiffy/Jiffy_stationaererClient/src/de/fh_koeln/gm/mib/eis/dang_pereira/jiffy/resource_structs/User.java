package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/* Ignoriere Eigenschaften, die im JSON-Dokument existieren, aber nicht in der Klasse */
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
	
	@JsonGetter("user")
	public Id getUser() {
		return this.user;
	}
	
	@JsonSetter("user")
	public void setUser(Id user) {
		this.user = user;
	}
	
	@JsonGetter("name")
	public String getName() {
		return this.name;
	}
	
	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonGetter("username")
	public String getUsername() {
		return this.username;
	}
	
	@JsonSetter("username")
	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonGetter("user_type")
	public String getUserType() {
		return this.user_type;
	}
	
	@JsonSetter("user_type")
	public void setUserType(String user_type) {
		this.user_type = user_type;
	}
	
	@JsonGetter("gender")
	public String getGender() {
		return this.gender;
	}
	
	@JsonSetter("gender")
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
