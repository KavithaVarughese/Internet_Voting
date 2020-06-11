import java.util.Random;
import java.net.*; 
import java.io.*; 
import java.lang.*;
public class DHServer { 


public static Integer binary_exponentiation(Integer a,Integer b,Integer p)
{
	Integer res = 1;
	while(b > 0)
	{
		if(b%2 == 1)
		{
			res = (res * a)%p;
		}
		a = (a*a)%p;
		b = b/2;
	}
	return res;
}


    private static Integer getServerKey(DataInputStream in,DataOutputStream out) throws IOException 
    { 
        try { 
            //int port = 5056; 
  
           
  
            // Client p, g, and key 
            Integer clientP, clientG, clientA, B, Bdash; 
            String Bstr; 
  
           
       // Server's Private Key 
           // System.out.println("From Server : Private Key = " + b); 
  
            // Accepts the data from client 
            //DataInputStream in = new DataInputStream(server.getInputStream()); 
  
            clientP = Integer.parseInt(in.readUTF()); // to accept p 
            //System.out.println("From Client : P = " + clientP); 
  
            clientG = Integer.parseInt(in.readUTF()); // to accept g 
            //System.out.println("From Client : G = " + clientG); 
  
            clientA = Integer.parseInt(in.readUTF()); // to accept A 
            //System.out.println("From Client : Public Key = " + clientA); 
		
	   //int p1 = (int)clientP;
	    Random rand = new Random();
	    Integer b = rand.nextInt(clientP);
  	    //System.out.println("From Server : Private Key = " + b); 
            B = (binary_exponentiation(clientG,b,clientP)) % clientP; // calculation of B 
            Bstr = Integer.toString(B); 
  	
          
  
            out.writeUTF(Bstr); // Sending B 
  
            Bdash = (binary_exponentiation(clientA,b,clientP)) % clientP; // calculation of Bdash 
  
            //System.out.println("Secret Key to perform Symmetric Encryption = "
                              // + Bdash); 
	    return Bdash;
            //server.close(); 
        } 
  
       
        catch (Exception e) { 
	e.printStackTrace();
        } 
return -1;
    } 
public static Integer fetchServerKey(DataInputStream in,DataOutputStream out)
{
	try{
	Integer r  = getServerKey(in,out);
	return r;}
	 catch (Exception e) { 
            e.printStackTrace(); 
        } 
return -1;
}
} 

