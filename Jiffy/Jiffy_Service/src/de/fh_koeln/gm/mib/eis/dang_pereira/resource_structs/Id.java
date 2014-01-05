package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Id {

	@JsonProperty("id")
	Integer id;
	
	@JsonProperty("uri")
	String uri;
	

	public Id() {}
	
	public Id(Integer id, String uri) {
		this.id = id;
		this.uri = uri;
	}
	
	
	public Integer getID() {
		return this.id;
	}
	
	public void setID(Integer id) {
		this.id = id;
	}
	
	
	public String getURI() {
		return this.uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}
	
	
	public String toString() {
		
		return "{\r\n" +
				"\"id\": " + this.id + ",\r\n" +
				"\"uri\": " + this.uri + "\r\n" +
				"}";
	}
}
