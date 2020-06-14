// Java implementation for a client 
// Save file as Client.java 
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.math.*;
import java.util.Scanner; 
import javax.crypto.spec.SecretKeySpec;

// Client class 
public class Webserver
{ 	

	private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
	private static long N1 = 5497326541L;
	
	//Necessities for AES Encryption
	static Cipher cipher;
	//private static SecretKey SharedKey;// = getSecretKey();				//datatype to be created (for KAVITHA)


	public static void main(String[] args) throws IOException 
	{ BigInteger clientKey =  new BigInteger("1");
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
				// 2-Factor authentication
				String res = Login.authenticate(dis,dos);

				//System.out.println(res); 
				//Diffie Hellman Exchange
				if(res.equals("accepted"))
				{
					clientKey = DHClient.fetchClientKey(dis, dos);
					//System.out.println(clientKey);
				}
				else
				{
					System.out.println("Please contact the Election Authority");
					break;
				}

				System.out.println("--------------------------Diffie Hellman Complete----------------------------");

				SecretKey SharedKey = getSecretKey(clientKey);
				//PACKET 1

				// Voter starting communication with packet 1
				System.out.println("Please enter your VoterID : B160779CS [This step is redundant ... Has to be removed]");

				// Voter enters voter Id
				String VoterID = scn.nextLine();
				// Things to be discuessed with Ritika .. till here

				//PACKET 1 and 2

				//Packet 1 generation
				String packet1 = getmessagePacket1(VoterID, SharedKey);
				 

				//Packet 1 sent to S1 
				System.out.println("----------------------------Sending packet 1----------------------------------------");
				dos.writeUTF(packet1); 

				//recieve the candidate table in the form of a hash
				// Hash map Form :
				// C0 : <candidate id>
				// you can get the unique id of candidate using CandidateTable.get("C0")
				ObjectInputStream mapdis = new ObjectInputStream(dis);
				HashMap<String, String> CandidateTable = (HashMap) mapdis.readObject();

				//Print Candidates
				printMenu(CandidateTable);

				//casting the vote
				System.out.println("Enter Name of Candidate you wish to vote for . Must be exactly as mentioned in the list.");
				String CID;
				do{
					String Vote = scn.nextLine();
					if(CandidateTable.containsKey(Vote)){
						CID = CandidateTable.get(Vote);
						break;
					}
					else{
						System.out.println("Please mention the candidate name exactly as mentioned in the list.");
					}
				}while(true);
				
				// recieving packet 2
				String received = dis.readUTF(); 
				
				//SecretKey SharedKey = getSecretKey(clientKey);
				//decrypts the packet 2
				String packet2 = decryptAES(received, SharedKey);
				String[] msgList = packet2.split("\\s+");
				String UID = msgList[0];
				long N1_mod = Long.parseLong(msgList[1]);
				long N2 = Long.parseLong(msgList[2]);
				String secret = msgList[3];

				//Check if nonce 1 is correct
				if ((N1_mod != N1-1)){
					System.out.println("Voter already voted");
					System.out.println("Refusing this connection.");
					System.out.println("Connection closed"); 
					break; 
				}
				
				//PACKET 3

				// sending packet 3
				System.out.println("----------------------------Sending Packet3----------------------------------------");
				String tosend = getmessagePacket3(CID, secret, UID, N2, SharedKey);
				dos.writeUTF(tosend);
				
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


	//External Functions .....

	public static String getmessagePacket3(String CID, String secret, String UID, long N2,  SecretKey SharedKey) throws SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
	{													//mod fn header here if changing the key
		String Msg = "";
		try{
			Msg = CID + " " + secret;
			Msg = Base64.getEncoder().encodeToString(encryptRSA(Msg, publicKey)) + " " + UID;
			N2 = N2 - 1;
			Msg = encryptAES(Msg + " " + Base64.getEncoder().encodeToString(digSignatureRSA(Msg)) + " " + Long.toString(N2), SharedKey);
		}catch(Exception e){
			e.printStackTrace();
		}
		// Msg = Msg + " " + Base64.getEncoder().encodeToString(digSignatureRSA(Msg)) + " " + Integer.toString(N2);
		return Msg;
	}

	
	public static PublicKey getPublicKeyRSA(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

	public static byte[] encryptRSA(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKeyRSA(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static byte[] digSignatureRSA(String Msg) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, SignatureException{
    	KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
      
	    //Initializing the key pair generator
	    keyPairGen.initialize(2048);
	      
	    //Generate the pair of keys
	    KeyPair pair = keyPairGen.generateKeyPair();
	      
	    //Getting the private key from the key pair
	    PrivateKey privKey = pair.getPrivate();
	      
	    //Creating a Signature object
	    Signature sign = Signature.getInstance("SHA256withDSA");
	      
	    //Initialize the signature
	    sign.initSign(privKey);
	    byte[] bytes = Msg.getBytes();
	      
	    //Adding data to the signature
	   	sign.update(bytes);
	      
	    //Calculating the signature
	    byte[] signature = sign.sign();

	    return signature;
    }

	public static String getmessagePacket1(String VoterID, SecretKey SharedKey) throws SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
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
	public static SecretKey getSecretKey(BigInteger clientKey){

		String key = clientKey.toString();
		int length = key.length();
		int i = 0;
		String keyStr = "012345678901234567890123456789XY";
		for(i= length; i < keyStr.length();i++)
		{
			char x = keyStr.charAt(i);
			key = key + String.valueOf(x);
		}
		
		byte[] decodedKey = Base64.getMimeDecoder().decode(key);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		System.out.println(secretKey);
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

