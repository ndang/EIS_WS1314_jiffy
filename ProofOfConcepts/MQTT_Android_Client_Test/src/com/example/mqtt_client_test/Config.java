
package com.example.mqtt_client_test;

public class Config {

	public static final String BROKER_ADDR = "ssl://192.168.43.166:8883";
	public static final String TAG = "MQTT-Client";
	
	public static String keystore_asset = "broker.bks";
	public static String keystore_pass = "password";
	public static String truststore_asset = "client.bks";
	public static String truststore_pass = "password";
	
	public static String ACTIVITY_ACTIVE_FILTER = "com.example.mqtt_client.activity_active";
	public static String MESSAGE_AVAIL_FILTER = "com.example.mqtt_client.message_avail";
	
	public static String NOTIFY_LED_COLOR = "#ff0000ff"; // #aarrggbb oder farbname (blue, red etc.)
	
	private Config() {}

}
