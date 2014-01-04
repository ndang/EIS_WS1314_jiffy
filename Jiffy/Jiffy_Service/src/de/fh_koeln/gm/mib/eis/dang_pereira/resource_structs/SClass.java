package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SClass {

	@JsonProperty("class")
	Id class_id;
	
	@JsonProperty("year")
	Integer year;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("teacher")
	Id teacher;
	
	
	public String toString() {
		
		return "{" +
				"\"class\": " + this.class_id + ",\r\n" +
				"\"year\": " + this.year + ",\r\n" +
				"\"decription\": " + this.description + ",\r\n" +
				"\"teacher\": " + this.teacher + ",\r\n" +
				"}";
	}
}
