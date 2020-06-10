// Java implementation of Server 2
// Saved file as Server2.class

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

// S1 class 
public class S2
{ 
	public static void main(String[] args) throws IOException 
	{ 
		// S2 is listening on port 1234
        ServerSocket ss = new ServerSocket(1234); 
        
        Socket s = null; 

        
    
        String received; 
        String toreturn;
        
		while (true) 
		{ 	
		
			try
			{ 
                
                //Connected with S1
                s = ss.accept();

                // obtaining input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("S1 is connected : " + s); 

                
                
                // receive the request from S1
                received = dis.readUTF();	
                System.out.println(received);
                
                //Communication packet 5
			    dos.writeUTF("Exit"); 
            

                System.out.println("Client " + s + " sends exit..."); 
                System.out.println("Closing this connection."); 
                s.close(); 
                System.out.println("Connection closed");   
            }	

			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
        }
	} 
} 
