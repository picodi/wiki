package parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JPanel implements ActionListener
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

    public View()
    {
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(580, 520));

        inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(500, 250));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Submit Query"));


        outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout());
        outputPanel.setPreferredSize(new Dimension(500, 220));
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Examine Result"));

        inputLabel = new JLabel("Input");
        outputLabel = new JLabel("Output");
        submitButton = new JButton("Submit");

        inputTextarea = new JTextArea(10, 40);
        inputPane = new JScrollPane(inputTextarea);

        outputTextarea = new JTextArea(10, 40);
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

    public void actionPerformed(ActionEvent evt) {
        String text = inputLabel.getText();
        inputTextarea.append(text + newline);
        //inputLabel.selectAll();

        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        inputTextarea.setCaretPosition(inputTextarea.getDocument().getLength());
    }

    public static void main(String[] args)
    {

        JFrame frame = new JFrame("SentenceIdentifier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new View());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}