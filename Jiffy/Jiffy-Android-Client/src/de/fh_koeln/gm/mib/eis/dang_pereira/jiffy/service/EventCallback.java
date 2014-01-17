package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.activities.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class EventCallback implements MqttCallback {
	
	private ContextWrapper cw;
	NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
	private int numMessages = 0;
	
	public EventCallback(ContextWrapper cw) {
		this.cw = cw;
	}
	
	
	@Override
	public void connectionLost(Throwable t) {
		Log.d(Config.TAG, "Connection lost!");
		
		((MQTTService)this.cw).restartIfNotConnected();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		
		String payload = new String(msg.getPayload());
		
		MQTTService mqttService = ((MQTTService)cw);
		mqttService.handleMsg(topic, payload);
		
		if(mqttService.getActivityStatus())
			return;
		
		NotificationManager mNotificationManager =
				(NotificationManager) cw.getSystemService(Context.NOTIFICATION_SERVICE);
		
		final Intent intent = new Intent(cw, MainActivity.class);
        final PendingIntent activity = PendingIntent.getActivity(cw, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(cw)
			.setContentTitle("Neue Nachrichten")
			.setContentText("Sie haben neue Nachrichten erhalten!")
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(activity)
			.setOnlyAlertOnce(false)
			.setAutoCancel(true)
			.setDefaults(Notification.DEFAULT_VIBRATE)
			.setLights(Color.parseColor(Config.NOTIFY_LED_COLOR), 1000, 3000);
		
		String constrMsg = topic + ":" + payload;
		
		inboxStyle.addLine(constrMsg);
		
		mNotifyBuilder
			.setStyle(inboxStyle)
			.setNumber(++numMessages);
		
		mNotificationManager.notify(	0,
										mNotifyBuilder.build());
	}

	
	public void resetNotifcation() {
		numMessages = 0;
		inboxStyle = new NotificationCompat.InboxStyle();
	}
	
}
