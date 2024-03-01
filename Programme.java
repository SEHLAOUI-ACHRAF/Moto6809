package moto_6809;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Programme extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private static JTextArea textArea;
    private static Highlighter highlighter;
    private static Highlighter.HighlightPainter painter;
    protected static int h = 0;

    public Programme() {
        setTitle("Programme");
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(280, 120, 250, 300);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(10, 10, 220, 250);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 10, 220, 250);
        contentPane.add(scrollPane);
        
        highlighter = textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);

    }
    
    private static void highlightLine(final int number) {
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    try {
			        int start, end;
			        if (h == 0) {
			            start = textArea.getLineStartOffset(number);
			            end = textArea.getLineEndOffset(number);
			        } else {
			            start = textArea.getLineStartOffset(number);
			            end = textArea.getLineEndOffset(number);
			            highlighter.removeAllHighlights();
			        }
			        textArea.setSelectionStart(start);
			        textArea.setSelectionEnd(end);
			        highlighter.addHighlight(start, end, painter);
			    } catch (BadLocationException e) {
			        e.printStackTrace();
			    }
			}
		});
	}

    public static void displayInstructions(int idrom, String instru_pc) {
        // Append the instruction and its idrom to the text area
    	String id = ROM.getValueOfColumn0(idrom);
        textArea.append(" " + id + "	" + instru_pc + "\n");
        highlightLine(h);
        h++;
    }

	public static void clearTextArea() {
		clearTextArea();
	}

	public static void deleteLines(int numberOfLines) {
        String text = textArea.getText();
        String[] lines = text.split("\n");

        // Ensure the requested number of lines to delete is within the range
        numberOfLines = Math.min(numberOfLines, lines.length);

        // Remove the specified number of lines
        StringBuilder newText = new StringBuilder();
        for (int i = numberOfLines; i < lines.length; i++) {
            newText.append(lines[i]).append("\n");
        }

        textArea.setText(newText.toString().trim());
    }

	public static void displayInstructions(String instru_pc) {
        textArea.append(" " + "    " + "	" + instru_pc + "\n");
        highlightLine(h);
        h++;
	}
}