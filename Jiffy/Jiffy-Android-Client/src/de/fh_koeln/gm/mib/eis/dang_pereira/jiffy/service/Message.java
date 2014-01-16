package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service;

public class Message {

	private String topic;
	private String msg;
	
	public Message(String topic, String msg) {
		this.topic = topic;
		this.msg = msg;
	}
	
	public String getTopic() {
		return this.topic;
	}
	
	
	public String getMsg() {
		return this.msg;
	}
}
