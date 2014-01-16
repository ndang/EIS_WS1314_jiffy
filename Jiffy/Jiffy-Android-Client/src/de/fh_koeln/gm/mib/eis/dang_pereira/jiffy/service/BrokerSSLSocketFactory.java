package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;

import android.util.Log;

public class BrokerSSLSocketFactory {

	private BrokerSSLSocketFactory() {}
	
	public static SocketFactory getSocketFactory(InputStream ksStream, String passKS, InputStream tsStream, String passTS) {
		
	    KeyManagerFactory kmf;
	    KeyStore ks;
	    
		try {
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		    ks = KeyStore.getInstance("BKS");
		    ks.load(ksStream, passKS.toCharArray());
		    ksStream.close();
		    
		    kmf.init(ks, passKS.toCharArray());
		    
		    KeyStore ts;
		    ts = KeyStore.getInstance("BKS");
		    ts.load(tsStream, passTS.toCharArray());
		    tsStream.close();

		    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		    tmf.init(ts);
		    
		    SSLContext ctx = SSLContext.getInstance("TLS");
		    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		    
		    return ctx.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			Log.e(Config.TAG, e.getMessage());
		} catch (KeyStoreException e) {
			Log.e(Config.TAG, e.getMessage());
		} catch (CertificateException e) {
			Log.e(Config.TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(Config.TAG, e.getMessage());
		} catch (UnrecoverableKeyException e) {
			Log.e(Config.TAG, e.getMessage());
		} catch (KeyManagementException e) {
			Log.e(Config.TAG, e.getMessage());
		}

	    return null;
	}
	
}
