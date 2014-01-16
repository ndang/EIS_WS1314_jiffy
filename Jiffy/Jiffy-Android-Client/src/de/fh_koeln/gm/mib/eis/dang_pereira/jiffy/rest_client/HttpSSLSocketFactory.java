package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_client;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;


public class HttpSSLSocketFactory {

	private HttpSSLSocketFactory() {}
	
	public static SSLSocketFactory getSSLSocketFactory(InputStream ksStream, String passKS) throws Exception {
		
		try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            
            try {
                trusted.load(ksStream, passKS.toCharArray());
            } finally {
                ksStream.close();
            }
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
	}
}
