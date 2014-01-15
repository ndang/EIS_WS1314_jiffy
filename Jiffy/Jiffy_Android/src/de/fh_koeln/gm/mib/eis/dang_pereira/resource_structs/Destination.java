package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Destination {
	
	@JsonProperty("private")
	String private_topic;
	
	@JsonProperty("official")
	String official_topic;
	
	public Destination() {}
	
	public Destination(String private_topic, String official_topic) {
		this.private_topic = private_topic;
		this.official_topic = official_topic;
	}
	
	@JsonGetter("private")
	public String getPrivate() {
		return this.private_topic;
	}
	
	@JsonSetter("private")
	public void setPrivate(String private_topic) {
		this.private_topic = private_topic;
	}
	
	
	@JsonGetter("official")
	public String getOfficial() {
		return this.official_topic;
	}
	
	@JsonSetter("official")
	public void setOfficial(String official_topic) {
		this.official_topic = official_topic;
	}
	
	public String toString() {
		
		return "{\r\n" +
				"\"private\": " + this.private_topic + ",\r\n" +
				"\"official\": " + this.official_topic + "\r\n" +
				"}";
	}
	
}
