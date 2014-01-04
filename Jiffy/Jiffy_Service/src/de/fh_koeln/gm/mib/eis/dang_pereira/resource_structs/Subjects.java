package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subjects {

	@JsonProperty("subjects")
	ArrayList<Subject> subjects;

	public String toString() {
		
		String tmp = "{\"subjects\": [\r\n";
		
		for(Subject s: this.subjects) {
			tmp += s + "\r\n";
		}
		
		return tmp + "]}";
	}
	
}
