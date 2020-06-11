// Java implementation for a client 
// Save file as Client.java 

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

// Client class 
public class Webserver
{ 
	//Nonce for packet 1
	private static long N1 = 5497326541L;

	//Necessities for AES Encryption
	static Cipher cipher;
	private static SecretKey SharedKey = getSecretKey();

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
			// information between Webserver and S1 server
			while (true) 
			{ 

				// Voter starting communication with packet 1
				System.out.println("Please enter your VoterID");

				// Voter enters voter Id
				String VoterID = scn.nextLine();
				// Things to be discuessed with Ritika .. till here

				//Packet 1 generation
				String packet1 = getmessagePacket1(VoterID);
				 

				//Voter Id sent to S1 
				dos.writeUTF(packet1); 

				//recieve the candidate table in the form of a hash
				// Hash map Form :
				// C0 : <candidate id>
				// you can get the unique id of candidate using CandidateTable.get("C0")
				ObjectInputStream mapdis = new ObjectInputStream(dis);
				HashMap<String, String> CandidateTable = (HashMap) mapdis.readObject();

				//Print Candidates
				printMenu(CandidateTable);
				
				// recieving packet 2
				String received = dis.readUTF(); 
				String packet2 = decryptAES(received, SharedKey);
				System.out.println(packet2);
				break; 
			} 
			
			// closing resources 
			 scn.close(); 
			 dis.close(); 
			 dos.close(); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 

	public static String getmessagePacket1(String VoterID) throws SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
	{
		String packet = "";
		try
		{
			String msg = "I_want_to_vote";
			packet = encryptAES(msg + " " + VoterID + " " + Long.toString(N1), SharedKey);
		}catch(Exception e){
			e.printStackTrace();
		}

		return packet;
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

	public static SecretKey getSecretKey(){
		String keyStr = "012345678901234567890123456789XY";
		byte[] decodedKey = Base64.getMimeDecoder().decode(keyStr);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}

	public static void printMenu(HashMap<String,String> CandidateTable){
		int i = 1;
		for (String item: CandidateTable.keySet()) {
			System.out.println( Integer.toString(i) + " : " + item);
			i++;
		}
	}

} 