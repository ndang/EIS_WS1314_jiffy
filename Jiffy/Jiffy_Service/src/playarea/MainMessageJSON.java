package playarea;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.message_consumer.Message;

public class MainMessageJSON {

	public static void main(String[] args) throws IOException {
		
		
		ObjectMapper m = new ObjectMapper();
		Message msg = m.readValue(new File("res/message_example.json"), Message.class);
		
		
		System.out.println(msg);
	}

}
