package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TabHost;

public class EducationOverview extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.education_overview);
		
        
		TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
	    tabs.setup();
	    
	    TabHost.TabSpec spec=tabs.newTabSpec("tag1");

	    spec.setContent(R.id.child1);
	    spec.setIndicator("Alice");
	    tabs.addTab(spec);

	    spec=tabs.newTabSpec("tag2");
	    spec.setContent(R.id.child2);
	    spec.setIndicator("Bob");
	    tabs.addTab(spec);
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
