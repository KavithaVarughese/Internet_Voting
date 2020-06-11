import java.net.*; 
import java.io.*; 
import java.util.*;
import java.util.Random;
import java.lang.*;
public class DHClient { 

private static int generate_primes()
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
}

private static Integer getClientKey(DataInputStream in,DataOutputStream out)throws IOException//main(String[] args) 
    { 
        try { 
            String pstr, gstr, Astr; 
            //String serverName = "localhost"; 
           // int port = 5056; 
  
            // Declare p, g, and Key of client 
            int p = generate_primes(); 
		//System.out.println(p);
	    
	    Random rand = new Random();
            int g = rand.nextInt(p);
		//System.out.println(g); 
            int a = rand.nextInt(p);
		//System.out.println(a);  
            Integer Adash, serverB; 
  
            
            pstr = Integer.toString(p); 
            out.writeUTF(pstr); // Sending p 
  
            gstr = Integer.toString(g); 
            out.writeUTF(gstr); // Sending g 
  
            Integer A = binary_exponentiation(g,a,p); // calculation of A 
            Astr = Integer.toString(A); 
            out.writeUTF(Astr); // Sending A 
  
            // Client's Private Key 
            //System.out.println("From Client : Private Key = " + a); 
  
            // Accepts the data 
           // DataInputStream in = new DataInputStream(client.getInputStream()); 
  
            serverB = Integer.parseInt(in.readUTF()); 
            //System.out.println("From Server : Public Key = " + serverB); 
  
            Adash = (binary_exponentiation(serverB,a,p)) % p ; // calculation of Adash 
  
            //System.out.println("Secret Key to perform Symmetric Encryption = "
                              // + Adash); 
	    return Adash;
            //client.close(); 
        } 
        catch (Exception e) { 
            e.printStackTrace(); 
        } 
return -1;
    }

public static Integer fetchClientKey(DataInputStream in,DataOutputStream out)
{
	try{
	Integer r  = getClientKey(in,out);
	return r;}
	 catch (Exception e) { 
            e.printStackTrace(); 
        } 
 	return -1;
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
 
} 

