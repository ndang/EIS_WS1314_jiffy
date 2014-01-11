package de.fh_koeln.gm.mib.eis.dang_pereira.rest.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.core.util.Base64;

import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grade;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Grades;
import de.fh_koeln.gm.mib.eis.dang_pereira.resource_structs.Student;

@Path("/student")
public class StudentResource extends Resource {

	public StudentResource() {
		/* 
		 * Konstruktor der Vaterklasse aufrufen, um Zugriff auf das DataLayer-Objekt und den Jackson ObjectMapper zu erhalten
		 */
		super();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postStudent(@Context HttpHeaders headers, String body) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		
		Student student = null;
		try {
			student = jmapper.readValue(body, Student.class);
		} catch (IOException e) {
			System.err.println("Konnte das JSON-Dokument nicht abbilden: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		/* Um das Passwort nicht mit im Dokument zu versenden, wird es per HTTP-Feld überreicht */
		String givenPass = null;
		List<String> list = headers.getRequestHeader("given-user-password");
		
		if(list != null && list.size() == 1)
			givenPass = list.get(0);
		
		/* Da das Passwort im Base64-Format vorliegt, muss es vorher dekodiert werden */
		givenPass = Base64.base64Decode(givenPass);
		
		
		/* Daten weiter an den DB-Layer geben, damit dieser sie in die DB schreibt */
		Integer userId = this.dbl.postStudent(student, givenPass);
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(userId != null){
			return Response.status(201).header("Location", "/user/" + userId).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	@PUT
	@Path("/{user_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putStudent(@Context HttpHeaders headers, @PathParam("user_id") Integer userId, String body) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		
		Student student = null;
		try {
			student = jmapper.readValue(body, Student.class);
		} catch (IOException e) {
			System.err.println("Konnte das JSON-Dokument nicht abbilden: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		/* Um das Passwort nicht mit im Dokument zu versenden, wird es per HTTP-Feld überreicht */
		String givenPass = null;
		List<String> list = headers.getRequestHeader("given-user-password");
		
		if(list != null && list.size() == 1)
			givenPass = list.get(0);
		
		/* Da das Passwort im Base64-Format vorliegt, muss es vorher dekodiert werden */
		givenPass = Base64.base64Decode(givenPass);
		
		
		/* Daten weiter an den DB-Layer geben, damit dieser sie in die DB schreibt */
		boolean success = this.dbl.putStudent(userId, student, givenPass);
		
		/* Wenn Aktualisierung erfolgreich war, dann soll das per Statuscode bekannt gegeben werden */
		if(success){
			return Response.status(204).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	@GET
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudent(	@Context HttpHeaders headers,
								@PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Nur Schüler können zurückgegeben werden */
		if(!idIsOfUserType(userId, "STUDENT")) {
			return Response.status(404).build();
		}
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		Student student = this.dbl.getStudent(userId);
		
		String studentStr = null;
		
		try {
			studentStr = jmapper.writeValueAsString(student);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Studenten-Objekt nicht serialisieren: " + e.getMessage());
		}
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(student != null){
			return Response.ok().entity(studentStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	

	
	/*  ######  /grade   ######  */
	
	@POST
	@Path("/{user_id}/grade")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postGrade(	@Context HttpHeaders headers,
								@PathParam("user_id") Integer userId, String body) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		// TODO: Sichergehen, dass es ein Lehrer ist, der das Fach unterrichtet
		
		
		Grade grade = null;
		try {
			grade = jmapper.readValue(body, Grade.class);
		} catch (IOException e) {
			System.err.println("Konnte das JSON-Dokument nicht abbilden: " + e.getMessage());
		}
		
		
		/* Daten weiter an den DB-Layer geben, damit dieser sie in die DB schreibt */
		Integer gradeId = this.dbl.postStudentGrade(userId, grade);
		
		/* Wenn die Note eingefügt wurde, dann soll der Ressourcen-Ort zurückgegeben werden */
		if(gradeId != null){
			return Response.status(201).header("Location", "/user/" + userId + "/grade/" + gradeId).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	@GET
	@Path("/{user_id}/grade/{grade_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentGrade(@Context HttpHeaders headers,
									@PathParam("user_id") Integer userId,
									@PathParam("grade_id") Integer gradeId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		
		/* Nur die Note eines Schüler kann zurückgegeben werden */
		if(!idIsOfUserType(userId, "STUDENT")) {
			return Response.status(404).build();
		}
		
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		Grade grade = this.dbl.getStudentGrade(userId, gradeId);
		
		String gradeStr = null;
		
		try {
			gradeStr = jmapper.writeValueAsString(grade);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Grade-Objekt nicht serialisieren: " + e.getMessage());
		}
		
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(grade != null){
			return Response.ok().entity(gradeStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
	
	
	@GET
	@Path("/{user_id}/grades")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStudentGrades(@Context HttpHeaders headers,
									@PathParam("user_id") Integer userId) {
		
		if(!userExists(headers)) {
			return Response.status(401).build();
		}
		
		/* Nur die Noten eines Schüler können zurückgegeben werden */
		if(!idIsOfUserType(userId, "STUDENT")) {
			return Response.status(404).build();
		}
		
		
		/* Daten per DB-Layer beziehen und sie in ein JSON-Dokument umbetten */
		Grades grades = this.dbl.getStudentGrades(userId);
		
		String gradesStr = null;
		
		try {
			gradesStr = jmapper.writeValueAsString(grades);
		} catch (JsonProcessingException e) {
			System.err.println("Konnte das Grades-Objekt nicht serialisieren: " + e.getMessage());
			e.printStackTrace();
		}
		
		/* Wenn Daten zurückgegeben wurden, dann sollen sie ausgeliefert werden */
		if(grades != null){
			return Response.ok().entity(gradesStr).type(MediaType.APPLICATION_JSON).build();
		}
		else {
			return Response.status(404).build();
		}
	}
	
}
