package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Id {

	@JsonProperty("id")
	Integer id;
	
	@JsonProperty("uri")
	String uri;
	
	@JsonProperty("destination")
	Destination destination;
	
	
	public Id() {}
	
	public Id(Integer id, String uri, Destination destination) {
		this.id = id;
		this.uri = uri;
		this.destination = destination;
	}
	
	@JsonGetter("id")
	public Integer getID() {
		return this.id;
	}
	
	@JsonSetter("id")
	public void setID(Integer id) {
		this.id = id;
	}
	
	@JsonGetter("destination")
	public Destination getDestination() {
		return this.destination;
	}
	
	@JsonSetter("destination")
	public void setPrivate(Destination destination) {
		this.destination = destination;
	}
	
	public String toString() {
		
		return "{\r\n" +
				"\"id\": " + this.id + ",\r\n" +
				"\"uri\": " + this.uri + "\r\n" +
				"\"destination\": " + this.destination + "\r\n" +
				"}";
	}
}
