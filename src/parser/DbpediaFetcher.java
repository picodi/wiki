package parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Writer;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.sql.ResultSet;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DbpediaFetcher
{
    public static final String SPARQL_SERVICE = "http://dbpedia.org/sparql";
    public static final String STYLESHEET = "http://www.w3.org/TR/rdf-sparql-XMLres/result-to-html.xsl";
    public static final String EMPTY_RESULTS_SET = "-1";
    public static final String INCORRECT_QUERY = "-2";
    public static final String BAD_CONNECTION = "-3";
    public static final String PATH_TO_XMLS = "dbpedia_results/";

    /**
     * Executes given query and saves the resulting XML in a file
     *
     * @param sparqlQuery
     *
     * @return String
     */
    public String executeQuery(String sparqlQuery)
    {
        Model model = ModelFactory.createDefaultModel();
        try {
            Query query = QueryFactory.create(sparqlQuery);
        } catch (QueryParseException e) {
            return INCORRECT_QUERY;
        }

        try {
            QueryExecution qexec = QueryExecutionFactory.sparqlService(SPARQL_SERVICE, sparqlQuery);
            ResultSet results = qexec.execSelect();
            if (!results.hasNext()){
                return EMPTY_RESULTS_SET;
            }
            String fileName = getFilename();
            File file = new File(fileName);
            FileOutputStream fop = new FileOutputStream(file);
            ResultSetFormatter.outputAsXML(fop, results, STYLESHEET);
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return BAD_CONNECTION;
        }

    }

    /**
     * Gives the relative path to where the data will be stored
     *
     * @return String
     */
    public String getFilename()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        Date now = new Date();
        String fileName = sdf.format(now);

        return formatFilePath(fileName);
    }

    /**
     * Returns the path for an XML, whose filename is given as a parameter
     *
     * @param fileName
     *
     * @return String
     */
    public String formatFilePath(String fileName)
    {
        return PATH_TO_XMLS + fileName + ".xml";
    }

}