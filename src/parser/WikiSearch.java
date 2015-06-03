package parser;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WikiSearch
{
    public static final String DEFAULT_URL = "name";
    public static final String MESSAGE_NO_SENTENCE_MATCH = "Both keywords were not found in same sentence";
    private String url = "";

    public WikiSearch()
    {}


    public ArrayList<String> getData(String url, String keyword1, String keyword2)
    {
        ArrayList<String> finalResults = new ArrayList<>();
        WikiTrimmer wikiTrimmer = new WikiTrimmer();

        if (url.compareTo("") == 0)
        {
            System.err.println( "No URL found for " + keyword1+ "\n" );
            //System.exit(1);
            return finalResults;
        }

        URL u;
        InputStream inputStream = null;
        BufferedReader in;
        String s;

        try {
            u = new URL(url);
            inputStream = u.openStream();
            in = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8));

            boolean inBody = false;
            while ((s = in.readLine()) != null)
            {
                inBody = isInBody(s, inBody);

                if (inBody) {
                    String tempString = wikiTrimmer.trimString(s);
                    if (tempString.contains(keyword1) && tempString.contains(keyword2)) {
                        finalResults.addAll(getSentancesContainingKeywords(tempString, keyword1, keyword2));
                    }
                }
            }

            if (finalResults.size() == 0) {
            //    finalResults.add(MESSAGE_NO_SENTENCE_MATCH);
            }

        } catch (MalformedURLException exception) {
            System.err.println("MalformedURLException occured.");
            exception.printStackTrace();
        } catch (IOException ioException) {
            System.err.println("ioException occured.");
            ioException.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return finalResults;
    }

    public boolean isInBody(String s, boolean inBody)
    {
        if (s.contains("<body")) {
            return true;
        }

        if (s.contains("</body")) {
            return false;
        }

        return inBody;
    }

    public ArrayList<String> getSentancesContainingKeywords(String paragraph, String keyword1, String keyword2)
    {
        ArrayList<String> results = new ArrayList<>();
        String split = removeHtmlTags(paragraph);


            String current = split;
            if(current.contains(keyword1) && current.contains(keyword2)) {
                results.add(current);
            }

        return results;
    }

    public String removeHtmlTags(String string)
    {
        return string.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");
    }
}