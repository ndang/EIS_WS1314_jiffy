package com.example.mqtt_client_test;

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
