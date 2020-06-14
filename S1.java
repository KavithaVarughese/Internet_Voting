// Java implementation of Server 1
// Save file as S1.class 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.security.*;
import java.lang.*;
import java.math.*;
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
	//accessing voter table in the form of Hashmap and classes
	static VoterData temp1 = new VoterData();
	public static HashMap<String, VoterInfo> VoterTable = temp1.getVoterTable();

	//accessing Candidate table
	static CandidateData temp2 = new CandidateData();
	public static HashMap<String, String> CandidateTable = temp2.getCandidateTable();

	//create empty VoterCheck Table
	public static HashMap<String, VoterCheck> VoterCheckTable= new HashMap<String, VoterCheck>();
	
	//aes secret key for S2 Server
	public static SecretKey getSecretKeyS2(){
		String keyStr = "012345678901234567890123456789XY";
		byte[] decodedKey = Base64.getMimeDecoder().decode(keyStr);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}

	public static void main(String[] args) throws IOException 
	{ 
		// S1 is listening on port 5056 for voter
		ServerSocket ss = new ServerSocket(5056); 
		
		// running infinite loop for getting voter vote  
		while (true){ 
			Socket s = null; 
			
			try{ 
				// socket object to receive incoming voter requests 
				s = ss.accept(); 
				
				// obtaining input and output streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 

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
//
// ClientHandler class 
//
class VoterHandler extends Thread{

	//socket connection variables
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s;

	// For AES Encryption
	static Cipher cipher;

	//Nonces
	private long N2 = 3725678901L;

	// Constructor 
	public VoterHandler(Socket s, DataInputStream dis, DataOutputStream dos){ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
	}

	@Override
	public void run(){ 

		BigInteger serverKey = new BigInteger("1");
		//socket communication buffer strings
		String received; 
		String toreturn; 
		String vote = new String();
		
		//authentication strings
		String accepted = "accepted";
		String rejected = "rejected";

		//Initialize Username .. useful later on in program
		String Username = "";
		int flagS2 = 0;

		try{ 
			
			//LOGIN STARTS -- We have to assume that an already existing salt is there on both sides
			//Functions for salt functionality
			SecureRandom random = new SecureRandom();
			Base64.Encoder enc = Base64.getEncoder();

			//2-Factor authentication 
			int flag = 0,i=0;
			
			while(flag < 3){
				// receive VoterId
				Username = dis.readUTF();
				System.out.println("Recieved VoterId : " + Username);
				if(S1.VoterTable.get(Username).getUidAssigned()){

					dos.writeUTF(rejected);
					dos.flush();
					try{ 
						// closing resources 
						this.s.close();
						this.dis.close(); 
						this.dos.close();
						System.out.println("Connection is broken");
						flagS2 = 1;
						break;
					}catch(IOException e){ 
						e.printStackTrace(); 
					}
				}

				//Taking Salt from database and converting to bytes
				String salttemp = S1.VoterTable.get(Username).getSalt();
				byte[] salt = salttemp.getBytes();

				//get salt to corresponding user from database(using some random thing as salt as of now)
				random.nextBytes(salt);
				
				//encode the salt to send
				String salt1 = Base64.getEncoder().encodeToString(salt);
				String message = "----------------------------Sending Salt----------------------------------------";
				dos.writeUTF(message);
				//sending encoded salt
				dos.flush();
				dos.writeUTF(salt1);
				dos.flush();
				String hp;

				// Hashing the passwork string taken from the database
				//password accessed from database
				final String pass = S1.VoterTable.get(Username).getPassword();

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
				if(Username.equals(S1.VoterTable.get(Username).getVoterId()) && Pwd.equals(hp)){

					dos.writeUTF(accepted);
					dos.flush();
					flag = 4;
					
				}else{
					dos.writeUTF(rejected);
					dos.flush();
					System.out.println("rejected");
					flag ++;
				}		
			}
			
			//sending an otp to mail
			if(flag == 4){
				//get email from database
				String check = TLSEmail.otp(S1.VoterTable.get(Username).getEmail());
				//Recieves OTP
				String otp = dis.readUTF();
				if(otp.equals(check)){

					dos.writeUTF(accepted);
					dos.flush();
					serverKey = DHServer.fetchServerKey(dis,dos);

				}else{
					dos.writeUTF(rejected);
					dos.flush();
				}
			}

			if(flagS2 != 1)
			{
				//PACKET 1 and 2
				// receive packet 1
				received = dis.readUTF(); 
				System.out.println("\n----------------------------Receive Packet1-------------------------------");

				SecretKey SharedKey = getSecretKey(serverKey);
				//decrypts the packet 1
				String packet1 = decryptAES(received, SharedKey);
				String[] msgList = packet1.split("\\s+");
				String VoterId = msgList[1];
				long N1 = Long.parseLong(msgList[2]);
		
				//generates uniqueId
				String uniqueID;
				if(!S1.VoterTable.get(VoterId).getUidAssigned()){
					do{
						//get random uniqueID
						uniqueID = getAlphaNumericString(10);
						//assign if uniqueID is unique
						if(!S1.VoterCheckTable.containsKey(uniqueID)){
							VoterCheck voter = new VoterCheck(VoterId,uniqueID);
							S1.VoterCheckTable.put(uniqueID, voter);
							S1.VoterTable.get(VoterId).changeUidAssigned();
							S1.VoterTable.get(VoterId).setUniqueId(uniqueID);
							break;
						}
					}while(true);
				}


				//sending candidate list as an hashmap object to Webserver
				ObjectOutputStream mapdos = new ObjectOutputStream(dos);
				mapdos.writeObject(S1.CandidateTable);
				
				//Semding packet 2 
				System.out.println("----------------------------Sending Packet2----------------------------------------");
				String packet2 = getmessagePacket2(S1.VoterTable.get(VoterId).getUniqueId(), N1, SharedKey);
				dos.writeUTF(packet2);

				// PACKET 3
				
				// receive packet 3
				received = dis.readUTF(); 
				System.out.println("\n----------------------------Receive Packet3-------------------------------");

				//decrypting packet 3
				String Msg = decryptAES(received, SharedKey);
				msgList = Msg.split("\\s+");			//splits to a list of rsa encrypted packet, uid, dig sig and N2-1
				String UID = msgList[1];
				Long N2_mod = Long.parseLong(msgList[3]);
				//Check if nonce is correct
				//getVoteCasted returns boolean for whether the corresponding UID has casted vote or not
				//So if it returns true.. then socket should close
				if ((S1.VoterCheckTable.get(UID).getVoteCasted()) || (N2_mod != N2-1)){
					System.out.println("Voter already voted");
					System.out.println("Refusing this connection.");
					this.s.close(); 
					System.out.println("Connection closed"); 
					flagS2 = 1;
				}
				//else .. set Vote Casted to true
				S1.VoterCheckTable.get(UID).setVoteCasted();
				vote = msgList[0];
			}	
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch(Exception e){
				e.printStackTrace();
		}
		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
		
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 


		// All communication with S2 from here onwards

		if (flagS2 != 1)
		{
			try{
				


				//Set up socket for connecting to S2
				// getting localhost ip
				InetAddress ip_S2 = InetAddress.getByName("localhost");
				// establish the connection with server port 1235
				Socket s_S2 = new Socket(ip_S2, 1234);
				// obtaining input and out streams
				DataInputStream dis_S2 = new DataInputStream(s_S2.getInputStream());
				DataOutputStream dos_S2 = new DataOutputStream(s_S2.getOutputStream());
				//set up communication with S2 in this block
				//send request to S2

				long N3 = 34555;
				String toSendtoS2 = EncryptionDecryptionAES.encrypt(vote+" "+Long.toString(N3),S1.getSecretKeyS2());
				dos_S2.writeUTF(toSendtoS2);
				System.out.println("\n---------------------Sent Packet4---------------------");

				//recieve response
				received = dis_S2.readUTF();

				if(Long.parseLong(received)+1 == N3){
					System.out.println("\n---------------------Received Packet5---------------------");
					
				}else{
					System.out.println("\nWrong nonce. Vote is not counted");
				}
				
				dis_S2.close();
				dos_S2.close();
				s_S2.close();
					
			}catch(Exception e){ 
				e.printStackTrace(); 
			} 
		}	
	}		//end run() 

	//
	//External Function

	public static String encryptAES(String plainText, SecretKey secretKey) throws Exception{
		cipher = Cipher.getInstance("AES");
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}
	
	public static String decryptAES(String encryptedText, SecretKey secretKey) throws Exception{
		cipher = Cipher.getInstance("AES");
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		String decryptedText = new String(decryptedByte);
		return decryptedText;
	}

	static String getAlphaNumericString(int n){ 
  
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++){ 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index = (int)(AlphaNumericString.length() * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString.charAt(index)); 
        } 
  
        return sb.toString(); 
	}
	
	public static SecretKey getSecretKey(BigInteger serverKey){
		String key = serverKey.toString();
		int length = key.length();
		int i = 0;
		String keyStr = "012345678901234567890123456789XY";
		for(i= length; i < keyStr.length();i++){
			char x = keyStr.charAt(i);
			key = key + String.valueOf(x);
		}

		byte[] decodedKey = Base64.getMimeDecoder().decode(key);
		
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		System.out.println(secretKey);
		return secretKey;
	}

	public static String getmessagePacket2(String uniqueID, long N1, SecretKey SharedKey) throws SignatureException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException{
	
		long N2 = 3725678901L;
		String packet = "";
		String secret = getAlphaNumericString(10);
		try{
			packet = encryptAES(uniqueID + " " + Long.toString(N1-1) + " " + Long.toString(N2) + " " + secret , SharedKey);
		}catch(Exception e){
			e.printStackTrace();
		}
		return packet;
	}
} 

