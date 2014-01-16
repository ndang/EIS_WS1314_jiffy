package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ExcuseMsg {
	@JsonProperty("date_from")
	public String date_from;
	
	@JsonProperty("date_to")
	public String date_to;
	
	
	public ExcuseMsg() {}
	
	public ExcuseMsg(String date_from, String date_to) {
		this.date_from = date_from;
		this.date_to = date_to;
	}
	
	
	@JsonGetter("date_from")
	public String getDateFrom() {
		return this.date_from;
	}

	@JsonSetter("date_from")
	public void setDateFrom(String date_from) {
		this.date_from = date_from;
	}
	
	
	@JsonGetter("date_to")
	public String getDateTo() {
		return this.date_to;
	}

	@JsonSetter("date_to")
	public void setDateTo(String date_to) {
		this.date_to = date_to;
	}
	
	
	public String toString() {
		
		return "{ \"date_from\": " + this.date_from + ", \"date_to\": " + this.date_to + " }";
	}
}
