package moto_6809;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ROM extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static DefaultTableModel model = new DefaultTableModel();

	public ROM() {
		setTitle("ROM");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(750, 120, 212, 276);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setLayout(new BorderLayout(0, 0));
		
		

		model.addColumn("Address");
        model.addColumn("Data");
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
    	for (int i = 5120; i < 6144; i++) {
                String address = intToHex(i); // Convert decimal to hexadecimal
                model.addRow(new Object[]{address, "FF"});
            }
	}
	
	static void setStringToColumn1(int rowIndex, String value) {
        int size = value.length();
        if (size == 2) {
        	if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
                model.setValueAt(value, rowIndex, 1); // Column 1 is index 1
            } else {
                System.out.println("Invalid row index");
            }
        }
        else {
        	if (rowIndex >= 0 && rowIndex < model.getRowCount()) {
        		String value1 = value.substring(0, 2);
        		String value2 = value.substring(2);
        		model.setValueAt(value1, rowIndex, 1);
        		rowIndex++;
        		model.setValueAt(value2, rowIndex, 1); // Column 1 is index 1
            } else {
                System.out.println("Invalid row index");
            }
        }
    }
	
	private static String intToHex(int value) {
        // Convert to hexadecimal
        String hexValue = Integer.toHexString(value).toUpperCase();

        // Pad with leading zeros to ensure a fixed width of 4 characters
        while (hexValue.length() < 4) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }
	public static String getValueOfColumn0(int idrom) {
		if (idrom >= 0 && idrom < model.getRowCount()) {
            return (String) model.getValueAt(idrom, 0); // Column 0 is index 0
        } else {
            System.out.println("Invalid row index");
            return null;
        }
	}
	public static void ved_rom() {
		for (int i = 5120; i < 6144; i++) {
            String address = intToHex(i); // Convert decimal to hexadecimal
            model.addRow(new Object[]{address, "FF"});
        }
	}
}