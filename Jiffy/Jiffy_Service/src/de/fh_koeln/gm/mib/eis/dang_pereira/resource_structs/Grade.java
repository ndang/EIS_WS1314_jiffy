package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Grade {

	@JsonProperty("grade")
	Id grade;
	
	@JsonProperty("value")
	BigDecimal value;
	
	@JsonProperty("grade_weight")
	Integer grade_weight;
	
	@JsonProperty("comment")
	String comment;
	
	@JsonProperty("subject")
	Subject subject;
	
	
	public Grade() {}
	
	public Grade(Id grade, BigDecimal value, Integer grade_weight, String comment, Subject subject) {
		this.grade = grade;
		this.value = value;
		this.grade_weight = grade_weight;
		this.comment = comment;
		this.subject = subject;
	}
	
	
	public Id getGrade() {
		return this.grade;
	}
	
	public void setGrade(Id grade) {
		this.grade = grade;
	}
	
	
	public BigDecimal getvalue() {
		return this.value;
	}
	
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	
	public Integer getGradeWeight() {
		return this.grade_weight;
	}
	
	public void setGradeWeight(Integer grade_weight) {
		this.grade_weight = grade_weight;
	}
	
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	public Subject getSubject() {
		return this.subject;
	}
	
	public void setClasses(Subject subject) {
		this.subject = subject;
	}
	
	
	public String toString() {
		
		return "{" +
				"\"grade\": " + this.grade + ",\r\n" +
				"\"value\": " + this.value.toPlainString() + ",\r\n" +
				"\"grade_weight\": " + this.grade_weight + ",\r\n" +
				"\"comment\": " + this.comment + ",\r\n" +
				"\"subject\": " + this.subject + ",\r\n" +
				"}";
	}
	
}
