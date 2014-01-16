package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	
	
	public Guardian() {
		super();
	}
	
	public Guardian(Id user, String name, String username, String user_type, String gender) {
		super(user, name, username, user_type, gender);
	}

	public Guardian(Id user, String name, String username, String user_type, String gender, String language, Boolean contact_person) {
		super(user, name, username, user_type, gender);
		
		this.language = language;
		this.contact_person = contact_person;
	}
	
	@JsonGetter("language")
	public String getLanguage() {
		return this.language;
	}
	
	@JsonSetter("language")
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@JsonGetter("contact_person")
	public Boolean getContactPerson() {
		return this.contact_person;
	}
	
	@JsonSetter("contact_person")
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
