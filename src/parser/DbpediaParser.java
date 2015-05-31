package parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.*;

public class DbpediaParser
{
    public static final String LABEL_NAME = "name";
    public static final String LABEL_PAGEID = "pageid";
    public static final String DEFAULT_FILE = "xml/population.xml";
    private String filename;

    public DbpediaParser()
    {
        this.setFilename(DEFAULT_FILE);
    }

    public DbpediaParser(String filename)
    {
        this.setFilename(filename);
    }

    public DbpediaParser setFilename(String filename)
    {
        this.filename = filename;

        return this;
    }

    public String getFilename()
    {
        return this.filename;
    }

    /**
     * Returns an array of parameters tuples, identified in the given XML
     *
     * @return
     */
    public ArrayList<WikiParamSet> parse()
    {
        ArrayList<WikiParamSet> extractedParameters = new ArrayList<>();
        System.out.println("Parsing file: "+ this.getFilename());
        if (this.getFilename() == null) {
            System.err.println("Error: File not found");
            return extractedParameters;
        }

        try {

            File fXmlFile = new File(this.getFilename());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList resultsList = doc.getElementsByTagName("results");
            Node results = resultsList.item(0);
            NodeList resultList = results.getChildNodes();


            for (int temp = 0; temp < resultList.getLength(); temp++) {

                WikiParamSet parameterSet = new WikiParamSet();

                Node result = resultList.item(temp);
                NodeList resultDataList = result.getChildNodes();

                for (int i = 0; i < resultDataList.getLength(); i++) {
                    Node resultData = resultDataList.item(i);

                    if (resultData.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) resultData;
                        if (eElement.getAttribute("name").compareTo(LABEL_NAME) == 0) {
                            parameterSet.setKeyword1(getPackedContent(eElement));
                        } else if (eElement.getAttribute("name").compareTo(LABEL_PAGEID) == 0) {
                            parameterSet.setPageId(getPackedContent(eElement));
                        } else {
                            parameterSet.setKeyword2(getPackedContent(eElement));
                        }
                    }
                }

                if (parameterSet.getKeyword1().compareTo("") > 0 && parameterSet.getKeyword2().compareTo("") > 0 ) {
                    extractedParameters.add(parameterSet);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return extractedParameters;
    }

    /**
     * Prints the array of WikiParameters
     * @param list
     */
    public void printWikiparams(ArrayList<WikiParamSet> list)
    {
        System.out.println("Printing extracted keywords");

        for (WikiParamSet set : list) {
            System.out.println(set.getPageId() + ": " + set.getKeyword1() + " " + set.getKeyword2());
        }
    }

    /**
     * Trims the content of an element, avoiding empty space related issues
     *
     * @param element
     *
     * @return
     */
    public String getPackedContent(Element element) {
        if (element != null) {
            String text = element.getTextContent();
            if (text != null) {
                return text.trim().replaceAll("\\s+", " ");
            }
        }
        return "";
    }
}
