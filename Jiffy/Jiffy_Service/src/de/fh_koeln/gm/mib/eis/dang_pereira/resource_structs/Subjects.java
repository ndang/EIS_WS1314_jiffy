package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subjects {

	@JsonProperty("subjects")
	ArrayList<Subject> subjects;

	
	public Subjects(ArrayList<Subject> subjects) {
		this.subjects = subjects;
	}
	
	
	public ArrayList<Subject> getSubjects() {
		return this.subjects;
	}
	
	public void setSubjects(ArrayList<Subject> subjects) {
		this.subjects = subjects;
	}
	
	
	public String toString() {
		
		String tmp = "{\"subjects\": [\r\n";
		
		for(Subject s: this.subjects) {
			tmp += s + "\r\n";
		}
		
		return tmp + "]}";
	}
	
}
