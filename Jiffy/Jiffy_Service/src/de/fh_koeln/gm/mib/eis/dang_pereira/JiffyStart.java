package de.fh_koeln.gm.mib.eis.dang_pereira;

import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.MainGlobalConsumer;
import de.fh_koeln.gm.mib.eis.dang_pereira.rest.MainREST;

/**
 * Einstiegsklasse für den Start des Services
 * Broker und REST-Endpoint könne parallel in eigenen Threads laufen,
 * oder es kann per Parameter angegeben werden, welcher von beiden alleine gestartet werden soll (getrennte Prozesse)
 * 
 * @author dang_pereira
 *
 */
public class JiffyStart {

	public static void main(String[] args) {
		
		if(args.length > 0) {
			
			switch(args[0]) {
				case "consumer":
					new MainGlobalConsumer(); /* Message Broker Consumer */
					break;
					
				case "rest":
					new MainREST(); /* REST-Endpoint */
					break;
				default:
					System.out.println("- Jiffy-");
					System.out.println();
					System.out.println("Um nur eine Komponente zu starten:");
					System.out.println("\tjava -jar jiffy.jar [consumer|rest]");
			}
			
		} else {
			/* Paralleler lauf der beiden Logiken in jeweils eigenen Threads */
			
			new Thread() {
				@Override
				public void run() {
					new MainGlobalConsumer(); /* Message Broker Consumer */
				}
			}.start();
			
			
			new Thread() {
				@Override
				public void run() {
					new MainREST(); /* REST-Endpoint */
				}
			}.start();
		}
		
	}

}
