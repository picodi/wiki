package parser;

import gate.*;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.util.persistence.PersistenceManager;
import gate.corpora.RepositioningInfo;

public class PatternMatcher
{
    public static final String ANNOTATION_WANTED = "class";
    public static final String PATH_TO_FILE = "file:/home/pico/IdeaProjects/untitled/";
    private String learningEngine = "savedFiles/learning_engine.xml";
    private String filename;
    private CorpusController corpusController;
    private CorpusController annieController;
    private Corpus applicationCorpus;

    public PatternMatcher()
    {
        initGate();
        initAnnie();
    }

    public void initAnnie(){
        Out.prln("Initialising ANNIE...");

        File pluginsHome = Gate.getPluginsHome();
        File anniePlugin = new File(pluginsHome, "ANNIE");
        File annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
        try {
            annieController = (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Out.prln("...ANNIE loaded");
    }

    public void initGate()
    {
        Out.prln("Initialising GATE...");
        try {
            Gate.setUserSessionFile(new File("/home/pico/.gate.session"));
            Gate.init();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Out.prln("...GATE initialised");
    }

    public String loadPipeline(String filename){
        try {
            ArrayList list = (ArrayList) PersistenceManager.loadObjectFromFile(Gate.getUserSessionFile());
            SerialAnalyserController pipeline = (SerialAnalyserController) list.get(0);

            populateCorpus(PATH_TO_FILE + filename);
            annieController.setCorpus(applicationCorpus);
            annieController.execute();

            Out.prln("Annie executed!");

            pipeline.setCorpus(applicationCorpus);
            pipeline.execute();

            Out.prln("Pipeline executed!");

            String[] anns = {"Sentence"};
            Out.prln("Printing rezults");
            String results = printResults(anns);
            printInFile("testDeRezultatFinal.txt", results);
            return results;
            //Out.prln("Done printing rezults");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void populateCorpus(String filename)
    {
        try {
            applicationCorpus = Factory.newCorpus("Application corpus");
            URL u = new URL(filename);
            FeatureMap params = Factory.newFeatureMap();
            params.put("sourceUrl", u);
            params.put("preserveOriginalContent", true);
            params.put("collectRepositioningInfo", true);
            Out.prln("Creating doc for " + u);
            Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", params);
            applicationCorpus.add(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void printInFile(String filename, String data)
    {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");

                writer.println(data);
                writer.println();

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String printResults(String[] annotations) throws IOException {
        StringBuffer finalResults = new StringBuffer("");


        Iterator iter = applicationCorpus.iterator();
        int count = 0;
        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";

        while (iter.hasNext()) {
            Document doc = (Document) iter.next();
            printInFile("rezultatAnotari.xml", doc.toXml());
            AnnotationSet defaultAnnotSet = doc.getAnnotations();
            Set annotTypesRequired = new HashSet();
            for(String ann : annotations) {
                annotTypesRequired.add(ann);
            }
            Set<Annotation> peopleAndPlaces =
                    new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));

            FeatureMap features = doc.getFeatures();
            String originalContent = (String)
                    features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
            RepositioningInfo info = (RepositioningInfo)
                    features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);

            ++count;
            File file = new File("StANNIE_" + count + ".HTML");
            Out.prln("File name: '" + file.getAbsolutePath() + "'");
            if (originalContent != null && info != null) {
                Out.prln("OrigContent and reposInfo existing. Generate file...");

                Iterator it = peopleAndPlaces.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();



                while (it.hasNext()) {
                    currAnnot = (Annotation) it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

                StringBuffer editableContent = new StringBuffer(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                Out.prln("Unsorted annotations count: " + peopleAndPlaces.size());
                Out.prln("Sorted annotations count: " + sortedAnnotations.size());
                for (int i = sortedAnnotations.size() - 1; i >= 0; --i) {
                    currAnnot = (Annotation) sortedAnnotations.get(i);
                    insertPositionStart =
                            currAnnot.getStartNode().getOffset().longValue();
                    insertPositionStart = info.getOriginalPos(insertPositionStart);
                    insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                    insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                    if (insertPositionEnd != -1 && insertPositionStart != -1) {
                        editableContent.insert((int) insertPositionEnd, endTag);
                        editableContent.insert((int) insertPositionStart, startTagPart_3);
                        editableContent.insert((int) insertPositionStart,
                                currAnnot.getType());
                        editableContent.insert((int) insertPositionStart, startTagPart_2);
                        editableContent.insert((int) insertPositionStart,
                                currAnnot.getId().toString());
                        editableContent.insert((int) insertPositionStart, startTagPart_1);
                    } // if
                } // for

                FileWriter writer = new FileWriter(file);
                writer.write(editableContent.toString());
                writer.close();
            } // if - should generate
            else if (originalContent != null) {
                Out.prln("OrigContent existing. Generate file...");

                Iterator it = peopleAndPlaces.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

                while (it.hasNext()) {
                    currAnnot = (Annotation) it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

                StringBuffer editableContent = new StringBuffer(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                Out.prln("Unsorted annotations count: " + peopleAndPlaces.size());
                Out.prln("Sorted annotations count: " + sortedAnnotations.size());
                for (int i = sortedAnnotations.size() - 1; i >= 0; --i)
                {
                    currAnnot = (Annotation) sortedAnnotations.get(i);

                   // Out.prln(currAnnot.getFeatures());
                    if(currAnnot.getFeatures().containsKey(ANNOTATION_WANTED))
                    {
                        insertPositionStart =
                                currAnnot.getStartNode().getOffset();
                        insertPositionEnd = currAnnot.getEndNode().getOffset();



                       // Out.prln("Start = " + insertPositionStart + " End = " + insertPositionEnd);
                        if (insertPositionEnd != -1 && insertPositionStart != -1) {

                            finalResults.append(
                                editableContent.substring((int)insertPositionStart, (int)insertPositionEnd) + "\n");


                            editableContent.insert((int) insertPositionEnd, endTag);
                            editableContent.insert((int) insertPositionStart, startTagPart_3);
                            editableContent.insert((int) insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int) insertPositionStart, startTagPart_2);
                            editableContent.insert((int) insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int) insertPositionStart, startTagPart_1);

                        }
                    } // if
                } // for

                FileWriter writer = new FileWriter(file);
                writer.write(editableContent.toString());
                writer.close();
            } else {
                Out.prln("Content : " + originalContent);
                Out.prln("Repositioning: " + info);
            }

            String xmlDocument = doc.toXml(peopleAndPlaces, false);
            String fileName = new String("StANNIE_toXML_" + count + ".HTML");
            FileWriter writer = new FileWriter(fileName);
            writer.write(xmlDocument);
            writer.close();
        }

        return finalResults.toString();
    }

   class SortedAnnotationList extends Vector {
        public SortedAnnotationList() {
            super();
        } // SortedAnnotationList

        public boolean addSortedExclusive(Annotation annot) {
            Annotation currAnot = null;

            // overlapping check
            for (int i=0; i<size(); ++i) {
                currAnot = (Annotation) get(i);
                if(annot.overlaps(currAnot)) {
                    //return false;
                } // if
            } // for

            long annotStart = annot.getStartNode().getOffset().longValue();
            long currStart;
            // insert
            for (int i=0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                currStart = currAnot.getStartNode().getOffset().longValue();
                if(annotStart < currStart) {
                    insertElementAt(annot, i);
          /*
           Out.prln("Insert start: "+annotStart+" at position: "+i+" size="+size());
           Out.prln("Current start: "+currStart);
           */
                    return true;
                } // if
            } // for

            int size = size();
            insertElementAt(annot, size);
//Out.prln("Insert start: "+annotStart+" at size position: "+size);
            return true;
        } // addSorted
    } // SortedAnnotationList

    public static void main(String[] args)
    {
        PatternMatcher p = new PatternMatcher();
    }

}
