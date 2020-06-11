// Java implementation of Server 1 

// Saved file as S1.class 

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.security.*;
import javax.crypto.*;
import java.lang.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
		
		String accepted = "accepted";
		String rejected = "rejected";
		Integer serverKey;
		
		//All communication with voter in this block
		while (true) 
		{ 
			try { 

				// Send something to the voter
				//dos.writeUTF("Sending some information\n"+ 
							//"Type Exit to terminate connection."); 
				//LOGIN STARTS -- We have to assume that an already existing salt is there on both sides
				SecureRandom random = new SecureRandom();
				Base64.Encoder enc = Base64.getEncoder();
				byte[] salt = new byte[16];
				random.nextBytes(salt);

				String salt1 = enc.encodeToString(salt);
				// Sending the salt
				//System.out.println(salt1);
				String message = "Sending Salt";
				dos.writeUTF(message);
				dos.flush();
				dos.writeUTF(salt1);
				dos.flush();
				final String pass = "adminroot";
				//String salt2 = dis.readUTF();
				//byte[] salt2 = salt1.getBytes();
				String hp;
				try{
				KeySpec spec = new PBEKeySpec(pass.toCharArray(),salt,65536, 128);
				SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				byte hash[] = f.generateSecret(spec).getEncoded();
				
				hp = enc.encodeToString(hash);
				}
				catch (NoSuchAlgorithmException ex) {
				      throw new IllegalStateException( ex);
				    }
				    catch (InvalidKeySpecException ex) {
				      throw new IllegalStateException("Invalid SecretKeyFactory", ex);
				    }
				//System.out.println(hp);
				int flag = 0,i=0;
				while(flag < 3)
			        {
					// receive username
					String Username = dis.readUTF();
					//System.out.println(Username);
					//receive hashed password
					String Pwd = dis.readUTF();
					//System.out.println(Pwd);
					
					if(Username.equals("root") && Pwd.equals(pass))
					{
						dos.writeUTF(accepted);
						dos.flush();
						flag = 4;
						
					}
					else
					{
						dos.writeUTF(rejected);
						dos.flush();
						System.out.println("rejected");
						flag ++;
					}
					
				}
				//sending an otp
				if(flag == 4)
				{
					//Add the recipients email ID
					String check = TLSEmail.otp("ritika_b160540cs@nitc.ac.in");
					//System.out.println("OTP is:" +check);
					String otp = dis.readUTF();
					if(otp.equals(check))
					{
						dos.writeUTF(accepted);
						dos.flush();
						serverKey = DHServer.fetchServerKey(dis,dos);
						//System.out.println(serverKey);
					}
					else
					{
						dos.writeUTF(rejected);
						dos.flush();
					}
				}
				//acknowledgemnt to close socket
				received = dis.readUTF();
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
