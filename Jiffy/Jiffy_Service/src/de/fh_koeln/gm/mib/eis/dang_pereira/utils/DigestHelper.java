package de.fh_koeln.gm.mib.eis.dang_pereira.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Eine Helperklasse, um den Hashwert aus einem String mittels eines gegebenen Hash-Alorithmus zu erzeugen
 * 
 * @author dang_pereira
 *
 */
public class DigestHelper {

	/* Klasse soll nicht intanziiert werden dürfen */
	private DigestHelper() {}
	
	public static String getHash(String data, String hashAlgorithm) {
		
		if(data == null)
			return null;
		
		/* Wenn der Algorithmus nicht gegeben ist, dann nutze SHA-256 */
		if(hashAlgorithm == null)
			hashAlgorithm = "SHA-256";
		
		/*
		 *  Bildung des Haswerts des gegebenen Passworts,
    	 * da das Passwort in der Datenbank ebenfalls nur als Hashwert geführt wird
    	 */
    	String dataHash = null;
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgorithm);
			md.update(data.getBytes("UTF-8"));
			byte[] mdbytes = md.digest();
			
			/* Der Hash wird in eine hexadezimale Form überführt */
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < mdbytes.length; i++) {
	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        dataHash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Unbekannter Algorithmus: " + hashAlgorithm);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.err.println("Kann den Datenstring nicht in UTF-8 enkodieren");
			e.printStackTrace();
		}
		
		return dataHash;
	}
	
}
