package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class GuardianMsg {
	@JsonProperty("msg_subtype")
	public String msg_subtype;
	
	@JsonProperty("excuse")
	public ExcuseMsg excuse;
	
	@JsonProperty("grade_ack")
	public Id grade_ack;
	
	
	public GuardianMsg() {}
	
	public GuardianMsg(String msg_subtype, ExcuseMsg excuse, Id grade_ack) {
		this.msg_subtype	= msg_subtype;
		this.excuse			= excuse;
		this.grade_ack		= grade_ack;
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
	
	
	@JsonGetter("grade_ack")
	public Id getGradeAck() {
		return this.grade_ack;
	}
	
	@JsonSetter("grade_ack")
	public void setGradeAck(Id grade_ack) {
		this.grade_ack = grade_ack;
	}
	
	
	public String toString() {
		return "{ \"msg_subtype\": " + this.msg_subtype + ", \"excuse\": " + this.excuse + ", \"grade_ack\": " + this.grade_ack + " }";
	}
}