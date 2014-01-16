package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db;

public class LocalMessage {

	private String msg;
	private String subject;
	private String type;
	private String subtype;
	private String subtext;
	private Integer user_id;
	private String name;
	private String date;
	private Integer read;
	
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return this.msg;
	}
	
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	
	public void setSubType(String subtype) {
		this.subtype = subtype;
	}
	
	public String getSubType() {
		return this.subtype;
	}
	
	
	public void setSubtext(String subtext) {
		this.subtext = subtext;
	}
	
	public String getSubtext() {
		return this.subtext;
	}
	
	
	public void setUserId(Integer user_id) {
		this.user_id = user_id;
	}

	
	public Integer getUserId() {
		return this.user_id;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getName() {
		return this.name;
	}
	
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return this.date;
	}
	
	
	public void setRead(Integer read) {
		this.read = read;
	}
	
	public Integer getRead() {
		return this.read;
	}
	
}
