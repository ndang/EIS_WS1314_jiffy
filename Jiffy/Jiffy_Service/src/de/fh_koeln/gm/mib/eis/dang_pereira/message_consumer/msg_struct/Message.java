package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Zielklasse für das Marshalling/Unmarshalling von Nachrichten im JSON-Datenformat für den Einsatz mit "Jackson"
 * 
 * @author dang_pereira
 *
 */
public class Message {
	
	@JsonProperty("msg_type")
	public String msg_type;
	
	@JsonProperty("msg_id")
	public Integer msg_id;
	
	@JsonProperty("msg_ref")
	public Integer msg_ref;
	
	@JsonProperty("msg_subject")
	public String msg_subject;
	
	@JsonProperty("msg_text")
	public String msg_text;
	
	@JsonProperty("msg_send_date")
	public String msg_send_date;
	
	@JsonProperty("from_user_id")
	public Id from_user_id;
	
	@JsonProperty("to_user_id")
	public Id to_user_id;
	
	@JsonProperty("student_id")
	public Id student_id;
	
	@JsonProperty("school")
	public SchoolMsg school;
	
	@JsonProperty("guardian")
	public GuardianMsg guardian;
	
	
	public Message() {}
	
	
	public Message(	String		msg_type,		Integer		msg_id,		Integer		msg_ref,
					String		msg_subject,	String		msg_text,	String		msg_send_date,
					Id			from_user_id,	Id			to_user_id, Id			student_id,
					SchoolMsg	school,			GuardianMsg guardian) {
		
		this.msg_type		= msg_type;
		this.msg_id			= msg_id;
		this.msg_ref		= msg_ref;
		this.msg_subject	= msg_subject;
		this.msg_text		= msg_text;
		this.msg_send_date	= msg_send_date;
		this.from_user_id	= from_user_id;
		this.to_user_id		= to_user_id;
		this.student_id		= student_id;
		this.school			= school;
		this.guardian		= guardian;
	}
	
	
	@JsonGetter("msg_id")
	public Integer getMsgID() {
		return this.msg_id;
	}

	@JsonSetter("msg_id")
	public void setMsgID(Integer msg_id) {
		this.msg_id = msg_id;
	}
	
	
	@JsonGetter("msg_type")
	public String getMsgType() {
		return this.msg_type;
	}

	@JsonSetter("msg_type")
	public void setMsgType(String msg_type) {
		this.msg_type = msg_type;
	}
	
	
	@JsonGetter("msg_ref")
	public Integer getMsgRef() {
		return this.msg_ref;
	}

	@JsonSetter("msg_ref")
	public void setMsgRef(Integer msg_ref) {
		this.msg_ref = msg_ref;
	}
	
	
	@JsonGetter("msg_subject")
	public String getMsgSubject() {
		return this.msg_subject;
	}

	@JsonSetter("msg_subject")
	public void setMsgSubject(String msg_subject) {
		this.msg_subject = msg_subject;
	}
	
	
	@JsonGetter("msg_text")
	public String getMsgText() {
		return this.msg_text;
	}

	@JsonSetter("msg_text")
	public void setMsgText(String msg_text) {
		this.msg_text = msg_text;
	}
	
	@JsonGetter("msg_send_date")
	public String getMsgSendDate() {
		return this.msg_send_date;
	}

	@JsonSetter("msg_send_date")
	public void setMsgSendDate(String msg_send_date) {
		this.msg_send_date = msg_send_date;
	}
	
	
	@JsonGetter("from_user_id")
	public Id getFromUserId() {
		return this.from_user_id;
	}

	@JsonSetter("from_user_id")
	public void setFromUserId(Id from_user_id) {
		this.from_user_id = from_user_id;
	}
	
	
	@JsonGetter("to_user_id")
	public Id getToUserId() {
		return this.to_user_id;
	}

	@JsonSetter("to_user_id")
	public void setToUserId(Id to_user_id) {
		this.to_user_id = to_user_id;
	}
	
	
	@JsonGetter("student_id")
	public Id getStudentId() {
		return this.student_id;
	}

	@JsonSetter("student_id")
	public void setStudentId(Id student_id) {
		this.student_id = student_id;
	}


	@JsonGetter("school")
	public SchoolMsg getSchool() {
		return this.school;
	}

	@JsonSetter("school")
	public void setSchool(SchoolMsg school) {
		this.school = school;
	}


	@JsonGetter("guardian")
	public GuardianMsg getGuardian() {
		return this.guardian;
	}

	@JsonSetter("guardian")
	public void setGuardian(GuardianMsg guardian) {
		this.guardian = guardian;
	}

	

	public String toString() {
		return "{\t\"msg_type\": " + this.msg_type + ",\n\t" +
				"\"msg_id\": "+ this.msg_id + "\n\t" +
				"\"msg_ref\": "+ this.msg_ref + "\n\t" +
				"\"msg_subject\": "+ this.msg_subject + "\n\t" +
				"\"msg_text\": "+ this.msg_text + "\n\t" +
				"\"msg_send_date\": "+ this.msg_send_date + "\n\t" +
				"\"from_user_id\": "+ this.from_user_id + "\n\t" +
				"\"to_user_id\": "+ this.to_user_id + "\n\t" +
				"\"student_ref\": "+ this.student_id + "\n\t" +
				"\"school\": "+ this.school + "\n\t" +
				"\"guardian\": "+ this.guardian + "\n}";
	}
	
}