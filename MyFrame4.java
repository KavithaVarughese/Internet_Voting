import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;

public class MyFrame4{

    public static String output;
    public static String secret;

    public MyFrame4(String secret){
        this.secret = secret;
    }

    public void display(){

        JFrame frame = new JFrame();
    
        // show a joptionpane dialog using showMessageDialog
        JOptionPane.showMessageDialog(frame,this.secret);
        System.exit(0);
    }


}