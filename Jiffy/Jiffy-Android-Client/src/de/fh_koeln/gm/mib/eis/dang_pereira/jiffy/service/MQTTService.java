package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import javax.net.SocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils.PrivateBroadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MQTTService extends Service {
	
	private Queue<ServiceMessage> msgStore = new LinkedList<ServiceMessage>();
	private MqttClient mqttClient;
	private boolean activityIsActive = true;
	private TopicsSubscribeBroadCastReceiver mTopicsSubscribeReceiver = new TopicsSubscribeBroadCastReceiver();
	private ServiceBroadCastReceiver mActivityReceiver = new ServiceBroadCastReceiver();
	private ArrayList<String> subscribedTopics = new ArrayList<String>();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		Config cfg = Config.getInstance();
		
		try {
			
			/* Je nachdem, kann auch die GeräteID genommen werden, dafür wird aber ein zusätzliche Berechtigung benötigt */
			String clientId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			
			/* Den eigentlichen MQTT-Client erzeugen und eine eindeutige Client-Id zuweisen */
			mqttClient = new MqttClient(cfg.broker.host + ":" + cfg.broker.port, clientId, new MemoryPersistence());
			
			Log.d(Config.TAG, cfg.broker.host + ":" + cfg.broker.port);
			
			SharedPreferences spref = getSharedPreferences("account", Context.MODE_PRIVATE);
			String username = "";
			String password = "";
			
			/* Anmeldedaten aus den globalen App-Einstellungen holen */
			username = spref.getString("username", "");
			password = spref.getString("password", "");

			Log.d(Config.TAG, username + " - " + password);
			
			if(username.isEmpty() || password.isEmpty()) {
				PrivateBroadcast.broadcastStatus(this, false, Config.SERVICE_CON_STATUS_FILTER);
			} else {
				MqttConnectOptions mqttOpts = new MqttConnectOptions();
				mqttOpts.setUserName(username);
				mqttOpts.setPassword(password.toCharArray());
				mqttOpts.setCleanSession(false);
				mqttOpts.setKeepAliveInterval(2000);
				mqttOpts.setConnectionTimeout(5000);
				
				SocketFactory sf = BrokerSSLSocketFactory.getSocketFactory(
												getResources().openRawResource(R.raw.broker), cfg.ssl.pass_ks,
												getResources().openRawResource(R.raw.client), cfg.ssl.pass_ts);
				mqttOpts.setSocketFactory(sf);
				
				mqttClient.connect(mqttOpts);
				
				Log.d(Config.TAG, "Verbindung zum Broker wurde hergestellt!");
				
				PrivateBroadcast.broadcastStatus(this, true, Config.SERVICE_CON_STATUS_FILTER);
				
				mqttClient.setCallback(new EventCallback(this));
				
				/* Erst jetzt sollte gesetzt werden, dass man angemeldet ist */
				spref.edit()
					.putBoolean("loggedIn", true)
				.commit();
			}
			
		} catch (MqttException e) {
			Log.d(Config.TAG, "Verbindung fehlgeschlagen: " + e.getMessage());
			PrivateBroadcast.broadcastStatus(this, false, Config.SERVICE_CON_STATUS_FILTER);
		} catch  (Exception e) {
			Log.d(Config.TAG, "SSLSocket-Fehler: " + e.getMessage());
		}
		
		/* BroadcastReceiver binden */
		bindBroadCastReceivers(true);
	}
	
	
	
    public void bindBroadCastReceivers(boolean bind) {
    	if(bind) {
    		/* Den Service über die Sichtbarkeit einer Activity informieren */
    		LocalBroadcastManager.getInstance(this).registerReceiver(mActivityReceiver,
  			      new IntentFilter(Config.ACTIVITY_ACTIVE_FILTER));
    		
    		/* Auf zu abonnierende Topics horchen */
    		LocalBroadcastManager.getInstance(this).registerReceiver(mTopicsSubscribeReceiver,
  			      new IntentFilter(Config.SERVICE_SUBSCRIBE_TOPICS_FILTER));
  		
    	} else {
    		LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityReceiver);
    		LocalBroadcastManager.getInstance(this).unregisterReceiver(mTopicsSubscribeReceiver);
    	}
    }
	
	
	public void handleMsg(String topic, String msg) {
		
		if(activityIsActive) {
			PrivateBroadcast.broadcastMsg(this, topic, msg);
		}
		else {
			this.msgStore.offer(new ServiceMessage(topic, msg));
		}
		
		Log.d(Config.TAG, "New Message: " + topic + ":" + msg + "\n");
	}
	
	public boolean getActivityStatus() {
		return activityIsActive;
	}
	
	@Override
	public void onDestroy() {
		
		/* Vor dem zerstören nich vorher die aktiven BroadcastReceiver lösen */
		bindBroadCastReceivers(false);
		
		try {
			if(mqttClient != null && mqttClient.isConnected())
				mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Etwas ist schief gelaufen!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
		
		super.onDestroy();
	}

	
	public class ServiceBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			activityIsActive = intent.getBooleanExtra("active", false);
			
			if(activityIsActive) {
				Log.d(Config.TAG, "Activity is active!");
				
				for(ServiceMessage msg: msgStore) {
					handleMsg(msg.getTopic(), msg.getMsg());
				}
			}
			else {
				Log.d(Config.TAG, "Activity is inactive!");
			}
		}
	}
	
	
	public class TopicsSubscribeBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String[] topicsToSubscribe = intent.getStringArrayExtra("topics");
			
			if(topicsToSubscribe == null)
				return;
			
			
			/* Erstmal die bisherigen unsubscriben */
			for(String subscribedTopic: subscribedTopics) {
				try {
					mqttClient.unsubscribe(subscribedTopic);
				} catch (MqttException e) {
					Log.e(Config.TAG, "Topic konnte nicht unsubscribed werden: " + e.getMessage());
				}
			}
			
			subscribedTopics.clear();
			
			/* Gegebene Topics abonnieren */
			for(int i = 0; i < topicsToSubscribe.length; i++) {
				try {
					mqttClient.subscribe(topicsToSubscribe[i]);
					subscribedTopics.add(topicsToSubscribe[i]);
					Log.d(Config.TAG, "Topic abonniert: " + topicsToSubscribe[i]);
				} catch (MqttException e) {
					Log.e(Config.TAG, "Topic konnte nicht subscribed werden: " + e.getMessage());
				}
			}
		}
	}
}
