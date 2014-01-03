package de.fh_koeln.gm.mib.eis.dang_pereira;

import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.MainGlobalConsumer;
import de.fh_koeln.gm.mib.eis.dang_pereira.rest.MainREST;

public class JiffyParallelStart {

	public static void main(String[] args) {
		
		/* 
		 * Normalerweise sollten die Klassen:
		 *		de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.MainGlobalConsumer
		 *		u.
		 *		de.fh_koeln.gm.mib.eis.dang_pereira.rest.MainREST
		 * unabhängig voneinander gestartet werden, damit sie in eigenständigen Prozessen laufen.
		 * Somit beeinflusst keine "Prozedur" die andere in ihrer Aufgabe.
		 * Für die Entwicklung und dem Testen des Jiffy Service, wird diese Klasse verwendet,
		 * um beide "Prozeduren" über nur eine Klasse bzw. Objekt parallel in separaten Threads starten zu können.
		 *
		 */
		
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
