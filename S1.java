// Java implementation of Server 1 

// Saved file as S1.class 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


// S1 class 
public class S1
{ 
	
	private long N3 = 3724116239L;

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
	//accessing voter table in the form of Hashmap and classes
	VoterData temp1 = new VoterData();
	public HashMap<String, VoterInfo> VoterTable = temp1.getVoterTable();

	//accessing Candidate table
	CandidateData temp2 = new CandidateData();
	public HashMap<String, String> CandidateTable = temp2.getCandidateTable();

	//create empty VoterCheck Table
	public static HashMap<String, VoterCheck> VoterCheckTable= new HashMap<String, VoterCheck>(); 


	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 

	//For AES encryption Decryption
	static Cipher cipher;
	private static SecretKey SharedKey = getSecretKey();


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

		//All communication with voter in this block
		while(true) 
		{ 
			try { 

				// receive packet 1
				received = dis.readUTF(); 
				String packet1 = decryptAES(received, SharedKey);
				String[] msgList = packet1.split("\\s+");
				String VoterId = msgList[1];
				long N1 = Long.parseLong(msgList[2]);

				String uniqueID;
				if(!VoterTable.get(VoterId).getUidAssigned())
				{
					do
					{
						//get random uniqueID
						uniqueID = getAlphaNumericString(10);
						//assign if uniqueID is unique
						if(!VoterCheckTable.containsKey(uniqueID))
						{
							VoterCheck voter = new VoterCheck(VoterId,uniqueID);
							VoterCheckTable.put(uniqueID, voter);
							VoterTable.get(VoterId).changeUidAssigned();
							VoterTable.get(VoterId).setUniqueId(uniqueID);
							break;
						}
					}while(true);
				}


				//sending candidate list as an hashmap object to Webserver
				ObjectOutputStream mapdos = new ObjectOutputStream(dos);
				mapdos.writeObject(CandidateTable);
				
				//Semding packet 2 
				String packet2 = getmessagePacket2(VoterTable.get(VoterId).getUniqueId(), N1);
				dos.writeUTF(packet2);

				break;
				//End of Packet 2

			 } catch (IOException e) { 
			 	e.printStackTrace(); 
			 } catch (Exception e){
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

	public static String encryptAES(String plainText, SecretKey secretKey) throws Exception 
	{
		cipher = Cipher.getInstance("AES");
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}
	
	public static String decryptAES(String encryptedText, SecretKey secretKey) throws Exception 
	{
		cipher = Cipher.getInstance("AES");
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}

	static String getAlphaNumericString(int n) 
    { 
  
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
	}
	
	public static SecretKey getSecretKey(){
		String keyStr = "012345678901234567890123456789XY";
		byte[] decodedKey = Base64.getMimeDecoder().decode(keyStr);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}

	public static String getmessagePacket2(String uniqueID, long N1) throws SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
	{
		long N2 = 3725678901L;
		String packet = "";
		String secret = getAlphaNumericString(10);
		try
		{
			packet = encryptAES(uniqueID + " " + Long.toString(N1-1) + " " + Long.toString(N2) + " " + secret , SharedKey);
		}catch(Exception e){
			e.printStackTrace();
		}

		return packet;
	}
} 
