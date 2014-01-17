import java.util.ArrayList;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.data_access.RESTDataHandler;


public class RESTTest {

	
	public static void main(String[] argvs) {
		
		RESTDataHandler rdh = RESTDataHandler.getInstance();
		
		ArrayList<String> tList = rdh.getTopicsToSubscribe().getTopics();
		
		for(String topic: tList) {
			System.out.println(topic);
		}
		
	}
}
