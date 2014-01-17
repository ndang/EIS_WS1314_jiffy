package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities;

import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db.DBHandler;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.local_db.LocalMessage;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Id;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.Message;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.msg_structs.SchoolMsg;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.User;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	private TextView tvUnreadMsgCount;
	
	
	private MessageBroadCastReceiver mMessageReceiver = new MessageBroadCastReceiver();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		tvUnreadMsgCount = (TextView) findViewById(R.id.lbl_countMsg);
		
		
		
		DBHandler dbh = new DBHandler(getApplicationContext());
		
		dbh.open();		

		/*
		de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.Id userId =
				new de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_res_structs.Id(1, "blaa", null); 
		User u = new User(userId, "Peter", "peter", "GUARDIAN", "MALE");
		
		dbh.createUser(u);
		
		ArrayList<LocalUser> uList = dbh.getUsers();
		
		for(LocalUser lu: uList) {
			Log.d(Config.TAG, "SQLite: " + lu.getName());
		}
		*/
		
		/*
		Message m = new Message();
		m.setMsgUUID("UUID2");
		m.setFromUserId(new Id(1, "/user/2"));
		m.setMsgText("Bllaaaa");
		m.setMsgSubject("Subj");
		m.setMsgType("school");
		m.setMsgSendDate("2004-01-17 00:26:30");
		m.setMsgRelevance(0);
		m.setStudentId(new Id(2, "/user/2"));
		
		SchoolMsg s = new SchoolMsg();
		s.setMsgSubType("grade");
		s.setGrade(new Id(3, "/student/3/grade/3"));
		m.setSchool(s);

		dbh.createMsg(m);
		
		
		ArrayList<LocalMessage> lmList = dbh.getMessages();
		
		for(LocalMessage lm: lmList) {
			Log.d(Config.TAG, "Msg: " + lm.getName() + " - " + lm.getSubject() + " - " + lm.getDate());
		}
		*/
		
	}

	
    public void bindBroadCastReceivers(boolean bind) {
    	if(bind) {
    		/* Nachrichtenbenachrichtigung */
    		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
  			      new IntentFilter(Config.MESSAGE_AVAIL_FILTER));
  		
    	} else {
    		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	}
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void seeEducation(View view) {
		Intent intent = new Intent(this, EducationOverview.class);
		// EditText editText = (EditText) findViewById(R.id.edit_message);
		// String message = editText.getText().toString();
		// intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void sendMsg(View view) {
		Intent intent = new Intent(this, MsgWrite.class);
		startActivity(intent);

	}

	public void readMsg(View view) {
		Intent intent = new Intent(this, MsgRead.class);
		startActivity(intent);

	}

    @Override
    public void onResume() {
    	super.onResume();
    }
	
	
    public class MessageBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String topic = intent.getStringExtra("topic");
			String msg = intent.getStringExtra("msg");
			
			Log.d(Config.TAG, topic + " | " + msg);
		}
	}
	
}
