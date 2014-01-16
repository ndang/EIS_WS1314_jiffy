package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class MsgRead extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readmessages);
		
        // Get the message from the intent
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        
//        // Create the text view
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText(message);
//        
//        // Set the text view as the activity layout
//        setContentView(textView);
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
