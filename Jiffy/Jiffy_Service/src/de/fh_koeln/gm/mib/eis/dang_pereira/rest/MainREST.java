package de.fh_koeln.gm.mib.eis.dang_pereira.rest;

import java.io.IOException;

import com.sun.grizzly.SSLConfig;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import de.fh_koeln.gm.mib.eis.dang_pereira.Config;


public class MainREST {

	
	public MainREST() {
		
		System.out.println("Starte Grizzly...");
        
		Config cfg = Config.getInstance(); 
		
		/* Den Serverlet-Adapter erzeugen und initialisieren */
        ServletAdapter adapter = new ServletAdapter();
        adapter.addInitParameter("com.sun.jersey.config.property.packages", "de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources");
        adapter.setContextPath("/");
        adapter.setServletInstance(new ServletContainer());

        /* SSL konfigurieren */
        SSLConfig ssl = new SSLConfig();
        ssl.setKeyStoreFile(cfg.ssl.path_ks);
        ssl.setKeyStorePass(cfg.ssl.pass_ks);
        ssl.setTrustStoreFile(cfg.ssl.path_ts);
        ssl.setTrustStorePass(cfg.ssl.pass_ts);

        /* Den Webserver erzeugen */
        GrizzlyWebServer webServer = new GrizzlyWebServer(cfg.rest_endpoint.port, "." ,true);

        /* Das Serverlet hinzufügen */
        webServer.addGrizzlyAdapter(adapter, new String[]{"/"});

        /* Die SSL-Konfiguration setzen */
        webServer.setSSLConfig(ssl);
        
        try {
        	/* Den Webserver starten */
        	webServer.start();
		} catch (IOException e) {
			System.err.println("Konnte Grizzly nicht starten: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		
		/* Einstiegs- bzw. Startpunkt für den REST-Endpoint */
		
		new MainREST();
	}
}
