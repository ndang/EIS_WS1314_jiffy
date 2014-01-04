package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

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
	
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + ",\r\n" +
				"\"guardian\": " + this.guardian + ",\r\n" +
				"\"grades_uri\": " + this.grades_uri + ",\r\n" +
				"\"latest_class\": " + this.latest_class + "\r\n" +
				"}";
	}
}
