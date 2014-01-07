package playarea;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Classes;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grade;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grades;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Guardian;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.SClass;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Subject;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Subjects;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Topics;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.User;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Users;

public class MainResourceJSON {

	public static void main(String[] args) throws IOException {

		ObjectMapper m = new ObjectMapper();
		
		
		//User u = m.readValue(new File("res/playdata/resource_structs/user.json"), User.class);
		//System.out.println(u);
		
		
		//Student s = m.readValue(new File("res/playdata/resource_structs/student.json"), Student.class);
		//System.out.println(s);
		
		
		//Guardian g = m.readValue(new File("res/playdata/resource_structs/guardian.json"), Guardian.class);
		//System.out.println(g);
		
		
		//Users us = m.readValue(new File("res/playdata/resource_structs/users.json"), Users.class);
		//System.out.println(us);
		
		
		//Grade gr = m.readValue(new File("res/playdata/resource_structs/grade.json"), Grade.class);
		//System.out.println(gr);
		
		
		//Grades grs = m.readValue(new File("res/playdata/resource_structs/grades.json"), Grades.class);
		//System.out.println(grs);
	
		
		//SClass c = m.readValue(new File("res/playdata/resource_structs/class.json"), SClass.class);
		//System.out.println(c);
		
		
		//Classes cs = m.readValue(new File("res/playdata/resource_structs/classes.json"), Classes.class);
		//System.out.println(cs);
		
		//Subject su = m.readValue(new File("res/playdata/resource_structs/subject.json"), Subject.class);
		//System.out.println(su);
		
		//Subjects sus = m.readValue(new File("res/playdata/resource_structs/subjects.json"), Subjects.class);
		//System.out.println(sus);
		
		//Topics ts = m.readValue(new File("res/playdata/resource_structs/topics.json"), Topics.class);
		//System.out.println(ts);
		
	}

}
