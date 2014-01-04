package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subject {

	@JsonProperty("subject")
	Id subject;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("teacher")
	Id teacher;
	

	public String toString() {
		
		return "{" +
				"\"subject\": " + this.subject + ",\r\n" +
				"\"description\": " + this.description + ",\r\n" +
				"\"teacher\": " + this.teacher + "\r\n" +
				"}";
	}
	
}
