package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class SchoolMsg {
	@JsonProperty("msg_subtype")
	public String msg_subtype;
	
	@JsonProperty("grade")
	public Id grade;
	
	@JsonProperty("info")
	public InfoMsg info;
	
	
	@JsonGetter("msg_subtype")
	public String getMsgSubType() {
		return this.msg_subtype;
	}

	@JsonSetter("msg_subtype")
	public void setMsgSubType(String msg_subtype) {
		this.msg_subtype = msg_subtype;
	}
	
	
	@JsonGetter("info")
	public InfoMsg getInfo() {
		return this.info;
	}

	@JsonSetter("info")
	public void setinfo(InfoMsg info) {
		this.info = info;
	}
	
	
	public String toString() {
		return "{ \"msg_subtype\": " + this.msg_subtype + ", \"grade\": " + this.grade + ", \"info\": " + this.info + " }";
	}
}
