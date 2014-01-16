package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class InfoMsg {
	@JsonProperty("class_broadcast")
	public Boolean class_broadcast;
	
	@JsonProperty("desc_date")
	public String desc_date;
	
	
	public InfoMsg() { }
	
	public InfoMsg(Boolean class_broadcast, String desc_date) {
		this.class_broadcast = class_broadcast;
		this.desc_date = desc_date;
	}
	
	
	@JsonGetter("class_broadcast")
	public Boolean getClassBroadcast() {
		return this.class_broadcast;
	}

	@JsonSetter("class_broadcast")
	public void setClassBroadcast(Boolean class_broadcast) {
		this.class_broadcast = class_broadcast;
	}
	

	@JsonGetter("desc_date")
	public String getDescDate() {
		return this.desc_date;
	}

	@JsonSetter("desc_date")
	public void setDescDate(String desc_date) {
		this.desc_date = desc_date;
	}
	
	
	public String toString() {
		return "{ \"class_broadcast\": " + this.class_broadcast + ", \"desc_date\": " + this.desc_date + " }";
	}
}
