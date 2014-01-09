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
	
	@JsonProperty("excuse_ack")
	public ExcuseMsg excuse_ack;
	
	
	public SchoolMsg() { }
	
	public SchoolMsg(String msg_subtype, Id grade, InfoMsg info, ExcuseMsg excuse_ack) {
		this.msg_subtype	= msg_subtype;
		this.grade			= grade;
		this.info			= info;
		this.excuse_ack		= excuse_ack;
	}
	
	
	@JsonGetter("msg_subtype")
	public String getMsgSubType() {
		return this.msg_subtype;
	}

	@JsonSetter("msg_subtype")
	public void setMsgSubType(String msg_subtype) {
		this.msg_subtype = msg_subtype;
	}
	
	
	@JsonGetter("grade")
	public Id getGrade() {
		return this.grade;
	}

	@JsonSetter("grade")
	public void setGrade(Id grade) {
		this.grade = grade;
	}
	
	
	@JsonGetter("info")
	public InfoMsg getInfo() {
		return this.info;
	}

	@JsonSetter("info")
	public void setInfo(InfoMsg info) {
		this.info = info;
	}
	
	
	@JsonGetter("excuse_ack")
	public ExcuseMsg getExcuseAck() {
		return this.excuse_ack;
	}

	@JsonSetter("excuse_ack")
	public void setExcuseAck(ExcuseMsg excuse_ack) {
		this.excuse_ack = excuse_ack;
	}
	
	
	public String toString() {
		return "{ \"msg_subtype\": " + this.msg_subtype + ", \"grade\": " + this.grade + ", \"info\": " + this.info + ", \"excuse_ack\": " + this.excuse_ack + " }";
	}
}
