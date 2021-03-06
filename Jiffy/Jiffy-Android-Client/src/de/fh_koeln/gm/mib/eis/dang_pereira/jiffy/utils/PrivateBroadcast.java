package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.utils;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class PrivateBroadcast {

	
	private PrivateBroadcast() {}
	
	
	public static void broadcastStatus(Context from, boolean status, String broadKey) {
  	  Intent intent = new Intent(broadKey);
  	  intent.putExtra("status", status);
  	  LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
	
	public static void broadcastMsgAvail(Context from, String broadKey) {
		Intent intent = new Intent(broadKey);
		LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
	
	public static void broadcastTopicsToSubscribe(Context from, String[] topics) {
	  	  Intent intent = new Intent(Config.SERVICE_SUBSCRIBE_TOPICS_FILTER);
	  	  intent.putExtra("topics", topics);
	  	  LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
	}
	
	
	public static void broadcastServiceRestart(Context from) {
	  	  Intent intent = new Intent(Config.ACTIVITY_FILTER);
	  	  intent.putExtra("restartIfNeeded", true);
	  	  LocalBroadcastManager.getInstance(from).sendBroadcast(intent);
		}
}
