package com.example.test;

import java.io.File;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public class MainApplication {

	
	public static void main(String[] args) {
		
		// Erzeugung des TDB-Datasets
		String directory = "./tdb";
		Dataset dataset = TDBFactory.createDataset(directory);

		// Bezug des Datasets als Model
		Model tdb = dataset.getDefaultModel();

        // Eigener Namespace
		final String NS = "http://www.semanticweb.org/choldyn/ontologies/2013/11/untitled-ontology-12#";
		
		// Ressourcen und verbindende Eigenschaft erstellen
		Resource bob = new ResourceImpl(NS, "Bob");
		Property pKnows = new PropertyImpl(NS, "knows");
		Resource alice = new ResourceImpl(NS, "Alice");
		
		// Tripel zum Model/TDB hinzufuegen
		tdb.add(bob, pKnows, "42");
		tdb.add(bob, pKnows, alice);
		
		
        // Abfrage aufbauen
        String q =
        "PREFIX myOnto: " + NS +
        "SELECT * " +
        "WHERE {?s myOnto:knows \"42\"." + 
	        "?s myOnto:knows myOnto:Alice}";

        // Query erzeugen und ausführen
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, tdb);
        ResultSet results = qexec.execSelect();
	
        ResultSetFormatter.out(System.out, results, query) ;
	
        TDB.sync(dataset);
	}
	
}
