// Java implementation for a client 
// Save file as Client.java 
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

// Client class 
public class Webserver
{ 	

	private int CID = 090983293902;			//to be changed with a method
	private int secret =097099090808;
	private int UID = 9012380912839;
	private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
	private int N2 = 1234567890;

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
				System.out.println(dis.readUTF()); 
				// String tosend = scn.nextLine();

				// code to obtain the message (packet3)------------------------------- 

				String tosend = makeMsg();
				dos.writeUTF(tosend);

				//--------------------------------------------------------------------
				
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

	public String makeMsg(){													//mod fn header here if changing the key
		String Msg = Integer.toString(CID) + " " + Integer.toString(secret);
		Msg = Base64.getEncoder().encodeToString(encryptRSA(Msg, publicKey)) + " " + Integer.toString(UID);
		N2 = Integer.toString(N2 - 1);
		Msg = encryptAES(Base64.getEncoder().encodeToString(digSignatureRSA(Msg)) + " " + Integer.toString(N2));
		return Msg;
	}

	public static byte[] encryptRSA(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static byte[] digSignatureRSA(String Msg){
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

	    return signature
    }

    public static String encryptAES(String plainText, SecretKey secretKey)
			throws Exception {
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}


} 
