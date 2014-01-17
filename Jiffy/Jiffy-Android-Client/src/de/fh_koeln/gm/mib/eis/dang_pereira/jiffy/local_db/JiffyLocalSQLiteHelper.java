package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JiffyLocalSQLiteHelper extends SQLiteOpenHelper {
	
	public final static String DB_NAME = "jiffy.db";
	public final static int DB_VERSION = 2;
	
	private final static String DB_CREATE_USER = "CREATE TABLE User (" + 
			"user_id INTEGER PRIMARY KEY, " +
			"name TEXT NULL ," +
			"username TEXT NULL ," +
			"type TEXT NULL)";
	
	private final static String DB_CREATE_MSG = "CREATE TABLE Message (" + 
			"msg_uuid TEXT PRIMARY KEY , " +
			"user_id TEXT NULL ," +
			"msg TEXT NULL ," +
			"msg_subject TEXT NULL ," +
			"msg_type TEXT NULL , " +
			"msg_subtype TEXT NULL , " +
			"msg_subtext TEXT NULL , " +
			"received_date TEXT NULL ," +
			"unread INTEGER NOT NULL)";
	
	private final static String DB_CREATE_GRADES = "CREATE TABLE Grades ("+
			"grade_id INTEGER PRIMARY KEY," +
			"value NUMERIC NULL , " +
			"grade_weight INTEGER NULL , " +
			"date_given TEXT NULL , " +
			"comment TEXT NULL , " +
			"subject TEXT NULL , " +
			"student_id INTEGER NULL ," +
			"teacher_id INTEGER NULL)";
	
	
	public JiffyLocalSQLiteHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_USER);
		db.execSQL(DB_CREATE_MSG);
		db.execSQL(DB_CREATE_GRADES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS User");
		db.execSQL("DROP TABLE IF EXISTS Message");
		db.execSQL("DROP TABLE IF EXISTS Grades");
		onCreate(db);
	}

}
