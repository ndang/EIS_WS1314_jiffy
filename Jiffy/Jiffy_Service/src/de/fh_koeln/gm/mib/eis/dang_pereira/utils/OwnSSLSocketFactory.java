package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

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
	    
	    /* Laden der Key-Store-Datei */
		kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    ks = KeyStore.getInstance("JKS");
	    ks.load(ksStream, passKS.toCharArray());

	    kmf.init(ks, passKS.toCharArray());
	    
	    /* Laden der Trust-Store-Datei */
	    KeyStore ts;
	    ts = KeyStore.getInstance("JKS");
	    ts.load(tsStream, passTS.toCharArray());
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    tmf.init(ts);
	    
	    /* Erzeugen des eigenetlichen SSL-Kontexts */
	    SSLContext ctx = SSLContext.getInstance("TLS");
	    /* Das Kontext-Objekt mit den Daten des Key- und Trust-Stores initialisieren */
	    ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		
	    /* Die Socket-Factory des Kontexts für die Weiterverwendung zurückgeben */
	    return ctx.getSocketFactory();
	}
	
}
