package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* 
 * Eigenschaften, die im JSON-Dokuemnt existieren, aber nich in der Klasse, nicht ignorieren.
 *	Als Beispiel: Ein guardian ist vom Obertyp user, aber nicht vom Typ guardian.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class Guardian extends User {

	@JsonProperty("language")
	String language;
	
	@JsonProperty("contact_person")
	Boolean contact_person;
	
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + ",\r\n" +
				"\"language\": " + this.language + ",\r\n" +
				"\"contact_person\": " + this.contact_person + "\r\n" +
				"}";
	}
	
}
