package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities.MainActivity;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service.MQTTService;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils.PrivateBroadcast;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils.ResourceCallback;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils.ResourceGetter;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
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
			finish();
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
					//Intent i = new Intent(self, MainActivity.class);
					//startActivity(i);
					//finish();
					
					String username = txtName.getText().toString();
					String password = txtPass.getText().toString();
					
					Log.d(Config.TAG, username + " - " + password);
					
					if(username.isEmpty() || password.isEmpty()) {
						Toast.makeText(self, "Ungültige Anmeldeinformationen", Toast.LENGTH_LONG).show();
					} else {
						SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
						spref.edit()
							.putString("username", username)
							.putString("password", password)
						.commit();
						
						pd = ProgressDialog.show(self, "Anmeldung", "Bitte warten...");
						
						HashMap<String, String> headers = new HashMap<String, String>();
						headers.put("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP));
						
						ResourceGetter rg = new ResourceGetter(getApplication(), headers);
						
						rg.checkRESTConnection(new ResourceCallback() {
							
							@Override
							public void receive(boolean status, Bundle bdl) {

								if(status) {
									/* Starte den Hintergrundservice */
									startService();
								}
								else {
									Toast.makeText(self, "Verbindung konnte nicht hergestellt werden!", Toast.LENGTH_LONG).show();
								}
								
								pd.dismiss();
							}
						});
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
    		/* Auf Meldungen horchen, die über den Status der Verbindung zum Broker informieren */
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
			
			if(pd != null)
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
					
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP));
					
					ResourceGetter rg = new ResourceGetter(getApplication(), headers);
					
					/* Zu abonnierende Topics vom REST-Endpoint liefern lassen */
					/* Eine schmutziger schmutziger Art das zu lösen; AAALL THE CALLBACKS!!! */
					rg.getTopicsToSubscribe(username, new ResourceCallback() {
						
						@Override
						public void receive(boolean status, Bundle bdl) {
							
							if(status && bdl.containsKey("topicsToSubscribe")) {
								String[] topicsToSubscribe = bdl.getStringArray("topicsToSubscribe");
								PrivateBroadcast.broadcastTopicsToSubscribe(self, topicsToSubscribe);
							}
							else {
								Log.d(Config.TAG, "Whoooppsss!");
							}
						}
					});
					
					Log.d(Config.TAG, "OnStatus");
					
					/* ActivityMain aufrufen! */
					Intent i = new Intent(self, MainActivity.class);
					startActivity(i);
					finish();
				}
				
			}
		}
	}
}
