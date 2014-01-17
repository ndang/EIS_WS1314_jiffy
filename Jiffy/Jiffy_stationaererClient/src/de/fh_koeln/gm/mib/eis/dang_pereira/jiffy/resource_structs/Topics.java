package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Topics {

	@JsonProperty("topics")
	ArrayList<String> topics;
	
	
	public Topics() {}
	
	public Topics(ArrayList<String> topics) {
		this.topics = topics;
	}
	
	@JsonGetter("topics")
	public ArrayList<String> getTopics() {
		return this.topics;
	}
	
	@JsonSetter("topics")
	public void setTopics(ArrayList<String> topics) {
		this.topics = topics;
	}
	
	
	public String toString() {
		
		String tmp = "{\"topics\": [\r\n";
		
		for(String t: this.topics) {
			tmp += t + ",\r\n";
		}
		
		return tmp + "]}";
	}
	
}
