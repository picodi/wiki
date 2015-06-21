package parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApplicationView extends JPanel implements ActionListener
{
    protected JPanel inputPanel;
    protected JPanel outputPanel;
    protected JLabel inputLabel;
    protected JTextArea inputTextarea;
    protected JScrollPane inputPane;
    protected JButton submitButton;
    protected JLabel outputLabel;
    protected JTextArea outputTextarea;
    protected JScrollPane outputPane;
    private final static String newline = "\n";
    private static String sparqlQueryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>        \n" +
            "PREFIX type: <http://dbpedia.org/class/yago/>\n" +
            "PREFIX prop: <http://dbpedia.org/property/>\n" +
            "PREFIX db-owl: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "SELECT ?name ?pageid ?arearank\n" +
            "WHERE {\n" +
            "    ?country a dbo:Country ;\n" +
            "             rdfs:label ?name ;\n" +
            "             db-owl:capital ?capital ;\n" +
            "             db-owl:wikiPageID ?pageid ;\n" +
            "             prop:areaRank ?arearank.\n" +
            "  \n" +
            "    FILTER (?arearank > 30 && ?arearank < 40 &&\n" +
            "            langMatches(lang(?name), \"EN\")) .\n" +
            "} ORDER BY ASC(?arearank)";

    public static ApplicationManager manager = new ApplicationManager(true);

    public ApplicationView()
    {
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(600, 660));

        inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(580, 320));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Submit Query"));


        outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout());
        outputPanel.setPreferredSize(new Dimension(580, 320));
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Examine Result"));

        inputLabel = new JLabel("Input");
        outputLabel = new JLabel("Output");
        submitButton = new JButton("Submit");

        inputTextarea = new JTextArea(15, 50);
        inputTextarea.append(sparqlQueryString);
        inputPane = new JScrollPane(inputTextarea);
        submitButton.addActionListener(this);

        outputTextarea = new JTextArea(15, 50);
        outputPane = new JScrollPane(outputTextarea);
        //inputLabel.addActionListener(this);

        inputPanel.add(inputLabel);
        inputPanel.add(inputPane);
        inputPanel.add(submitButton);
        outputPanel.add(outputLabel);
        outputPanel.add(outputPane);

        add(inputPanel);
        add(outputPanel);
    }

    public void actionPerformed(ActionEvent evt)
    {
        outputTextarea.setText("");
        String providedQuery = inputTextarea.getText();
        outputTextarea.append(manager.executeQuery(providedQuery));
        this.repaint();
    }
}