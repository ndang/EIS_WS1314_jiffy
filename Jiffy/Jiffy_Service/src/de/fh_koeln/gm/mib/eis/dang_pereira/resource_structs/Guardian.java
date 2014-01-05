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
	
	
	public Guardian(Id user, String name, String user_type, String gender) {
		super(user, name, user_type, gender);
	}

	public Guardian(Id user, String name, String user_type, String gender, String language, Boolean contact_person) {
		super(user, name, user_type, gender);
		
		this.language = language;
		this.contact_person = contact_person;
	}
	
	
	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Boolean getContactPerson() {
		return this.contact_person;
	}
	
	public void setContactPerson(Boolean contact_person) {
		this.contact_person = contact_person;
	}
	
	
	public String toString() {
		
		return "{" +
				this.getFormattedData() + ",\r\n" +
				"\"language\": " + this.language + ",\r\n" +
				"\"contact_person\": " + this.contact_person + "\r\n" +
				"}";
	}
	
}
