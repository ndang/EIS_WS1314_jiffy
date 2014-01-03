package de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	public Integer from_user_id;
	
	@JsonProperty("to_user_id")
	public Integer to_user_id;
	
	@JsonProperty("student_ref")
	public Integer student_ref;
	
	@JsonProperty("school")
	public School school;
	
	@JsonProperty("guardian")
	public Guardian guardian;
	
	
	
	public class School {
		@JsonProperty("msg_subtype")
		public String msg_subtype;
		
		@JsonProperty("grade")
		public Grade grade;
		
		@JsonProperty("info")
		public Info info;
		
		public class Grade {
			@JsonProperty("grade_id")
			public Integer grade_id;
			
			public String toString() {
				return "{ 'grade_id': '" + this.grade_id + "}";
			}
		}
		
		public class Info {
			@JsonProperty("class_broadcast")
			public Boolean class_broadcast;
			
			@JsonProperty("desc_date")
			public String desc_date;
			
			public String toString() {
				return "{ 'class_broadcast': " + this.class_broadcast + ", 'desc_date': " + this.desc_date + " }";
			}
		}
		
		public String toString() {
			return "{ 'msg_subtype': " + this.msg_subtype + ", 'grade': " + this.grade + ", 'info': " + this.info + " }";
		}
	}
	
	public class Guardian {
		@JsonProperty("msg_subtype")
		public String msg_subtype;
		
		@JsonProperty("excuse")
		public Excuse excuse;
		
		public class Excuse {
			@JsonProperty("date_from")
			public String date_from;
			
			@JsonProperty("date_to")
			public String date_to;
			
			public String toString() {
				
				return "{ 'date_from': " + this.date_from + ", 'date_to': " + this.date_to + " }";
			}
		}
		
		public String toString() {
			return "{ 'msg_subtype': " + this.msg_subtype + ", 'excuse': " + this.excuse + " }";
		}
	}
	
	
	public String toString() {
		return "{\t'msg_type': " + this.msg_type + ",\n\t" +
				"'msg_id': "+ this.msg_id + "\n\t" +
				"'msg_ref': "+ this.msg_ref + "\n\t" +
				"'msg_subject': "+ this.msg_subject + "\n\t" +
				"'msg_text': "+ this.msg_text + "\n\t" +
				"'msg_send_date': "+ this.msg_send_date + "\n\t" +
				"'from_user_id': "+ this.from_user_id + "\n\t" +
				"'to_user_id': "+ this.to_user_id + "\n\t" +
				"'student_ref': "+ this.student_ref + "\n\t" +
				"'school': "+ this.school + "\n\t" +
				"'guardian': "+ this.guardian + "\n}";
	}
	
}
