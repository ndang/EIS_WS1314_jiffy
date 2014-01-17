package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.resource_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Subject {

	@JsonProperty("subject")
	Id subject;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("teacher")
	Id teacher;
	
	
	public Subject() {}
	
	public Subject(Id subject, String description, Id teacher) {
		this.subject = subject;
		this.description = description;
		this.teacher = teacher;
	}
	
	
	@JsonGetter("subject")
	public Id getSubject() {
		return this.subject;
	}
	
	@JsonSetter("subject")
	public void setSubject(Id subject) {
		this.subject = subject;
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
				"\"subject\": " + this.subject + ",\r\n" +
				"\"description\": " + this.description + ",\r\n" +
				"\"teacher\": " + this.teacher + "\r\n" +
				"}";
	}
	
}
