package parser;


import java.util.ArrayList;
import java.util.HashMap;

public class Main
{
    public static void main(String[] args)
    {
        DbpediaParser parser = new DbpediaParser("area_rank.xml");
        ArrayList<WikiParamSet> list = parser.parse();

        UrlExtractor extractor = new UrlExtractor();
        JGet getter = new JGet();
        ArrayList<String> results = new ArrayList<>();
        for (WikiParamSet set : list) {
            if (set.getKeyword1().compareTo(set.getKeyword2()) != 0) {
                String page = extractor.getUrlForPageId(set.getPageId());
                results.addAll(getter.getData(page, set.getKeyword1(), set.getKeyword2()));
            }
        }


        System.out.println("results: ");
        for (String s : results) {
            System.out.println(s);
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
