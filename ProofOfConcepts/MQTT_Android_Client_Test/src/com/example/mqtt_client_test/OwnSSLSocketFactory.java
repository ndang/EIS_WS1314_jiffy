package com.example.mqtt_client_test;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class OwnSSLSocketFactory {

	private OwnSSLSocketFactory() {}
	
	public static SocketFactory getSocketFactory(InputStream ksStream, String passKS, InputStream tsStream, String passTS) throws Exception {
		
	    KeyManagerFactory kmf;
	    KeyStore ks;
	    
		kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    ks = KeyStore.getInstance("BKS");
	    ks.load(ksStream, passKS.toCharArray());

	    kmf.init(ks, passKS.toCharArray());
	    
	    
	    KeyStore ts;
	    ts = KeyStore.getInstance("BKS");
	    ts.load(tsStream, passTS.toCharArray());
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    tmf.init(ts);
	    
	    SSLContext ctx = SSLContext.getInstance("TLS");
	    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		
	    return ctx.getSocketFactory();
	}
	
}
