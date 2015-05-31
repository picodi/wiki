package parser;

import java.net.URL;
import java.net.URLClassLoader;
import weka.*;


public class NewClass {

    public static void main (String args[]) {

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

}
