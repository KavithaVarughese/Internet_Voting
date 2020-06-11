// Java implementation for a client 
// Save file as Client.java 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

// Client class 
public class Webserver
{ 
	public static void main(String[] args) throws IOException 
	{ 
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			// getting localhost ip 
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			// establish the connection with server port 5056 
			Socket s = new Socket(ip, 5056); 
	
			// obtaining input and out streams 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	
			// the following loop performs the exchange of 
			// information between client and client handler 
			while (true) 
			{ 

				// Voter starting communication
				System.out.println("Please enter your VoterID");

				// Voter enters voter Id
				String tosend = scn.nextLine(); 

				//Voter Id sent to S1 
				dos.writeUTF(tosend); 

				//recieve the candidate table in the form of a hash
				// Hash map Form :
				// C0 : <candidate id>
				// you can get the unique id of candidate using CandidateTable.get("C0")
				ObjectInputStream mapdis = new ObjectInputStream(dis);
				HashMap<String, String> CandidateTable = (HashMap) mapdis.readObject();

				//Print Candidates
				int i = 1;
				for (String item: CandidateTable.keySet()) {
					System.out.println( Integer.toString(i) + " : " + item);
					i++;
				}
				
				// If client sends exit,close this connection 
				// and then break from the while loop 
				if(tosend.equals("Exit")) 
				{ 
					System.out.println("Closing this connection : " + s); 
					s.close(); 
					System.out.println("Connection closed"); 
					break; 
				} 
				
				// printing date or time as requested by client 
				String received = dis.readUTF(); 
				System.out.println(received); 
			} 
			
			// closing resources 
			scn.close(); 
			dis.close(); 
			dos.close(); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
} 