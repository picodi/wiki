package parser;

import gate.*;
import gate.corpora.RepositioningInfo;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class PatternMatcher
{
    public static final String ANNOTATION_WANTED = "define-rank";
    public static final String GAPP_FILE = "ANNIE_with_defaults.gapp";
    private String filename;
    private CorpusController corpusController;

    public void initAnnie() throws GateException, IOException
    {
        Out.prln("Initialising ANNIE...");

        // load the ANNIE application from the saved state in plugins/ANNIE
        File pluginsHome = Gate.getPluginsHome();
        File anniePlugin = new File(pluginsHome, "ANNIE");
        File annieGapp = new File(anniePlugin, GAPP_FILE);
        corpusController =
                (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);

        Out.prln("...ANNIE loaded");
    }

    public void execute() throws GateException
    {
        Out.prln("Running Pipeline...");
        corpusController.execute();
        Out.prln("...ANNIE complete");
    }

    public String runFile(String fileName) throws GateException, IOException
    {
        // initialise the GATE library
        Out.prln("Initialising GATE...");
        Gate.init();
        Out.prln("...GATE initialised");

        // initialise ANNIE (this may take several minutes)
        StandAloneAnnie annie = new StandAloneAnnie();
        annie.initAnnie();

        // create a GATE corpus and add a document for each command-line argument
        Corpus corpus = Factory.newCorpus("Custom corpus");
            URL u = new URL(fileName);
            FeatureMap params = Factory.newFeatureMap();
            params.put("sourceUrl", u);
            params.put("preserveOriginalContent", true);
            params.put("collectRepositioningInfo", true);
            Out.prln("Creating doc for " + u);
            Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", params);
            corpus.add(doc);

        // tell the pipeline about the corpus and run it
        annie.setCorpus(corpus);
        annie.execute();

        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";

        AnnotationSet defaultAnnotSet = doc.getAnnotations();
        Set annotTypesRequired = new HashSet();
        annotTypesRequired.add("Token");
        annotTypesRequired.add("Sentence");
        Set<Annotation> annotationsHashset = new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
        FeatureMap features = doc.getFeatures();
        String originalContent = (String)features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
        RepositioningInfo info = (RepositioningInfo)features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);

        File file = new File("StANNIE_HTML");
        Out.prln("File name: '"+file.getAbsolutePath()+"'");
        if(originalContent != null && info != null) {
            Out.prln("OrigContent and reposInfo existing. Generate file...");

            Iterator it = annotationsHashset.iterator();
            Annotation currAnnot;
            SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

            while(it.hasNext()) {
                currAnnot = (Annotation) it.next();
                sortedAnnotations.addSortedExclusive(currAnnot);
            } // while

            StringBuffer editableContent = new StringBuffer(originalContent);
            long insertPositionEnd;
            long insertPositionStart;
            // insert anotation tags backward
            Out.prln("Unsorted annotations count: "+ annotationsHashset.size());
            Out.prln("Sorted annotations count: "+ sortedAnnotations.size());
            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                currAnnot = (Annotation) sortedAnnotations.get(i);
                insertPositionStart =
                        currAnnot.getStartNode().getOffset().longValue();
                insertPositionStart = info.getOriginalPos(insertPositionStart);
                insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                if(insertPositionEnd != -1 && insertPositionStart != -1) {
                    editableContent.insert((int)insertPositionEnd, endTag);
                    editableContent.insert((int)insertPositionStart, startTagPart_3);
                    editableContent.insert((int)insertPositionStart,
                            currAnnot.getType());
                    editableContent.insert((int)insertPositionStart, startTagPart_2);
                    editableContent.insert((int)insertPositionStart,
                            currAnnot.getId().toString());
                    editableContent.insert((int)insertPositionStart, startTagPart_1);
                } // if
            } // for

            FileWriter writer = new FileWriter(file);
            writer.write(editableContent.toString());
            writer.close();
        } // if - should generate
        else if (originalContent != null) {
            Out.prln("Original Content. Generating file");

            Iterator it = annotationsHashset.iterator();
            Annotation currAnnot;
            SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

            while(it.hasNext()) {
                currAnnot = (Annotation) it.next();
                sortedAnnotations.addSortedExclusive(currAnnot);
            } // while

            StringBuffer editableContent = new StringBuffer(originalContent);
            long insertPositionEnd;
            long insertPositionStart;

            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                currAnnot = (Annotation) sortedAnnotations.get(i);
                insertPositionStart = currAnnot.getStartNode().getOffset().longValue();
                insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                if(insertPositionEnd != -1 && insertPositionStart != -1) {
                    editableContent.insert((int)insertPositionEnd, endTag);
                    editableContent.insert((int)insertPositionStart, startTagPart_3);
                    editableContent.insert((int)insertPositionStart,
                            currAnnot.getType());
                    editableContent.insert((int)insertPositionStart, startTagPart_2);
                    editableContent.insert((int)insertPositionStart,
                            currAnnot.getId().toString());
                    editableContent.insert((int)insertPositionStart, startTagPart_1);
                }
            }

            FileWriter writer = new FileWriter(file);
            writer.write(editableContent.toString());
            writer.close();
        }  else {
            Out.prln("Content : "+ originalContent);
            Out.prln("Repositioning: "+info);
        }

        String xmlDocument = doc.toXml(annotationsHashset, false);
        String resulteFileName = "StANNIE_toXML.HTML";
        FileWriter writer = new FileWriter(fileName);
        writer.write(xmlDocument);
        writer.close();
        return  resulteFileName;
    }

    public void setCorpus(Corpus corpus)
    {
        corpusController.setCorpus(corpus);
    }


    public static class SortedAnnotationList extends Vector
    {
        public SortedAnnotationList()
        {
            super();
        }

        public boolean addSortedExclusive(Annotation annot)
        {
            Annotation currAnnotation = null;

            // overlapping check
            for (int i=0; i<size(); ++i) {
                currAnnotation = (Annotation) get(i);
                if(annot.overlaps(currAnnotation)) {
                    return false;
                }
            }

            long annotationStart = annot.getStartNode().getOffset().longValue();
            long currentStart;

            for (int i=0; i < size(); ++i) {
                currAnnotation = (Annotation) get(i);
                currentStart = currAnnotation.getStartNode().getOffset().longValue();
                if(annotationStart < currentStart) {
                    insertElementAt(annot, i);
                    return true;
                }
            }

            int size = size();
            insertElementAt(annot, size);
            return true;
        }
    }

}
