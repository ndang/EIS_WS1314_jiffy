package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Grades {

	@JsonProperty("grades")
	ArrayList<Grade> grades;
	
	
	public Grades() {}
	
	public Grades(ArrayList<Grade> grades) {
		this.grades = grades;
	}
	
	@JsonGetter("grades")
	public ArrayList<Grade> getGrades() {
		return this.grades;
	}
	
	@JsonSetter("grades")
	public void setGrades(ArrayList<Grade> grades) {
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
