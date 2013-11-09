package de.fhkoeln.eis;


import java.io.IOException;

import com.sun.grizzly.SSLConfig;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.spi.container.servlet.ServletContainer;


public class RestMain {
        
        public static void main(String[] args) throws IOException {

                System.out.println("Starting grizzly...");
                
                ServletAdapter adapter = new ServletAdapter();
                adapter.addInitParameter("com.sun.jersey.config.property.packages", "de.fhkoeln.eis.resources");
                adapter.setContextPath("/");
                adapter.setServletInstance(new ServletContainer());

                //Configure SSL (See instructions at the top of this file on how these files are generated.)
                SSLConfig ssl = new SSLConfig();
                ssl.setKeyStoreFile(Config.Security.pathKS);
                ssl.setKeyStorePass(Config.Security.passKS);
                ssl.setTrustStoreFile(Config.Security.pathTS);
                ssl.setTrustStorePass(Config.Security.passTS);

                //Build the web server.
                GrizzlyWebServer webServer = new GrizzlyWebServer(Config.port, "." ,true);

                //Add the servlet.
                webServer.addGrizzlyAdapter(adapter, new String[]{"/"});

                //Set SSL
                webServer.setSSLConfig(ssl);

                //Start it up.
                System.out.println(String.format("Jersey app started with WADL available at "
                  + "%sapplication.wadl\n", Config.hostname + ":" + Config.port + "/"));
                
                webServer.start();
        }

}