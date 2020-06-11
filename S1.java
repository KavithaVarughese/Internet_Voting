// Java implementation of Server 1
// Save file as S1.class 

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
				System.out.println("We are back here???"); 
				
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
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s;
	// private SecretKey SharedKey;		//datatype needs to be created (for KAVITHA)
	
	private static Set<Integer> voterLst = new HashSet<Integer>();		//voterlist stored as an unordered set of UIDs
	private int N2 = 1234567890;

	// Constructor 
	public VoterHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
	}

	public static String decryptAES(String encryptedText, SecretKey secretKey)
			throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}



	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
		
		//All communication with voter in this block
		while (true) 
		{ 
			try { 

				// Send something to the voter
				dos.writeUTF("Sending some information\n"+ 
							"Type Exit to terminate connection."); 
				
				// receive the response from voter
				received = dis.readUTF(); 

				//------------------------------------------------------------------------------
				//packet 3 receiving code

				String Msg = decryptAES(received, SharedKey);
				String[] msgList = Msg.split("\\s+");			//splits to a list of rsa encrypted packet, uid, dig sig and N2-1

				int UID = Integer.valueOf(msgList[1]);
				int N2_mod = Integer.valueOf(msgList[3]);

				if (voterLst.contains(UID) || (N2_mod != N2-1)){
					System.out.println("Voter already voted");
					System.out.println("Refusing this connection."); 
					this.s.close(); 
					System.out.println("Connection closed"); 
					break; 
				}

				else{

					voterLst.add(UID);			//adding the new voter to the voter set

					System.out.println("Data from the Voter:");
					System.out.println(UID);
					System.out.println(N2_mod);
					System.out.println(msgList[0]);
					System.out.println(msgList[2]);

						//packet goes to S2.





				//------------------------------------------------------------------------------
					
					//acknowledgemnt to close socket
					if(received.equals("Exit")) 
					{ 
						System.out.println("Client " + this.s + " sends exit..."); 
						System.out.println("Closing this connection."); 
						this.s.close(); 
						System.out.println("Connection closed"); 
						break; 
					} 
					
					// write on output stream
					// response to the voter
					dos.writeUTF("Response from S1");
				}

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
