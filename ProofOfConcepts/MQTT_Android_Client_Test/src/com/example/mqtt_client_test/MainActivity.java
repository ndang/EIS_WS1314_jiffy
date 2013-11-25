package com.example.mqtt_client_test;



import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

	public static final String SERVICE_CLASSNAME = "com.example.mqtt_client_test.MQTTService";
	private MessageBroadCastReceiver mMessageReceiver = new MessageBroadCastReceiver();
	
	private SharedPreferences sharedPrefs;
	
	private TextView tv;
	private Button b;
	private Button erase;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        tv = (TextView)findViewById(R.id.message_output);
        b = (Button)findViewById(R.id.handle_service);
        erase = (Button)findViewById(R.id.erase_button);
        
        erase.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv.setText("");
		    	SharedPreferences.Editor editor = sharedPrefs.edit();
		    	editor.clear();
		    	editor.commit();
			}
		});
        
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(Config.MESSAGE_AVAIL_FILTER));
        
		sharedPrefs = getPreferences(Context.MODE_PRIVATE);
		tv.setText(sharedPrefs.getString("messages", ""));
		
		doRestore(savedInstanceState);
        updateButton();
    }
    
    
    public void updateButton() {
    	
    	if(serviceIsRunning()) {
    		b.setText(R.string.stop_service);
    		b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					stopService();
					updateButton();
				}
			});
    	}else {
    		b.setText(R.string.start_service);
    		b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startService();
					updateButton();
				}
			});
    	}
    	
    }
    
    
    public void startService() {
    	Intent i = new Intent(this, MQTTService.class);
    	startService(i);
    }
    
    
    public void stopService() {
    	Intent i = new Intent(this, MQTTService.class);
    	stopService(i);
    }
    
    public boolean serviceIsRunning() {
    	ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    	
    	for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)) {
    		if(service.service.getClassName().equals(SERVICE_CLASSNAME))
    			return true;
    	}
    	
    	return false;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString("messages", tv.getText().toString());
    	editor.commit();
    	
    	broadcastActivityStatus(false);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	tv.setText(sharedPrefs.getString("messages", ""));
    	Log.d(Config.TAG, "Messages set! -> " + sharedPrefs.getString("messages", ""));
    	broadcastActivityStatus(true);
    }
	
    @Override
    public void onDestroy() {
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		doRestore(savedInstanceState);
	}
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putString("messages", tv.getText().toString());
    }
    
    private void doRestore(Bundle state) {
    	if(state != null) {
    		tv.setText(state.getString("messages"));
    	}
    }
    
    public void broadcastActivityStatus(boolean active) {
    	  Intent intent = new Intent(Config.ACTIVITY_ACTIVE_FILTER);
    	  intent.putExtra("active", active);
    	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
    
    public void prependMsg(String topic, String msg) {
    	Log.d(Config.TAG, "Message appended!");
    	tv.setText(topic + ":" + msg + "\n" + tv.getText());
    }
    
    public class MessageBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String topic = intent.getStringExtra("topic");
			String msg = intent.getStringExtra("msg");
			prependMsg(topic, msg);
		}
	}
    
}
