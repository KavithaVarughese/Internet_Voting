import java.util.Random;
import java.net.*; 
import java.io.*; 
import java.lang.*;
import java.util.*;
import java.math.*;
public class DHServer { 



public static BigInteger binary_exponentiation(BigInteger a,BigInteger b,BigInteger p)
{
	BigInteger res = new BigInteger("1");
	BigInteger zero = new BigInteger("0");
	BigInteger two = new BigInteger("2");
	while(b.compareTo(zero) > 0)
	{
		if((b.mod(two)).compareTo(res) == 0)
		{
			res = (res.multiply(a)).mod(p);
		}
		a = (a.multiply(a)).mod(p);
		b = b.divide(two);
	}
	return res;
}

    private static BigInteger getServerKey(DataInputStream in,DataOutputStream out) throws IOException 
    { 
        try { 
            //int port = 5056; 
  
           
  
            // Client p, g, and key 
            BigInteger clientP, clientG, clientA, B, b, Bdash; 
            String Bstr; 
  
           
       // Server's Private Key 
           // System.out.println("From Server : Private Key = " + b); 
  
            // Accepts the data from client 
            //DataInputStream in = new DataInputStream(server.getInputStream()); 
  
            clientP = new BigInteger(in.readUTF()); // to accept p 
            //System.out.println("From Client : P = " + clientP); 
  
            clientG = new BigInteger(in.readUTF()); // to accept g 
            //System.out.println("From Client : G = " + clientG); 
  
            clientA = new BigInteger(in.readUTF()); // to accept A 
            //System.out.println("From Client : Public Key = " + clientA); 
		
	  
	    Random rand = new Random();
	    b = new BigInteger(33, rand);
            BigInteger range = clientP.subtract(BigInteger.valueOf(1));
	    if (b.compareTo(range) >= 0)
		b = b.mod(range).add(BigInteger.valueOf(1));
  	    //System.out.println("From Server : Private Key = " + b); 



            B = (binary_exponentiation(clientG,b,clientP)).mod(clientP); // calculation of B 
            Bstr = B.toString(); 
  	
          
  
            out.writeUTF(Bstr); // Sending B 
  
            Bdash = (binary_exponentiation(clientA,b,clientP)).mod(clientP); // calculation of Bdash 
  
            //System.out.println("Secret Key to perform Symmetric Encryption = "
                              // + Bdash); 
	    return Bdash;
            //server.close(); 
        } 
  
       
        catch (Exception e) { 
	e.printStackTrace();
        } 
return BigInteger.valueOf(-1);
    } 
public static BigInteger fetchServerKey(DataInputStream in,DataOutputStream out)
{
	try{
	BigInteger r  = getServerKey(in,out);
	return r;}
	 catch (Exception e) { 
            e.printStackTrace(); 
        } 
return BigInteger.valueOf(-1);
}
} 

