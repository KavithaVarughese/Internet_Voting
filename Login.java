import java.io.Console;
import java.security.*;
import java.util.*;
import java.io.*; 
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
import java.net.*; 
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
public class Login
{
	

   public static String authenticate(DataInputStream dis,DataOutputStream dos) {   
   
    	Console cnsl = null;
    	String name = null;
      
      	try {
        
        	cnsl = System.console();  // creates a console object
		 	if (cnsl != null) // if console is not null
		 	{
	       		int i = 3;

				while(i>0)
				{
			
					i=i-1;
		    
		    		//enter name
		    		name = cnsl.readLine("Enter VoterId : B160779CS\n"); // read line from the user input
			
					//enter password
					System.out.println("Enter password : kavitha_pwd");
					char[] pwd = cnsl.readPassword(); // read password into the char array
					final String pass = String.valueOf(pwd);
			
					//sending VoterId
					System.out.println("--------------------- SENDING VOTERID ----------------------");
					dos.writeUTF(name);
					dos.flush();

					//Reading encoded  Salt
					String salt1 = dis.readUTF();
			
					//decoding read salt
					byte[] salt = Base64.getDecoder().decode(salt1) ;


					//hashing password with salt sent by server
					String hp;
					try{
						KeySpec spec = new PBEKeySpec(pass.toCharArray(),salt,65536, 128);
						SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
						byte hash[] = f.generateSecret(spec).getEncoded();
						Base64.Encoder enc = Base64.getEncoder();
						hp = enc.encodeToString(hash);
					}catch (NoSuchAlgorithmException ex) {
						throw new IllegalStateException(ex);
				    }catch (InvalidKeySpecException ex) {
				    	throw new IllegalStateException("Invalid SecretKeyFactory", ex);
				    }


			 		//send hashed password 
					System.out.println("-----------------------------Sending hashed password------------------------------------");
					dos.writeUTF(hp);
					dos.flush();


					//response from server
					String stat2 = dis.readUTF();
					if(stat2.equals("rejected"))
						System.out.println("Please try again");
					else
					{
						System.out.println("Check The registered mail for OTP");
						System.out.println("Enter the OTP");
						String otp = cnsl.readLine();
						//sending server otp;
						System.out.println("-----------------------------Sending OTP------------------------------------");
						dos.writeUTF(otp);
		                String result = dis.readUTF();
						if(result.equals("accepted"))
							return "accepted";
						else
							return "rejected";
					}
				}
				if(i==0)
				{

					System.out.println("Trials exhausted");
					return "rejected";
				}
		
        	} 
			else
			{
				System.out.println("Trials exhausted");
				return "rejected";
			}
         
      	}catch(Exception ex) {
         	// if any error occurs
         	ex.printStackTrace();      
        }
		
		System.out.println("Trials exhausted");
		return "rejected";
   }
}
	
