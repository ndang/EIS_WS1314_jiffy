package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InfoMsg {
	@JsonProperty("class_broadcast")
	public Boolean class_broadcast;
	
	@JsonProperty("desc_date")
	public String desc_date;
	
	
	
	
	
	public String toString() {
		return "{ \"class_broadcast\": " + this.class_broadcast + ", \"desc_date\": " + this.desc_date + " }";
	}
}
