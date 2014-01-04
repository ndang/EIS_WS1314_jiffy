package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Users {

	@JsonProperty("users")
	ArrayList<User> users;
	
	public String toString() {
		
		String tmp = "{\"users\": [\r\n";
		
		for(User u: this.users) {
			tmp += u + ",\r\n";
		}
		
		return tmp + "]}";
	}
	
}
