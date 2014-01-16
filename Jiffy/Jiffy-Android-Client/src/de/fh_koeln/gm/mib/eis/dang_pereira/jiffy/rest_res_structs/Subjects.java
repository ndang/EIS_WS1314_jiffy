package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Subjects {

	@JsonProperty("subjects")
	ArrayList<Subject> subjects;

	
	public Subjects() {}
	
	public Subjects(ArrayList<Subject> subjects) {
		this.subjects = subjects;
	}
	
	
	@JsonGetter("subjects")
	public ArrayList<Subject> getSubjects() {
		return this.subjects;
	}
	
	@JsonSetter("subjects")
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
