// Java implementation of Server 2

// Save file as S2.classs


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
        
        while (true) 
		{ 
			Socket s = null; 
			
			try
			{ 
				// socket object to receive incoming voter requests 
				s = ss.accept(); 
				
				System.out.println("A new vote is connected : " + s); 
				
				// obtaining input and output streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				System.out.println("Assigning new thread for this voter"); 

				// create a new thread object 
				Thread t = new VoteHandler(s, dis, dos); 

				// Invoking the start() method 
				t.start(); 
				
			} 
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
		} 
	} 
} 


// ClientHandler class 
class VoteHandler extends Thread
{ 
	//socket connection variables
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s;

	// Constructor 
	public VoteHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
	}


	@Override
	public void run() 
	{ 
		//socket communication buffer strings
		String received; 
        String toreturn; 
        
		//All communication with S1 votes in this block
		while(true) 
		{ 
			try { 

				//PACKET 1 and 2
				// receive packet 4
                received = dis.readUTF(); 
                
                //send packet 5
                dos.writeUTF("send packet communication 5"); 
                
                //after sending break
                break;

			} //ending try

			catch (IOException e) { 
				e.printStackTrace(); 
			} catch(Exception e){
				e.printStackTrace();
			}
		}		//closing while 

		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}
		catch(IOException e){ 
			e.printStackTrace(); 
		} 
	}	//end run() 
} 
