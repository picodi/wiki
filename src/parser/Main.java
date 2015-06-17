package parser;


import javax.swing.*;
import java.util.HashMap;

public class Main
{

    public static final boolean DEBUG_MODE = true;

    public static void main(String[] args)
    {
        //coordinator.setDebugMode(DEBUG_MODE);
        //coordinator.executeQuery(sparqlQueryString);

        JFrame frame = new JFrame("SentenceIdentifier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new ApplicationView());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }



    public static void printMap(HashMap<String, String> map)
    {
        for (String key : map.keySet() ) {
            String value = map.get(key);
            System.out.println( key + " = " + value);
        }
    }
}
