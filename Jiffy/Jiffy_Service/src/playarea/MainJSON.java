package playarea;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainJSON {

	
	public static void main(String[] args) throws JsonProcessingException, IOException {
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream("res/topics.json"), "UTF-8");
		BufferedReader breader = new BufferedReader(isr);
		
		String content = "";
		String line = "";
		while((line = breader.readLine()) != null) {
			content += line;
			content += System.getProperty("line.separator");
		}
		breader.close();
		
		// Jackson
		ObjectMapper m = new ObjectMapper();
		JsonNode node = m.readTree(content);
		System.out.println(node.path("topics").get(0).path("abst").asText());
	}
	
}
