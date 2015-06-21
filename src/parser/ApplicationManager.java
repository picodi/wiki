package parser;


import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ApplicationManager
{

    private boolean debugMode;

    public static final String PATH_TO_FILES = "wikipedia_results/";
    private DbpediaFetcher fetcher = new DbpediaFetcher();
    private DbpediaParser parser = new DbpediaParser();
    private UrlExtractor extractor = new UrlExtractor();
    private WikiParser wikiSearch = new WikiParser();
    private PatternMatcher matcher = new PatternMatcher();

    public ApplicationManager()
    {
        setDebugMode(false);
    }

    public ApplicationManager(boolean debugMode)
    {
        setDebugMode(debugMode);
    }

    public String executeQuery(String query)
    {
        String fileName = fetcher.executeQuery(query);
        try {

            if (fileName.compareTo(DbpediaFetcher.class.getDeclaredField("BAD_CONNECTION").get(null).toString()) == 0) {
                return "Bad connection.";
            }
            if (fileName.compareTo(DbpediaFetcher.class.getDeclaredField("INCORRECT_QUERY").get(null).toString()) == 0) {
                return "Incorrect Query. Check the syntax.";
            }
                // if fetcher returns an empty result
            if (fileName.compareTo(DbpediaFetcher.class.getDeclaredField("EMPTY_RESULTS_SET").get(null).toString()) == 0) {
                if (debugMode) {
                    System.err.println("DbpediaFetcher result: empty");
                }
                return "DbpediaFetcher result: empty.";
            } else {
                if (debugMode) {
                    System.out.println("DbpediaFetcher result: not empty");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("==============================================");

        System.out.println(fileName);
        parser.setFilename(fileName);
        ArrayList<WikiParamSet> list = parser.parse();


        if (debugMode) {
            System.out.println("No. results from DbpediaParser: " + list.size());
        }

        ArrayList<String> results = new ArrayList<>();
        for (WikiParamSet set : list) {

            if (debugMode) {
                System.out.println(set.getKeyword1() + " " + set.getKeyword2() + " " + set.getPageId());
            }

            if (set.getKeyword1().compareTo(set.getKeyword2()) != 0) {
                String page = extractor.getUrlForPageId(set.getPageId());

                if (debugMode) {
                    System.out.println("Searching patterns on: " + page);
                }
                results.addAll(wikiSearch.getData(page, set.getKeyword1(), set.getKeyword2()));
            }
        }

        if (debugMode) {
            System.out.println("No. results from WikiParser: " + results.size());
        }

        String wikiFileName = getFilename();
        printInFile(wikiFileName, results);
        String finalResults  = matcher.loadPipeline(wikiFileName);
        return finalResults;
    }

    public void printInFile(String filename, ArrayList<String> data)
    {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            for (String s : data) {
                writer.println(s);
                writer.println();
            }
            writer.close();
            System.out.println("Successfully printed: " + filename);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String concat(ArrayList<String> data)
    {
        String resultString = "";
        for (String s : data) {
            resultString = resultString.concat(s + "\n");
        }
        return resultString;
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
        return PATH_TO_FILES + fileName + ".txt";
    }


    public boolean getDebugMode()
    {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }

    public DbpediaFetcher getFetcher()
    {
        return fetcher;
    }

    public void setFetcher(DbpediaFetcher fetcher)
    {
        this.fetcher = fetcher;
    }

    public UrlExtractor getExtractor()
    {
        return extractor;
    }

    public void setExtractor(UrlExtractor extractor)
    {
        this.extractor = extractor;
    }

    public WikiParser getWikiSearch()
    {
        return wikiSearch;
    }

    public void setWikiSearch(WikiParser wikiSearch)
    {
        this.wikiSearch = wikiSearch;
    }
}
