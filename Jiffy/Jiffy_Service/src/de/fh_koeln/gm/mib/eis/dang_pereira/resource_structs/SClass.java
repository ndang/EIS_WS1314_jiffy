package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	
	@JsonGetter("class_id")
	public Id getClassId() {
		return this.class_id;
	}
	
	@JsonSetter("class_id")
	public void setClassId(Id class_id) {
		this.class_id = class_id;
	}
	
	@JsonGetter("year")
	public Integer getYear() {
		return this.year;
	}
	
	@JsonSetter("year")
	public void setYear(Integer year) {
		this.year = year;
	}
	
	@JsonGetter("description")
	public String getDescription() {
		return this.description;
	}
	
	@JsonSetter("description")
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonGetter("teacher")
	public Id getTeacher() {
		return this.teacher;
	}
	
	@JsonSetter("teacher")
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
