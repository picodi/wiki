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
        DbpediaFetcher fetcher = new DbpediaFetcher();
        String fileName = fetcher.executeQuery(sparqlQueryString);
        try {
            // if fetcher returns an empty result
            if (fileName.compareTo(DbpediaFetcher.class.getDeclaredField("EMPTY_RESULTS_SET").get(null).toString()) == 0) {
                if (DEBUG_MODE) {
                    System.out.println("DbpediaFetcher result: empty");
                }
                return;
            } else {
                if (DEBUG_MODE) {
                    System.out.println("DbpediaFetcher result: not empty");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DbpediaParser parser = new DbpediaParser(fileName);
        ArrayList<WikiParamSet> list = parser.parse();


        if (DEBUG_MODE) {
            System.out.println("No. results from DbpediaParser: " + list.size());
        }

        UrlExtractor extractor = new UrlExtractor();
        WikiSearch getter = new WikiSearch();
        ArrayList<String> results = new ArrayList<>();
        for (WikiParamSet set : list) {

            if (set.getKeyword1().compareTo(set.getKeyword2()) != 0) {
                String page = extractor.getUrlForPageId(set.getPageId());

                if (DEBUG_MODE) {
                    System.out.println("Searching patterns on: " + page);
                }
                results.addAll(getter.getData(page, set.getKeyword1(), set.getKeyword2()));
            }
        }

        if (DEBUG_MODE) {
            System.out.println("No. results from WikiSearch: " + results.size());
        }
        try {
            PrintWriter writer = new PrintWriter("custom-result.txt", "UTF-8");
            for (String s : results) {
                writer.println(s);
                writer.println();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printMap(HashMap<String, String> map)
    {
        for (String key : map.keySet() ) {
            String value = map.get(key);
            System.out.println( key + " = " + value);
        }
    }
}
