// Java implementation of Server 2
// Saved file as Server2.class

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import javax.crypto.SecretKey;
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

// S2 class
public class S2
{
    public static HashMap <String,Long> voteCounter = new HashMap<String,Long>();
    public static List <String> secretList = new ArrayList <>();
    public static EncryptionDecryptionAES aes = new EncryptionDecryptionAES();
    private static String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm";

    static int k = 0;

    public static void dumpTable(){
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter("Results.txt", false));
            
            for (Map.Entry m : S2.voteCounter.entrySet()){
                out.write("\n"+ m.getKey()+" gets " + m.getValue()+ " votes.\n");
            }
            out.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createCandidateTable(){
        voteCounter.put("ahufhuwjekhkjfahsdhaufhiu24hjk",new Long(0));
        voteCounter.put("ent245hjdn7kj2h348jshfkakn5n54",new Long(0));
        voteCounter.put("oi98jhghjg6uaghevhj87435jhk8or",new Long(0));
        voteCounter.put("32j4hhuisucjkhjhds874753lhuh82",new Long(0));
    }

    //aes secret key
    public static SecretKey getSecretKey(){
        String keyStr = "012345678901234567890123456789XY";
        byte[] decodedKey = Base64.getMimeDecoder().decode(keyStr);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

    //publish to a file
    public static void publish(String fileName, String str){
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write(str);
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void voteCount(String input){
        //integer of 512 bits from string
        String message = new String();
        try{

            message = rsa.decryptstr(input,privateKey);
            //cid is Candidate id in the vote
            //secret is the secret of the voter
            String cid = message.split("\\s+")[0];
            String secret = message.split("\\s+")[1];
            //check if this vote already counted

            if(!secretList.contains(secret)){
                voteCounter.replace(cid,voteCounter.get(cid)+Long.valueOf(1));
                secretList.add(secret);
                publish("secrets.txt",secret+"\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {
        //getting Secret Key
        SecretKey SharedKey = getSecretKey();
        createCandidateTable();
    	// S2 is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);
        Socket s = null;

        while (true){
            try{
                // Accept the incoming request
                s = ss.accept();
                // obtain input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                // Create a new Thread with this object.
                Thread t = new S2Thread(s,dis,dos);
                // start the thread.
                t.start();
                k++;
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}

//S2 thread for each vote
class S2Thread extends Thread{

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor
    public S2Thread(Socket s, DataInputStream dis, DataOutputStream dos){
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        String toreturn;
        String list[];
        String retList[];
        String message  = null;
        try {
            // obtaining input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            //receive packet 4
            received = dis.readUTF();
            System.out.println("\n------------------------Received packet4-----------------------------");
            //if packet4 was a list of cid and secret combinations
            //counting
            //decrytpion using aes
            message = EncryptionDecryptionAES.decrypt(received, S2.getSecretKey());

            S2.voteCount(message.split("\\s+")[0]);

            //Communication packet 5
            if(message!=null){
                toreturn = Long.toString(Long.parseLong(message.split("\\s+")[1]) - 1);
                dos.writeUTF(toreturn);
                System.out.println("\n----------------------------Sent Packet5--------------------------------");
            }
        }
        catch (Exception e){
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
        S2.dumpTable();
    }
}