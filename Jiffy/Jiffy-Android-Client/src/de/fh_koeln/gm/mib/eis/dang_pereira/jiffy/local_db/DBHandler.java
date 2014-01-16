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
		String user_type = user.getUserType();
	
		String stmt = "INSERT INTO User (user_id, name, username, type) " +
				"VALUES " + 
				"(" + userId + ", '" + name +"', '" + username + "', '" + user_type + "')";
		
		Log.d(Config.TAG, "before!");
		
		db.execSQL(stmt);
		
		Log.d(Config.TAG, "after!");
		
		return true;
	}
	
	
	public ArrayList<LocalUser> getUsers() {
		ArrayList<LocalUser> uList = new ArrayList<LocalUser>();
	
		String stmt = "SELECT user_id, name, username, type FROM  User ";
		
		Cursor c = db.rawQuery(stmt, null);
		
		c.moveToFirst();
		
		while(!c.isAfterLast()) {
			LocalUser lu = cursorToUser(c);
			uList.add(lu);
		}
	
		return uList;
	}
	
	
	
	public boolean createMsg(Message msg) {
		
		
		String msg_uuid = msg.getMsgUUID();
		int from_user_id = msg.getFromUserId().getID().intValue();
		String msg_text = msg.getMsgText();
		String msg_type = msg.getMsgType();
		String msg_subtype = "";
		String msg_subtext = "";
		String date = msg.getMsgSendDate();
		
		
		
		if(msg_type.equalsIgnoreCase("school")) {
			msg_subtype = msg.getSchool().getMsgSubType();
			
			if(msg_subtype.equalsIgnoreCase("info") && msg.getSchool().getInfo() != null) {
				InfoMsg iMsg = msg.getSchool().getInfo();
				
				if(iMsg.getClassBroadcast() == true) {
					msg_subtext = "Klassennachricht!";
				}
			}
			else if(msg_subtype.equalsIgnoreCase("grade")) {
				msg_subtext = "Neue Note!";
			}
			else if(msg_subtype.equalsIgnoreCase("excuse_ack")) {
				msg_subtext = "Entschuldigung bestätigt!";
			}
			else
				return false;
		}
		else 
			return false;

		
		
		ContentValues values = new ContentValues();
		values.put("msg_uuid", msg_uuid);
		values.put("user_id", from_user_id);
		values.put("msg", msg_text);
		values.put("msg_type", msg_type);
		values.put("msg_subtype", msg_subtype);
		
		String stmt = "INSERT INTO Message (msg_uuid, user_id, msg, msg_type, msg_subtype, msg_subtext, received_date, read) " +
				"VALUES " + 
				"( " + msg_uuid + ", " + from_user_id + ", '" + msg_text + "', '" + msg_type + "', '" + msg_subtype + "', '" + msg_subtext + "', " + date + ", 0)";
				
		
		db.execSQL(stmt);
		
		
		return true;
	}
	
	
	public ArrayList<LocalMessage> getMessages() {
		
		ArrayList<LocalMessage> mList = new ArrayList<LocalMessage>();
		
		String stmt = "SELECT m.msg, m.msg_type, m.msg_subtype, m.msg_subtext, m.received_date, m.user_id, m.read, u.name FROM Message m LEFT JOIN User u ON m.user_id = u.user_id " + 
				"ORDER BY date(m.received_date) DESC";
		
		Cursor c = db.rawQuery(stmt, null);
		
		if(c.moveToFirst()) {
			do {
				LocalMessage lm = cursorToMessage(c);
				mList.add(lm);
			} while(c.moveToNext());
		}
		
		return mList;
	}
	
	
	
	private LocalMessage cursorToMessage(Cursor c) {
		LocalMessage lm = new LocalMessage();
		
		lm.setMsg(c.getString(0));
		lm.setType(c.getString(1));
		lm.setSubType(c.getString(2));
		lm.setSubtext(c.getString(3));
		lm.setDate(c.getString(4));
		lm.setUserId(c.getInt(5));
		lm.setRead(c.getInt(6));
		lm.setName(c.getString(7));
		
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
