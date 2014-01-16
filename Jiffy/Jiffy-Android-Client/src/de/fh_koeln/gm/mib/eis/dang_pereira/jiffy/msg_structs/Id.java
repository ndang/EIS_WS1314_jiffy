package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	
	@JsonGetter("id")
	public Integer getID() {
		return this.id;
	}
	
	@JsonSetter("id")
	public void setID(Integer id) {
		this.id = id;
	}
	
	
	@JsonGetter("uri")
	public String getURI() {
		return this.uri;
	}
	
	@JsonSetter("uri")
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
