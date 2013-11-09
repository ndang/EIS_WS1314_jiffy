package de.fhkoeln.eis;

public class Config {

    //public static String hostname = "http://localhost";
    //public static int port = 8080;
    public static String hostname = "https://localhost";
    public static int port = 8080;
    
    
    public static class Security {
    	public static String pathKS = "res/broker.ks";
    	public static String passKS = "password";
    	public static String pathTS = "res/client.ts";
    	public static String passTS = "password";
    }
    
    public static class DB {
        public static String host = "localhost";
        public static String name = "test";
        public static String table = "user";
        public static String user = "root";
        public static String pass = "12345";
    
        private DB() {};
    }
    
    // don't allow instances of this class
    private Config() {}
}
