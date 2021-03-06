package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/* 
 * Eigenschaften, die im JSON-Dokuemnt existieren, aber nich in der Klasse, nicht ignorieren.
 *	Als Beispiel: Ein guardian ist vom Obertyp user, aber nicht vom Typ guardian.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class Teacher extends User {
	
	
	public Teacher() {
		super();
	}
	
	public Teacher(Id user, String name, String username, String user_type, String gender) {
		super(user, name, username, user_type, gender);
	}

	public String toString() {
		
		return super.toString();
	}
}
