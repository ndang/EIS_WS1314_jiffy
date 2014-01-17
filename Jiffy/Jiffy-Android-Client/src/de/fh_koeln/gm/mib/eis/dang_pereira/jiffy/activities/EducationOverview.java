package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TabHost;

public class EducationOverview extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.education_overview);

		// children tabhost
		TabHost tabs_children = (TabHost) findViewById(android.R.id.tabhost);
		tabs_children.setup();

		TabHost.TabSpec spec = tabs_children.newTabSpec("tag1");

		spec.setContent(R.id.child1);
		spec.setIndicator("Alice");
		tabs_children.addTab(spec);

		spec = tabs_children.newTabSpec("tag2");
		spec.setContent(R.id.child2);
		spec.setIndicator("Bob");
		tabs_children.addTab(spec);

		// child1 subjects tabhost
		TabHost tabs_child1 = (TabHost) findViewById(R.id.tabhost_child1_subjects);
		tabs_child1.setup();

		TabHost.TabSpec spec1 = tabs_child1.newTabSpec("tag1");

		spec1.setContent(R.id.child1_ma);
		spec1.setIndicator("Mathematik");
		tabs_child1.addTab(spec1);

		spec1 = tabs_child1.newTabSpec("tag2");
		spec1.setContent(R.id.child1_en);
		spec1.setIndicator("Englisch");
		tabs_child1.addTab(spec1);
		
		spec1 = tabs_child1.newTabSpec("tag3");
		spec1.setContent(R.id.child1_de);
		spec1.setIndicator("Deutsch");
		tabs_child1.addTab(spec1);
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
