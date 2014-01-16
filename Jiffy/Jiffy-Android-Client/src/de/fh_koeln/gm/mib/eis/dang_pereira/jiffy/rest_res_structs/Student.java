package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* 
 * Eigenschaften, die im JSON-Dokuemnt existieren, aber nich in der Klasse, nicht ignorieren.
 *	Als Beispiel: Ein guardian ist vom Obertyp user, aber nicht vom Typ guardian.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class Student extends User {

	@JsonProperty("guardian")
	Id guardian;
	
	@JsonProperty("grades_uri")
	String grades_uri;
	
	@JsonProperty("latest_class")
	Id latest_class;
	
	
	public Student() {}
	
	public Student(Id user, String name, String username, String user_type, String gender) {
		super(user, name, username, user_type, gender);
	}

	public Student(Id user, String name, String username, String user_type, String gender, Id guardian, String grades_uri, Id latest_class) {
		super(user, name, username, user_type, gender);
		
		this.guardian = guardian;
		this.grades_uri = grades_uri;
		this.latest_class = latest_class;
	}

	
	@JsonGetter("guardian")
	public Id getGuardian() {
		return this.guardian;
	}
	
	@JsonSetter("guardian")
	public void setGuardian(Id guardian) {
		this.guardian = guardian;
	}
	
	@JsonGetter("grades_uri")
	public String getGradesURI() {
		return this.grades_uri;
	}
	
	@JsonSetter("grades_uri")
	public void setGradesURI(String grades_uri) {
		this.grades_uri = grades_uri;
	}
	
	@JsonGetter("latest_class")
	public Id getLatestClass() {
		return this.latest_class;
	}
	
	@JsonSetter("latest_class")
	public void setLatestClass(Id latest_class) {
		this.latest_class = latest_class;
	}
	
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + ",\r\n" +
				"\"guardian\": " + this.guardian + ",\r\n" +
				"\"grades_uri\": " + this.grades_uri + ",\r\n" +
				"\"latest_class\": " + this.latest_class + "\r\n" +
				"}";
	}
}
