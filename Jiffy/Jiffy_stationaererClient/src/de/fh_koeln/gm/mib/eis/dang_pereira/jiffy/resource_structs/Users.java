package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Users {

	@JsonProperty("users")
	ArrayList<User> users;
	
	
	public Users() {}
	
	public Users(ArrayList<User> users) {
		this.users = users;
	}
	
	@JsonGetter("users")
	public ArrayList<User> getUsers() {
		return this.users;
	}
	
	@JsonSetter("users")
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	
	public String toString() {
		
		String tmp = "{\"users\": [\r\n";
		
		for(User u: this.users) {
			tmp += u + ",\r\n";
		}
		
		return tmp + "]}";
	}
	
}
