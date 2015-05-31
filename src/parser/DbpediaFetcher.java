package parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.sql.ResultSet;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DbpediaFetcher
{
    public static final String SPARQL_SERVICE = "http://dbpedia.org/sparql";
    public static final String PATH_TO_XMLS = "xml/";
    public static final String EMPTY_RESULTS_SET = "-1";

    static String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>        \n" +
            "PREFIX type: <http://dbpedia.org/class/yago/>\n" +
            "PREFIX prop: <http://dbpedia.org/property/>\n" +
            "PREFIX db-owl: <http://dbpedia.org/ontology/>\n" +
            "SELECT ?name ?pageid ?arearank\n" +
            "WHERE {\n" +
            "    ?country a type:LandlockedCountries ;\n" +
            "             rdfs:label ?name ;\n" +
            "             db-owl:capital ?capital ;\n" +
            "             db-owl:wikiPageID ?pageid ;\n" +
            "             prop:areaRank ?arearank.\n" +
            "  \n" +
            "    FILTER (?arearank > 0 && ?arearank < 100 &&\n" +
            "            langMatches(lang(?name), \"EN\")) .\n" +
            "} ORDER BY ASC(?arearank)";

    public static void main(String[] args)
    {
        executeQuery(sparqlQueryString);
    }

    public static String executeQuery(String sparqlQuery)
    {
        Model model = ModelFactory.createDefaultModel();
        Query query = QueryFactory.create(sparqlQuery);

        QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_SERVICE, sparqlQuery);
        ResultSet results = qexec.execSelect();

        if (!results.hasNext()){
            return EMPTY_RESULTS_SET;
        }

        String fileName = getFilename();
        File file = new File(fileName);

        try {
            FileOutputStream fop = new FileOutputStream(file);
            ResultSetFormatter.outputAsXML(fop, results);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return fileName;
    }

    public static String getFilename()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        Date now = new Date();
        String fileName = sdf.format(now);

        return PATH_TO_XMLS + fileName + ".xml";
    }

}