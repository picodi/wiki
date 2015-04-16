package parser;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Exchanger;

public class XmlWriter
{
    public void writeXmlFile(ArrayList<String> list) {

        try {

            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            Element root = doc.createElement("Outcome");
            doc.appendChild(root);

            Element Details = doc.createElement("example");
            root.appendChild(Details);

/*
            for (details dtl : list) {

                Element name = doc.createElement("Name");
                name.appendChild(doc.createTextNode(String.valueOf(dtl.getName())));
                Details.appendChild(name);

                Element id = doc.createElement("ID");
                id.appendChild(doc.createTextNode(String.valueOf(dtl.getId())));
                Details.appendChild(id);

                Element mmi = doc.createElement("Age");
                mmi.appendChild(doc.createTextNode(String.valueOf(dtl.getAge())));
                Details.appendChild(mmi);

            }
*/
            writeFile(doc);

        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        } catch (Exception e) {
            System.err.println("Error encountered");
            e.printStackTrace();
        }
    }

    public void writeFile(Document doc)
    {

        try {
            // Save the document to the disk file
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();

            // format the XML nicely
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            aTransformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            try {
                // location and name of XML file you can change as per need
                FileWriter fos = new FileWriter("./ros.xml");
                StreamResult result = new StreamResult(fos);
                aTransformer.transform(source, result);

            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (TransformerException ex) {
            System.out.println("Error outputting document");

        }
    }
}
