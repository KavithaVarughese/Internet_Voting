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
		
			String salt2 = dis.readUTF();
			//System.out.println(salt2);
			String salt1 = dis.readUTF();
			//System.out.println(salt1);
			while(i>0)
                	{  
			     i = i-1;
		        name = cnsl.readLine("Enter Name: "); // read line from the user input
			System.out.println("Enter password");
			char[] pwd = cnsl.readPassword(); // read password into the char array
			final String pass = String.valueOf(pwd);
			SecureRandom random = new SecureRandom();
			String hp;
			//byte[] salt = new byte[16];
			//random.nextBytes(salt);
			//Reading Salt
			
			byte[] salt = salt1.getBytes();
			try{
			KeySpec spec = new PBEKeySpec(pass.toCharArray(),salt,65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte hash[] = f.generateSecret(spec).getEncoded();
			Base64.Encoder enc = Base64.getEncoder();
			 hp = enc.encodeToString(hash);}
			catch (NoSuchAlgorithmException ex) {
			      throw new IllegalStateException(ex);
			    }
			    catch (InvalidKeySpecException ex) {
			      throw new IllegalStateException("Invalid SecretKeyFactory", ex);
			    }
			//String salt1 = enc.encodeToString(salt);
			//Sending User Name
			dos.writeUTF(name);
			dos.flush();
			//Sending Hashed password
			//dos.writeUTF(hp);
			dos.writeUTF(pass);
			dos.flush();
			//System.out.println(hp);
			//Receiving status
			//String stat = dis.readUTF();
			String stat2 = dis.readUTF();
			//System.out.println("Stat "+stat2);
			if(stat2.equals("rejected"))
			System.out.println("Please try again");
			else
			{
				System.out.println("Check The registered mail for OTP");
				System.out.println("Enter the OTP");
				String otp = cnsl.readLine();
				//sending server otp;
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
	{System.out.println("Trials exhausted");
		return "rejected";}
         
      } 
	catch(Exception ex) 
	{
         // if any error occurs
         ex.printStackTrace();      
        }
	System.out.println("Trials exhausted");
		return "rejected";
   }
}
	
