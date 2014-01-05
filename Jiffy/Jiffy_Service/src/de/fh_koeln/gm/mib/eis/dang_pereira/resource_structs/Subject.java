package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subject {

	@JsonProperty("subject")
	Id subject;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("teacher")
	Id teacher;
	
	
	public Subject(Id subject, String description, Id teacher) {
		this.subject = subject;
		this.description = description;
		this.teacher = teacher;
	}
	
	
	public Id getSubject() {
		return this.subject;
	}
	
	public void setSubject(Id subject) {
		this.subject = subject;
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
				"\"subject\": " + this.subject + ",\r\n" +
				"\"description\": " + this.description + ",\r\n" +
				"\"teacher\": " + this.teacher + "\r\n" +
				"}";
	}
	
}
