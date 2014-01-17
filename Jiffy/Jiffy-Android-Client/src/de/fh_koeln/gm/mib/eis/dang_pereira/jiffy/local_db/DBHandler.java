package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db;

import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.InfoMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHandler {

	private SQLiteDatabase db;
	private JiffyLocalSQLiteHelper sqlHelper;
	
	public DBHandler(Context ctx) {
		sqlHelper = new JiffyLocalSQLiteHelper(ctx);
		
	}
	
	public void open() {
		db = sqlHelper.getWritableDatabase();
	}
	

	public boolean createUser(User user) {
		
		Integer userId = user.getUser().getID();
		String name = user.getName();
		String username = user.getUsername();
		String userType = user.getUserType();
	
		
		ContentValues values = new ContentValues();
		values.put("user_id", userId);
		values.put("name", name);
		values.put("username", username);
		values.put("type", userType);
		
		try{
			db.insertOrThrow("User", null, values);
			return true;
		}
		catch(Exception e) {
			Log.e(Config.TAG, "INSERT-EXCEPTION: " + e.getMessage());
		}
			
		return false;
	}
	
	
	public ArrayList<LocalUser> getUsers() {
		ArrayList<LocalUser> uList = new ArrayList<LocalUser>();
	
		String stmt = "SELECT user_id, name, username, type FROM  User ";
		
		Cursor c = null;
		
		try{
			c = db.rawQuery(stmt, null);
		}
		catch(Exception e) {
			Log.e(Config.TAG, "SELECT-EXCEPTION: " + e.getMessage());
		}
		
		
		if(c != null && c.moveToFirst()) {
			do {
				LocalUser lu = cursorToUser(c);
				uList.add(lu);
			} while(c.moveToNext());
		}
		
		return uList;
	}
	
	
	
	public boolean createMsg(Message msg) {
		
		
		String msgUuid = msg.getMsgUUID();
		int fromUserId = msg.getFromUserId().getID().intValue();
		String msgText = msg.getMsgText();
		String msgSubject = msg.getMsgSubject();
		String msgType = msg.getMsgType();
		String msgSubtype = "";
		String msgSubtext = "";
		String date = msg.getMsgSendDate();
		
		
		if(msgType.equalsIgnoreCase("school")) {
			msgSubtype = msg.getSchool().getMsgSubType();
			
			if(msgSubtype.equalsIgnoreCase("info") && msg.getSchool().getInfo() != null) {
				InfoMsg iMsg = msg.getSchool().getInfo();
				
				if(iMsg.getClassBroadcast() == true) {
					msgSubtext = "Klassennachricht!";
				}
			}
			else if(msgSubtype.equalsIgnoreCase("grade")) {
				msgSubtext = "Neue Note!";
			}
			else if(msgSubtype.equalsIgnoreCase("excuse_ack")) {
				msgSubtext = "Entschuldigung bestätigt!";
			}
			else
				return false;
		}
		else 
			return false;

		
		ContentValues values = new ContentValues();
		values.put("msg_uuid", msgUuid);
		values.put("user_id", fromUserId);
		values.put("msg", msgText);
		values.put("msg_subject", msgSubject);
		values.put("msg_type", msgType);
		values.put("msg_subtype", msgSubtype);
		values.put("msg_subtext", msgSubtext);
		values.put("received_date", date);
		values.put("unread", 0);
		
		Log.d(Config.TAG, msgUuid + " - " + fromUserId);
		
		try{
			db.insertOrThrow("Message", null, values);
			return true;
		}
		catch(Exception e) {
			Log.e(Config.TAG, "INSERT-EXCEPTION: " + e.getMessage());
		}
		
		return false;
	}
	
	
	public ArrayList<LocalMessage> getMessages() {
		
		ArrayList<LocalMessage> mList = new ArrayList<LocalMessage>();
		
		String stmt = "SELECT m.msg, m.msg_subject, m.msg_type, m.msg_subtype, m.msg_subtext, m.received_date, m.user_id, m.unread, u.name FROM Message m LEFT JOIN User u ON m.user_id = u.user_id " + 
				"ORDER BY datetime(m.received_date) DESC";
		
		Cursor c = null;
		
		try{
			c = db.rawQuery(stmt, null);
		}
		catch(Exception e) {
			Log.e(Config.TAG, "SELECT-EXCEPTION: " + e.getMessage());
		}
		
		
		if(c != null && c.moveToFirst()) {
			do {
				LocalMessage lm = cursorToMessage(c);
				mList.add(lm);
			} while(c.moveToNext());
		}
		
		return mList;
	}
	
	
	
	public ArrayList<LocalMessage> getUnreadMessages() {
		
		ArrayList<LocalMessage> mList = new ArrayList<LocalMessage>();
		
		String stmt = "SELECT m.msg, m.msg_subject, m.msg_type, m.msg_subtype, m.msg_subtext, m.received_date, m.user_id, m.unread, u.name FROM Message m LEFT JOIN User u ON m.user_id = u.user_id " + 
				"ORDER BY datetime(m.received_date) DESC " +
				"WHERE m.unread = 0";
		
		Cursor c = null;
		
		try{
			c = db.rawQuery(stmt, null);
		}
		catch(Exception e) {
			Log.e(Config.TAG, "SELECT-EXCEPTION: " + e.getMessage());
		}
		
		
		if(c != null && c.moveToFirst()) {
			do {
				LocalMessage lm = cursorToMessage(c);
				mList.add(lm);
			} while(c.moveToNext());
		}
		
		return mList;
	}
	
	
	
	public int getNumUnreadMsgs() {
		int num = 0;
		
		String stmt = "SELECT COUNT(*) FROM Message WHERE unread = 0";
		
		Cursor c = null;
		
		try{
			c = db.rawQuery(stmt, null);
		}
		catch(Exception e) {
			Log.e(Config.TAG, "SELECT-EXCEPTION: " + e.getMessage());
		}
		
		
		if(c != null && c.moveToFirst()) {
			
		}
		
		return num;
	}
	
	
	
	private LocalMessage cursorToMessage(Cursor c) {
		LocalMessage lm = new LocalMessage();
		
		lm.setMsg(c.getString(0));
		lm.setSubject(c.getString(1));
		lm.setType(c.getString(2));
		lm.setSubType(c.getString(3));
		lm.setSubtext(c.getString(4));
		lm.setDate(c.getString(5));
		lm.setUserId(c.getInt(6));
		lm.setRead(c.getInt(7));
		lm.setName(c.getString(8));
		
		Log.d(Config.TAG, "sub: " + lm.getSubject());
		
		return lm;
	}
	
	
	private LocalUser cursorToUser(Cursor c) {
		LocalUser lu = new LocalUser();
		
		lu.setUserId(c.getInt(0));
		lu.setName(c.getString(1));
		lu.setUsername(c.getString(2));
		lu.setUserType(c.getString(3));
		
		return lu;
	}
	
}
