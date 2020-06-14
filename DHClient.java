import java.net.*; 
import java.io.*; 
import java.util.*;
import java.util.Random;
import java.lang.*;
import java.math.*;
public class DHClient { 

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



private static BigInteger getClientKey(DataInputStream in,DataOutputStream out)throws IOException//main(String[] args) 
    { 
        try { 
            String pstr, gstr, Astr; 
            //String serverName = "localhost"; 
           // int port = 5056; 
  
            // Declare p, g, and Key of client 
            BigInteger p =  BigInteger.probablePrime(33, new Random());
	     BigInteger range = p.subtract(BigInteger.valueOf(1));
	    
		//System.out.println(p);
	    
	    Random rand = new Random();
            BigInteger g = new BigInteger(33, rand);
	   	if (g.compareTo(range) >= 0)
		g = g.mod(range).add(BigInteger.valueOf(1));
		//System.out.println(g); 
            
	    BigInteger a = new BigInteger(33, rand);
	   	if (a.compareTo(range) >= 0)
		a = a.mod(range).add(BigInteger.valueOf(1));
		//System.out.println(a);  
            BigInteger Adash, serverB; 
  
            
            pstr = p.toString(); 
            out.writeUTF(pstr); // Sending p 
  
            gstr = g.toString(); 
            out.writeUTF(gstr); // Sending g 
  
            BigInteger A = (binary_exponentiation(g,a,p)).mod(p); // calculation of A 
            Astr = A.toString(); 
            out.writeUTF(Astr); // Sending A 
  
            // Client's Private Key 
            //System.out.println("From Client : Private Key = " + a); 
  
            // Accepts the data 
           // DataInputStream in = new DataInputStream(client.getInputStream()); 
  
            serverB =new BigInteger(in.readUTF()); 
            //System.out.println("From Server : Public Key = " + serverB); 
  
            Adash = (binary_exponentiation(serverB,a,p)).mod(p) ; // calculation of Adash 
  
            //System.out.println("Secret Key to perform Symmetric Encryption = "
                              // + Adash); 
	    return Adash;
            //client.close(); 
        } 
        catch (Exception e) { 
            e.printStackTrace(); 
        } 
return BigInteger.valueOf(-1);
    }

public static BigInteger fetchClientKey(DataInputStream in,DataOutputStream out)
{
	try{
	BigInteger r  = getClientKey(in,out);
	return r;}
	 catch (Exception e) { 
            e.printStackTrace(); 
        } 
 	return BigInteger.valueOf(-1);
}
/*private static boolean isPrime(int inputNum)
{
	if(inputNum <= 3 || inputNum %2 == 0)
		return inputNum == 2 || inputNum == 3;
	int divisor=3;
	while((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor!= 0))
		divisor+=2;
	return inputNum%divisor != 0;
}

private static int prime_number_generator()
{
	int num =0;
	Random rand = new Random();
	num = rand.nextInt(30) + 1;

	while(!isPrime(num))
	{
		num = rand.nextInt(30) + 1;
	}
	return num;
}*/
/*private static int generate_primes()
{
	ArrayList<Integer> arr = new ArrayList<Integer>();
	int i,j;
	for(i=2;i<=20000;i++)
	{
		int flag = 0;
		for(j=2;j*j <= i;j++)
		{
			if(i%j == 0)
			{
				flag =1;
				break;
			}
		}
		if(flag == 0)
		arr.add(i);
	}

	Random rand = new Random();
	int index = rand.nextInt(arr.size());
	Integer[] arr1 = new Integer[arr.size()];
	arr1 = arr.toArray(arr1);
	return arr1[index];
}

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
}*/
 
} 

