package playarea;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.Base64;

import de.fh_koeln.gm.mib.eis.dang_pereira.utils.BasicAuthHelper;
import de.fh_koeln.gm.mib.eis.dang_pereira.utils.UserDBAuth;

public class RESTAuthTest {

	
	private static HttpHeaders headers = new HttpHeaders() {
		
		@Override
		public MultivaluedMap<String, String> getRequestHeaders() {
			return null;
		}
		
		@Override
		public List<String> getRequestHeader(String arg0) {
			
			/* Es ist egal was in arg0 steht */
			
			List<String> list = new ArrayList<String>();
			
			String credsStr = "Peter:Christa";
			
			list.add("Basic " + new String(Base64.encode(credsStr)));
			
			return list;
		}
		
		@Override
		public MediaType getMediaType() {
			return null;
		}
		
		@Override
		public Locale getLanguage() {
			return null;
		}
		
		@Override
		public Map<String, Cookie> getCookies() {
			return null;
		}
		
		@Override
		public List<MediaType> getAcceptableMediaTypes() {
			return null;
		}
		
		@Override
		public List<Locale> getAcceptableLanguages() {
			return null;
		}
	};
	
	
	public static void main(String[] argv) {
		
		boolean success = false;
		
		try {
			success = UserDBAuth.authUser(BasicAuthHelper.extractAuthCreds(headers));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
			
		if(success) {
			System.out.println("Success!");
		}
		else {
			System.out.println("Fail!");
		}
		
	}
	
}
