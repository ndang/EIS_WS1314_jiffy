package de.fh_koeln.gm.mib.eis.dang_pereira.activities;


import com.example.jiffy_android.R;

import de.fh_koeln.gm.mib.eis.dang_pereira.helpers.Group;
import de.fh_koeln.gm.mib.eis.dang_pereira.helpers.MyExpandableListAdapter;
import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.msg_struct.Message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

public class MsgWrite extends Activity {

	SparseArray<Group> groups = new SparseArray<Group>();
	private EditText msg;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writemessages);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		createReceivers();
		ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView_receivers);
		MyExpandableListAdapter adapter = new MyExpandableListAdapter(this,
				groups);
		listView.setAdapter(adapter);
		
		this.msg = (EditText) findViewById(R.id.editText_msg);
	}

	public void createReceivers() {
		Group group = new Group("Empfänger");
		for (int i = 0; i < 3; i++) {
			group.children.add("Person " + i);
		}
		groups.append(0, group);
	}

	public void send(View view) {
		String text = msg.getText().toString();
		
		Message newMsg = new Message();
		
//		newMsg.from_user_id= Integer.valueOf(new Id() );
		
		Log.d("mylog",text);
		msg.setText("");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
