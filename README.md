## Internet_Voting

#  Code Base Explanation: 
  
  Main files are <br />
    Webserver.java : The code at voter end <br />
    S1.java  <br />
    S2.java : Counting Server <br />
    
  Database File are <br />
    Our database is in the form of a Hashmap. <br />
    VoterData.java : Creates a Hashmap between Strings and Class objects. The class contains all information of the voter such as VoterId, password, salt, email address etc. <br />
    CandidateData.java : Creates a Hashmap between Strings and Strings. The class contains all information of the Candidate. Currently they are just Candidate name and their corresponding unique id <br />
    VoterCheck.java : Creates a Hashmap between Strings and Class objects. This class contains a boolean checklist for voter. This is used by S1 to avoid duplicate votes or multiple votes by same voter. <br /> 
    
  Other Files <br />
    Login.java : Contains the code base for 2-factor authentication. <br />
    DHClient.java and DHServer.java : Diffie Hellman Key Exchange. <br />
    EmailUtil.java and TLSEmail.java : To send emails for OTP. <br />
    EncryptionDecryptionAES.java : AES Encryption and Decryption <br />
    MyFrameX.java : For GUI pop ups using java swing on voter's end. <br />
    Results.txt : Keeps the count of the votes against each candidate <br />
    secrets.txt : List of the secret of voters who have voted. Published in web.
    
 
  # How to compile and Run
    
  Please remember to change the IP Addresses according to your conveniences, if implemented in different machines. Else run the following 3 files in 3 different terminal tabs. <br />
  
  Please compile using the following <br />
    javac -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" Webserver.java <br />
    javac -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" S1.java <br />
    javac S2.java <br />
    
  Run the program using <br />
    java -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" Webserver <br />
    java -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" S1 <br />
    java S2


S2 -> S1 -> Webserver //order to run the files


  //Installing Apache Server and hosting the secrets to web
  1. sudo apt-get update
  2. sudo apt upgrade
  3. sudo apt-get install apache2
  4. sudo service apache2 restart
  5. sudo gpasswd -a "$USER" www-data
  6. sudo chown -R "$USER":www-data /var/www
  7. Repeat Step 4
  8. sudo ufw app list
  9. sudo ufw allow in "Apache Full" (or)   sudo ufw allow in "Apache"
  10. Place the index.html and secrets.txt within /var/www/html/
  11. Change the link in "publish" of S2
  \\\More to be seen on how to make the website accessible to other clients


