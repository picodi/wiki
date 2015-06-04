package parser;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Main
{

    public static final boolean DEBUG_MODE = true;

    public static String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>        \n" +
            "PREFIX type: <http://dbpedia.org/class/yago/>\n" +
            "PREFIX prop: <http://dbpedia.org/property/>\n" +
            "PREFIX db-owl: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "SELECT ?name ?pageid ?arearank\n" +
            "WHERE {\n" +
            "    ?country a dbo:Country ;\n" +
            "             rdfs:label ?name ;\n" +
            "             db-owl:capital ?capital ;\n" +
            "             db-owl:wikiPageID ?pageid ;\n" +
            "             prop:areaRank ?arearank.\n" +
            "  \n" +
            "    FILTER (?arearank > 0 && ?arearank < 10 &&\n" +
            "            langMatches(lang(?name), \"EN\")) .\n" +
            "} ORDER BY ASC(?arearank)";

    public static ApplicationCoordinator coordinator = new ApplicationCoordinator();
    public static void main(String[] args)
    {
        coordinator.setDebugMode(DEBUG_MODE);
        coordinator.executeQuery(sparqlQueryString);

        System.out.println("end");
    }

    public static void printMap(HashMap<String, String> map)
    {
        for (String key : map.keySet() ) {
            String value = map.get(key);
            System.out.println( key + " = " + value);
        }
    }
}
