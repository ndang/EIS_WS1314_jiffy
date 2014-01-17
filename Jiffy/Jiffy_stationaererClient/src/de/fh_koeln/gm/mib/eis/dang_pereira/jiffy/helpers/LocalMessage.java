package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.helpers;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;

public class LocalMessage {

	private Integer userId;
	private boolean unread;
	private String name;
	private String text;
	private Message msgStruct;
	
	
	public Integer getUserId() {
		return this.userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	public boolean getUnread() {
		return this.unread;
	}
	
	public void setUnread(boolean unread) {
		this.unread = unread;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
	public Message getMsgStruct() {
		return this.msgStruct;
	}
	
	public void setMsgStruct(Message msgStruct) {
		this.msgStruct = msgStruct;
	}
	
	
}
