// Java implementation of Server 1 

// Saved file as S1.class 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 


// S1 class 
public class S1
{ 
	
	

	public static void main(String[] args) throws IOException 
	{ 
		// S1 is listening on port 5056 for voter
		ServerSocket ss = new ServerSocket(5056); 
		
		// running infinite loop for getting 
		// voter vote  
		while (true) 
		{ 
			Socket s = null; 
			
			try
			{ 
				// socket object to receive incoming voter requests 
				s = ss.accept(); 
				
				System.out.println("A new voter is connected : " + s); 
				
				// obtaining input and output streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				System.out.println("Assigning new thread for this voter"); 

				// create a new thread object 
				Thread t = new VoterHandler(s, dis, dos); 

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
class VoterHandler extends Thread
{ 
	//creating voter table in the form of Hashmap and classes
	public static HashMap<String, VoterInfo> VoterTable = new HashMap<>();

	//creating Candidate table
	public static HashMap<String, String> CandidateTable = new HashMap<String, String>();

	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 
	
	//Candidate Table Creation
	public void createCandidateTable() {
		CandidateTable.put("C0", "ahufhuwjekhkjfahsdhaufhiu24hjk");
		CandidateTable.put("C1", "ent245hjdn7kj2h348jshfkakn5n54");
		CandidateTable.put("C2", "oi98jhghjg6uaghevhj87435jhk8or");
		CandidateTable.put("C3", "32j4hhuisucjkhjhds874753lhuh82");
	}

	// Constructor 
	public VoterHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
	} 

	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
		createCandidateTable();

		//All communication with voter in this block
		while (true) 
		{ 
			try { 
				// PACKET 1

				// receive the Voter Id
				// Incorporate with Ritika
				// Forming Voter Table
				received = dis.readUTF(); 
				
				if(!VoterTable.containsKey(received))
				{
					VoterInfo voter = new VoterInfo(received,5000);
					VoterTable.put(received, voter);
				}

				ObjectOutputStream mapdos = new ObjectOutputStream(dos);
				mapdos.writeObject(CandidateTable);
				
				break;
			// 	//acknowledgemnt to close socket
			// 	if(received.equals("Exit")) 
			// 	{ 
			// 		System.out.println("Client " + this.s + " sends exit..."); 
			// 		System.out.println("Closing this connection."); 
			// 		this.s.close(); 
			// 		System.out.println("Connection closed"); 
			// 		break; 
			// 	} 
				
			// 	// write on output stream
			// 	// response to the voter
			// 	dos.writeUTF("Response from S1");

			 } catch (IOException e) { 
			 	e.printStackTrace(); 
			 } 
		} 
		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e)
		{ 
			e.printStackTrace(); 
		} 

		// All communication with S2 from here onwards

		try
		{
			//Set up socket for connecting to S2
			// getting localhost ip 
			InetAddress ip_S2 = InetAddress.getByName("localhost");
			// establish the connection with server port 1234
			Socket s_S2 = new Socket(ip_S2, 1234);

			// obtaining input and out streams 
			DataInputStream dis_S2 = new DataInputStream(s_S2.getInputStream()); 
			DataOutputStream dos_S2 = new DataOutputStream(s_S2.getOutputStream());

			//set up communication with S2 in this block
			while (true) 
			{ 
				//send request to S2
				dos_S2.writeUTF("send packet communication 4"); 
			
				//recieve response
				received = dis_S2.readUTF(); 
				System.out.println(received); 

				// If S1 sends exit,close this connection 
				// and then break from the while loop 
				if(received.equals("Exit")) 
				{ 
					System.out.println("Closing this connection : " + s_S2); 
					s_S2.close(); 
					System.out.println("Connection closed"); 
					break; 
				} 
				
				// printing date or time as requested by client 
				
			} 
			
			// closing resources 
			dis_S2.close(); 
			dos_S2.close(); 

		}catch(Exception e){ 
			e.printStackTrace(); 
		} 

	} 
} 
