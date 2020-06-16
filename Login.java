import java.io.Console;
import java.security.*;
import java.util.*;
import java.io.*; 
import javax.crypto.*;
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
import java.net.*; 
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.awt.*;

public class Login
{
	public static int count = 0;

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

					MyFrame1 Frame1 = new MyFrame1(); // Asking username and password GUI
					Frame1.display();
					String output = Frame1.getOutput();
					String[] msgList = output.split("\\s+");
					name = msgList[0];
					String pwd = msgList[1];

					final String pass = String.valueOf(pwd);
			
					//sending VoterId
					dos.writeUTF(name);
					dos.flush();
						
					//Reading encoded  Salt message
					String salt1 = dis.readUTF();
					if(salt1.equals("rejected"))
						return "rejected";
					//Reading encoded Salt
					salt1 = dis.readUTF();
			
					//decoding read salt
					byte[] salt = Base64.getDecoder().decode(salt1) ;


					//hashing password with salt sent by server
					String hp;
					try{
						KeySpec spec = new PBEKeySpec(pass.toCharArray(),salt,65536, 128);
						SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
						byte hash[] = f.generateSecret(spec).getEncoded();
						Base64.Encoder enc = Base64.getEncoder();
						hp = enc.encodeToString(hash);
					}catch (NoSuchAlgorithmException ex) {
						throw new IllegalStateException(ex);
				    }catch (InvalidKeySpecException ex) {
				    	throw new IllegalStateException("Invalid SecretKeyFactory", ex);
				    }


			 		//send hashed password 
					dos.writeUTF(hp);
					dos.flush();


					//response from server
					String stat2 = dis.readUTF();
					if(stat2.equals("rejected"))
						System.out.println("Please try again");
					else
					{
						MyFrame2 Frame2 = new MyFrame2(); // Asking username and password
						Frame2.display();
						output = Frame2.getOutput();
						String otp = output;

						dos.writeUTF(otp);
		                String result = dis.readUTF();
						if(result.equals("accepted"))
						{
							String res = "accepted" + name;	
							return res;}
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
	
