package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class PrivateBroadcast {

	
	private PrivateBroadcast() {}
	
	
	public static void broadcastStatus(ContextWrapper from, boolean status, String broadKey) {
  	  Intent intent = new Intent(broadKey);
  	  intent.putExtra("status", status);
  	  LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
	
	public static void broadcastMsg(ContextWrapper from, String topic, String msg) {
		Intent intent = new Intent(Config.MESSAGE_AVAIL_FILTER);
		intent.putExtra("topic", topic);
		intent.putExtra("msg", msg);
		LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
	
	public static void broadcastTopicsToSubscribe(ContextWrapper from, String[] topics) {
	  	  Intent intent = new Intent(Config.SERVICE_SUBSCRIBE_TOPICS_FILTER);
	  	  intent.putExtra("topics", topics);
	  	  LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
}
