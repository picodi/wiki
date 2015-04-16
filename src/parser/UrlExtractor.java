package parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class UrlExtractor
{
    public static final String WIKI_URL_SKELETON = "http://en.wikipedia.org/w/api.php?action=query&prop=info&inprop=url&format=xml&pageids=";
    public static final String WANTED_ATTRIBUTE = "fullurl";

    public UrlExtractor()
    {}

    public String getUrlForPageId(String pageId)
    {
        String urlString = WIKI_URL_SKELETON.concat(pageId);
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new URL(urlString).openStream());

            doc.getDocumentElement().normalize();

            NodeList resultList = doc.getElementsByTagName("page");


            for (int temp = 0; temp < resultList.getLength(); temp++) {
                Node resultData = resultList.item(temp);

                if (resultData.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) resultData;

                    String url = eElement.getAttribute(WANTED_ATTRIBUTE);
                    if (url != null) {
                        return url;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
