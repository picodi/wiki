package parser;

import java.util.ArrayList;

public class WikiTrimmer
{
    public ArrayList<String> trimArray(ArrayList<String> input)
    {
        ArrayList<String> trimmedResult = new ArrayList<>();

        for (String s : input) {
            trimmedResult.add(removeBraces(removeParatheses(s)));
        }

        return trimmedResult;
    }

    public String trimString(String input)
    {
        return removeBraces(removeParatheses(input));
    }

    public String removeParatheses(String input)
    {
        return input.replaceAll("\\(.*?\\)","");
    }

    public String removeBraces(String input)
    {
        return input.replaceAll("\\[.*?\\]","");
    }
}
