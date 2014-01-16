package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db;

public class LocalUser {

	Integer userId;
	String name;
	String username;
	String userType;
	String gender;

	
	public Integer getUserId() {
		return this.userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getUserType() {
		return this.userType;
	}
	
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	public String getGender() {
		return this.gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
