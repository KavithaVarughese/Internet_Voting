import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;
import java.net.*; 
import java.io.*; 
import java.text.*; 
import java.util.*; 

public class MyFrame3{

    public static String output;
    private static HashMap<String,String> CandidateTable;
    public static JPanel panel = new JPanel(new GridLayout(0, 1));

    public MyFrame3(HashMap<String,String> CandidateTable){
        this.CandidateTable = CandidateTable;
    }

        public static void display(){

            JTextField field1 = new JTextField();
            JTextField field2 = new JPasswordField();
            
            panel.add(new JLabel("Enter Name of Candidate you wish to vote for . Must be exactly as mentioned in the list.\n"));
            printMenu(CandidateTable);
            panel.add(field1);

            int result = JOptionPane.showConfirmDialog(null, panel, "Test",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                output = field1.getText();
            } else {
                System.out.println("Cancelled");
            }
        }
    

	public static String getOutput(){
		return output;
    }
    
    public static void printMenu(HashMap<String,String> CandidateTable){
        int i = 1;
		for (String item: CandidateTable.keySet()) {
            panel.add(new JLabel(Integer.toString(i) + " : " + item));
			i++;
        }
	}

}