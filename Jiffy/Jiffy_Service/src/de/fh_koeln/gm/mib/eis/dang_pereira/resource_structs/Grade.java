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
