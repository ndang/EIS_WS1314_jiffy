package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Id {
	@JsonProperty("id")
	public Integer id;
	
	@JsonProperty("uri")
	public String uri;
	
	public String toString() {
		return "{ \"id\": " + this.id + ", \"uri\": " + this.uri + "}";
	}
}
