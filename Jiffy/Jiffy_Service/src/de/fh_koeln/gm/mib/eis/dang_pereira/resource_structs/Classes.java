package de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Classes {

	@JsonProperty("classes")
	ArrayList<SClass> classes;
	
	public String toString() {
		
		String tmp = "{\"classes\": [\r\n";
		
		for(SClass c: this.classes) {
			tmp += c + ",\r\n";
		}
		
		return tmp + "]}";
	}
}