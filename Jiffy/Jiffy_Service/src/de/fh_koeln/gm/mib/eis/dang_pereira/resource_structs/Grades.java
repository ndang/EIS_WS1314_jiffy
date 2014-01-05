package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Grades {

	@JsonProperty("grades")
	ArrayList<Grade> grades;
	
	
	public Grades() {}
	
	public Grades(ArrayList<Grade> grades) {
		this.grades = grades;
	}
	
	
	public ArrayList<Grade> getGrades() {
		return this.grades;
	}
	
	public void setClasses(ArrayList<Grade> grades) {
		this.grades = grades;
	}
	
	
	public String toString() {
		
		String tmp = "{\"grades\": [\r\n";
		
		for(Grade g: this.grades) {
			tmp += g + ",\r\n";
		}
		
		return tmp + "]}";
	}
	
}
