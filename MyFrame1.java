import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;

class MyFrame1 {

	public static String output;

    public static void display(){

        JTextField field1 = new JTextField();
        JTextField field2 = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username"));
        panel.add(field1);
        panel.add(new JLabel("Password"));
        panel.add(field2);
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            output = field1.getText()+ " " + field2.getText();
        } else {
            System.out.println("Cancelled");
        }
    }

	public static String getOutput(){
		return output;
	}

}