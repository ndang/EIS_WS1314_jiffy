package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SClass {

	@JsonProperty("class")
	Id class_id;
	
	@JsonProperty("year")
	Integer year;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("teacher")
	Id teacher;
	
	
	public SClass() {}
	
	public SClass(Id class_id, Integer year, String description, Id teacher) {
		this.class_id = class_id;
		this.year = year;
		this.description = description;
		this.teacher = teacher;
	}
	
	
	public Id getClassId() {
		return this.class_id;
	}
	
	public void setClassId(Id class_id) {
		this.class_id = class_id;
	}
	
	
	public Integer getYear() {
		return this.year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Id getTeacher() {
		return this.teacher;
	}
	
	public void setTeacher(Id teacher) {
		this.teacher = teacher;
	}
	
	
	public String toString() {
		
		return "{" +
				"\"class\": " + this.class_id + ",\r\n" +
				"\"year\": " + this.year + ",\r\n" +
				"\"decription\": " + this.description + ",\r\n" +
				"\"teacher\": " + this.teacher + ",\r\n" +
				"}";
	}
}
