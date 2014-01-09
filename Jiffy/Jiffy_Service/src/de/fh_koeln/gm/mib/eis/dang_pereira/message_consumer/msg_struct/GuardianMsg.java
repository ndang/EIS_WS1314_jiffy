package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class GuardianMsg {
	@JsonProperty("msg_subtype")
	public String msg_subtype;
	
	@JsonProperty("excuse")
	public ExcuseMsg excuse;
	
	
	public GuardianMsg() {}
	
	public GuardianMsg(String msg_subtype, ExcuseMsg excuse) {
		this.msg_subtype = msg_subtype;
		this.excuse = excuse;
	}
	
	
	@JsonGetter("msg_subtype")
	public String getMsgSubType() {
		return this.msg_subtype;
	}

	@JsonSetter("msg_subtype")
	public void setMsgSubType(String msg_subtype) {
		this.msg_subtype = msg_subtype;
	}
	
	
	@JsonGetter("excuse")
	public ExcuseMsg getExcuse() {
		return this.excuse;
	}
	
	@JsonSetter("excuse")
	public void setExcuse(ExcuseMsg excuse) {
		this.excuse = excuse;
	}
	
	
	public String toString() {
		return "{ \"msg_subtype\": " + this.msg_subtype + ", \"excuse\": " + this.excuse + " }";
	}
}