package parser;


import java.io.PrintWriter;
import java.util.ArrayList;

public class ApplicationCoordinator
{

    private boolean debugMode;

    private DbpediaFetcher fetcher = new DbpediaFetcher();
    private DbpediaParser parser = new DbpediaParser();
    private UrlExtractor extractor = new UrlExtractor();
    private WikiSearch wikiSearch = new WikiSearch();

    public ApplicationCoordinator()
    {
        setDebugMode(false);
    }

    public ApplicationCoordinator(boolean debugMode)
    {
        setDebugMode(debugMode);
    }

    public void executeQuery(String query)
    {
        String fileName = fetcher.executeQuery(query);
        try {
            // if fetcher returns an empty result
            if (fileName.compareTo(DbpediaFetcher.class.getDeclaredField("EMPTY_RESULTS_SET").get(null).toString()) == 0) {
                if (debugMode) {
                    System.out.println("DbpediaFetcher result: empty");
                }
                return;
            } else {
                if (debugMode) {
                    System.out.println("DbpediaFetcher result: not empty");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        parser.setFilename(fileName);
        ArrayList<WikiParamSet> list = parser.parse();


        if (debugMode) {
            System.out.println("No. results from DbpediaParser: " + list.size());
        }

        ArrayList<String> results = new ArrayList<>();
        for (WikiParamSet set : list) {

            if (set.getKeyword1().compareTo(set.getKeyword2()) != 0) {
                String page = extractor.getUrlForPageId(set.getPageId());

                if (debugMode) {
                    System.out.println("Searching patterns on: " + page);
                }
                results.addAll(wikiSearch.getData(page, set.getKeyword1(), set.getKeyword2()));
            }
        }

        if (debugMode) {
            System.out.println("No. results from WikiSearch: " + results.size());
        }

        printInFile("custom-result.txt", results);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public WikiSearch getWikiSearch()
    {
        return wikiSearch;
    }

    public void setWikiSearch(WikiSearch wikiSearch)
    {
        this.wikiSearch = wikiSearch;
    }
}
