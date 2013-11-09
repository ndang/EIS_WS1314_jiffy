package de.fhkoeln.eis.client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class OwnSSLContext {

	private OwnSSLContext() {}
	
	public static SSLContext getContext(String pathKS, String passKS, String pathTS, String passTS) throws Exception {
		
	    KeyManagerFactory kmf;
	    KeyStore ks;
	    
		kmf = KeyManagerFactory.getInstance("SunX509");
	    ks = KeyStore.getInstance("JKS");
	    ks.load(new FileInputStream(pathKS), passKS.toCharArray());

	    kmf.init(ks, passKS.toCharArray());
	    
	    
	    KeyStore ts;
	    ts = KeyStore.getInstance("JKS");
	    ts.load(new FileInputStream(pathTS), passTS.toCharArray());
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
	    tmf.init(ts);
	    
	    SSLContext ctx = SSLContext.getInstance("TLS");
	    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		
	    return ctx;
	}
	
}
