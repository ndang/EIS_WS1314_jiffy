package de.fh_koeln.gm.mib.eis.dang_pereira.activities;

import com.example.jiffy_android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

}
