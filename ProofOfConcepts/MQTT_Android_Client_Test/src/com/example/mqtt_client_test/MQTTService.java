package com.example.mqtt_client_test;

import java.util.Queue;
import java.util.LinkedList;

import javax.net.SocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MQTTService extends Service {
	
	private Queue<Message> msgStore = new LinkedList<Message>();
	private MqttClient mqttClient;
	private boolean activityIsActive = true;
	private ServiceBroadCastReceiver mMessageReceiver = new ServiceBroadCastReceiver();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			mqttClient = new MqttClient(Config.BROKER_ADDR, "Android Consumer", new MemoryPersistence());
			
			MqttConnectOptions mqttOpts = new MqttConnectOptions();
			mqttOpts.setUserName("Peter2");
			mqttOpts.setPassword("Christa2".toCharArray());
			mqttOpts.setCleanSession(false);
			SocketFactory sf = OwnSSLSocketFactory.getSocketFactory(
											getAssets().open(Config.keystore_asset), Config.keystore_pass,
											getAssets().open(Config.truststore_asset), Config.truststore_pass);
			mqttOpts.setSocketFactory(sf);
			
			mqttClient.connect(mqttOpts);
			
			Log.d(Config.TAG, "Connection successful!");
			
			mqttClient.subscribe("test1");
			
			mqttClient.setCallback(new EventCallback(this));
			
		} catch (MqttException e) {
			
			Log.d(Config.TAG, "Connection failed!");
			Log.e(Config.TAG, e.getMessage());
		} catch  (Exception e) {
			Log.d(Config.TAG, "SSLSocketFailure!");
		}
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(Config.ACTIVITY_ACTIVE_FILTER));
	}
	
	
	public void handleMsg(String topic, String msg) {
		
		if(activityIsActive) {
	    	  Intent intent = new Intent(Config.MESSAGE_AVAIL_FILTER);
	    	  intent.putExtra("topic", topic);
	    	  intent.putExtra("msg", msg);
	    	  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		else {
			this.msgStore.offer(new Message(topic, msg));
		}
		
		Log.d(Config.TAG, "New Message: " + topic + ":" + msg + "\n");
	}
	
	public boolean getActivityStatus() {
		return activityIsActive;
	}
	
	@Override
	public void onDestroy() {
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		
		try {
			if(mqttClient != null)
				mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
		
		super.onDestroy();
	}

	
	public class ServiceBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			activityIsActive = intent.getBooleanExtra("active", false);
			
			if(activityIsActive) {
				Log.d(Config.TAG, "Active!");
				
				for(Message msg: msgStore) {
					handleMsg(msg.getTopic(), msg.getMsg());
				}
			}
			else {
				Log.d(Config.TAG, "Inactive!");
			}
		}
	}
	
}
