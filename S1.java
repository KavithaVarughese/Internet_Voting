// Java implementation of Server 1
// Save file as S1.class 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.security.*;
import java.lang.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SignatureException;
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

	//socket connection variables
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s;

	// For AES Encryption
	static Cipher cipher;

	//Nonces
	private long N2 = 3725678901L;

	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private static SecretKey SharedKey = getSecretKey();		//datatype needs to be created (for KAVITHA)
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private static Set<Integer> voterLst = new HashSet<Integer>();		//voterlist stored as an unordered set of UIDs
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



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
		//socket communication buffer strings
		String received; 
		String toreturn; 
		
		//authentication strings
		String accepted = "accepted";
		String rejected = "rejected";

		//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//Server Shared key
		Integer serverKey;
		//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


		//All communication with voter in this block
		while(true) 
		{ 
			//Initialize Username .. useful later on in program
			String Username = "";

			try { 
				
				//LOGIN STARTS -- We have to assume that an already existing salt is there on both sides
				//Functions for salt functionality
				SecureRandom random = new SecureRandom();
				Base64.Encoder enc = Base64.getEncoder();
				///////byte[] salt = new byte[16];

				//2-Factor authentication 
				int flag = 0,i=0;
				while(flag < 3){
					// receive VoterId
					Username = dis.readUTF();
					System.out.println("Recieved VoterId : " + Username);
					
					//Taking Salt from database and converting to bytes
					String salttemp = VoterTable.get(Username).getSalt();
					byte[] salt = salttemp.getBytes();

					//get salt to corresponding user from database(using some random thing as salt as of now)
					random.nextBytes(salt);
					
					//encode the salt to send
					String salt1 = Base64.getEncoder().encodeToString(salt);
					String message = "----------------------------Sending Salt----------------------------------------";
					System.out.println(message);

					//sending encoded salt
					dos.flush();
					dos.writeUTF(salt1);
					dos.flush();
					String hp;

					// Hashing the passwork string taken from the database
					//password accessed from database
					final String pass = VoterTable.get(Username).getPassword();

					try{
						KeySpec spec = new PBEKeySpec(pass.toCharArray(),salt,65536, 128);
						SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
						byte hash[] = f.generateSecret(spec).getEncoded();
					
						hp = enc.encodeToString(hash);
					}catch (NoSuchAlgorithmException ex){
					    throw new IllegalStateException( ex);
					}catch (InvalidKeySpecException ex){
					    throw new IllegalStateException("Invalid SecretKeyFactory", ex);
					}
				
					//receive hashed password from voter
					String Pwd = dis.readUTF();

					//checking hashkey in database with one sent by user
					//check username to the one present in the database
					if(Username.equals(VoterTable.get(Username).getVoterId()) && Pwd.equals(hp))
					{
						System.out.println("----------------------------Sending Acceptance----------------------------------------");
						dos.writeUTF(accepted);
						dos.flush();
						flag = 4;
						
					}
					else
					{
						System.out.println("----------------------------Rejecting----------------------------------------");
						dos.writeUTF(rejected);
						dos.flush();
						System.out.println("rejected");
						flag ++;
					}
						
				}
				//sending an otp to mail
				if(flag == 4)
				{
					//get email from database
					String check = TLSEmail.otp(VoterTable.get(Username).getEmail());
					//Recieves OTP
					String otp = dis.readUTF();
					if(otp.equals(check))
					{
						System.out.println("----------------------------Sending Acceptance----------------------------------------");
						dos.writeUTF(accepted);
						dos.flush();
						serverKey = DHServer.fetchServerKey(dis,dos);
						//System.out.println(serverKey);
					}
					else
					{
						System.out.println("----------------------------Rejecting----------------------------------------");
						dos.writeUTF(rejected);
						dos.flush();
					}
				}
				
				System.out.println("--------------------------Diffie Hellman Complete----------------------------");


				//PACKET 1 and 2
				// receive packet 1
				received = dis.readUTF(); 

				//decrypts the packet 1
				String packet1 = decryptAES(received, SharedKey);
				String[] msgList = packet1.split("\\s+");
				String VoterId = msgList[1];
				long N1 = Long.parseLong(msgList[2]);

				//generates uniqueId
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
				System.out.println("----------------------------Sending Candidate List----------------------------------------");
				ObjectOutputStream mapdos = new ObjectOutputStream(dos);
				mapdos.writeObject(CandidateTable);
				
				//Semding packet 2 
				System.out.println("----------------------------Sending Packet2----------------------------------------");
				String packet2 = getmessagePacket2(VoterTable.get(VoterId).getUniqueId(), N1);
				dos.writeUTF(packet2);


				// PACKET 3
				
				// receive packet 3
				received = dis.readUTF(); 
				
				//decrypting packet 3
				String Msg = decryptAES(received, SharedKey);
				msgList = Msg.split("\\s+");			//splits to a list of rsa encrypted packet, uid, dig sig and N2-1
				String UID = msgList[1];
				Long N2_mod = Long.parseLong(msgList[3]);
				//Check if nonce is correct
				//getVoteCasted returns boolean for whether the corresponding UID has casted vote or not
				//So if it returns true.. then socket should close
				if ((VoterCheckTable.get(UID).getVoteCasted()) || (N2_mod != N2-1)){
					System.out.println("Voter already voted");
					System.out.println("Refusing this connection.");
					this.s.close(); 
					System.out.println("Connection closed"); 
					break; 
				}

				//else .. set Vote Casted to true
				VoterCheckTable.get(UID).setVoteCasted();

				System.out.println("-------------------- Printing all necessary values for Fatima --------------------------------");				
				System.out.println("Data from the Voter:");
				System.out.println(UID);
				System.out.println(N2_mod);
				System.out.println(msgList[0]);
				System.out.println(msgList[2]);

				//packet goes to S2. (FOR FATHIMA)
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
				dos_S2.writeUTF("send packet communication 4 or press Exit"); 
			
				//recieve response
				received = dis_S2.readUTF(); 
				System.out.println(received); 
			
				//after recieving simply break;
				break;
				
			} 
			
			// closing resources 
			dis_S2.close(); 
			dos_S2.close(); 

		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}	//end run() 


	//External Function

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

