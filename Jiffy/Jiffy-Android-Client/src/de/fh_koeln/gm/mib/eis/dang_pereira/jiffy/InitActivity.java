package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities.MainActivity;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service.MQTTService;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitActivity extends Activity {

	private EditText txtName;
	private EditText txtPass;
	private Button btnLogin;
	
	public ProgressDialog pd;
	
	private ServiceStatusBroadCastReceiver mServiceStatusReceiver = new ServiceStatusBroadCastReceiver();
	
	private Activity self;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.self = this;
		boolean loggedIn = false;
		
		SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
		
		/* Schauen, ob die Applikation bereits angemeldet ist */
		if(spref.contains("loggedIn")) {
			loggedIn = spref.getBoolean("loggedIn", false);
		}
		
		Log.d(Config.TAG, "loggedIn: " +loggedIn);
		
		if(loggedIn == true) {
			// Sofern bereits angemeldet, gehe zur MainActivity
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
		else {
			// wenn nicht, dann zeige das Login-Layout
			setContentView(R.layout.login_layout);
			
			txtName = (EditText) findViewById(R.id.loginTxtName);
			txtPass = (EditText) findViewById(R.id.loginTxtPass);
			
			
			/* Konfiguration initialisieren */
			Config cfg = Config.getInstance(getApplicationContext());
			
			btnLogin = (Button)findViewById(R.id.loginBtnLogin);
			btnLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO: rückgängig machen
					Intent i = new Intent(self, MainActivity.class);
					startActivity(i);
					finish();
					
					String username = txtName.getText().toString();
					String password = txtPass.getText().toString();
					
					Log.d(Config.TAG, username + " - " + password);
					
					if(username.isEmpty() || password.isEmpty()) {
						Toast.makeText(self, "UngÃ¼ltige Anmeldeinformationen", Toast.LENGTH_LONG).show();
					} else {
						SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
						spref.edit()
							.putString("username", username)
							.putString("password", password)
						.commit();
						
						Log.d(Config.TAG, "vor!");
						
						pd = ProgressDialog.show(self, "Anmeldung", "Bitte warten...");
						
						/* Starte den Hintergrundservice */
						startService();
					}
				}
			});
		}
		/* BroadcastReceiver binden */
		bindBroadCastReceivers(true);
	}

	
	
    public void startService() {
    	Intent i = new Intent(this, MQTTService.class);
    	startService(i);
    }
    
    
    public void stopService() {
    	Intent i = new Intent(this, MQTTService.class);
    	stopService(i);
    }
	
    
    public void bindBroadCastReceivers(boolean bind) {
    	if(bind) {
    		/* Auf Meldungen horchen, die Ã¼ber den Status der Verbindung zum Broker informieren */
    		LocalBroadcastManager.getInstance(this).registerReceiver(mServiceStatusReceiver,
  			      new IntentFilter(Config.SERVICE_CON_STATUS_FILTER));
    	} else {
    		LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceStatusReceiver);
    	}
    }
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.init, menu);
		return true;
	}
	
	
	public class ServiceStatusBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		
			boolean status = intent.getBooleanExtra("status", false);
			
			Log.d(Config.TAG, "Verbindung zum Broker: " + status);
			
			pd.dismiss();
			
			/* Je nachdem ob sich zum System verbunden werden konnte,
			 * wird entweder das Login-Layout gezeigt oder weiter zum MainActivity gegangen */
			if(!status) {
				/* Konnte nicht verbunden werden */
				stopService();
				
				SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
				
				spref.edit()
					.remove("username")
					.remove("password")
				.commit();
				
				Toast.makeText(self, "Verbindung konnte nicht hergestellt werden!", Toast.LENGTH_LONG).show();
			}
			else {
				/* Konnte verbinden; somit zur MainActivity */
				
				SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
				String username = "";
				String password = "";
				
				/* Anmeldedaten aus den globalen App-Einstellungen holen */
				username = spref.getString("username", "");
				password = spref.getString("password", "");
				
				if(username.isEmpty() || password.isEmpty()) {
					Log.e(Config.TAG, "Keine Benutzerdaten gegeben! Zu voreilig? ;)");
				} else {
					Intent i = new Intent(self, MainActivity.class);
					startActivity(i);
				}
				
			}
		}
	}
}
