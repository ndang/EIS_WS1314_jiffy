package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Id {

	@JsonProperty("id")
	Integer id;
	
	@JsonProperty("uri")
	String uri;
	
	
	public String toString() {
		
		return "{\r\n" +
				"\"id\": " + this.id + ",\r\n" +
				"\"uri\": " + this.uri + "\r\n" +
				"}";
	}
}
