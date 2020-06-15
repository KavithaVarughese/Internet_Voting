## Internet_Voting

# UPDATES : 
  Basic Skeletal code 
  
  Main files are <br />
    Webserver.java <br />
    S1.java <br />
    S2.java
    
  Please compile using the following <br />
    javac -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" Webserver.java <br />
    javac -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" S1.java <br />
    javac S2.java <br />
    
  Run the program using <br />
    java -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" Webserver <br />
    java -cp ".:/path/mail.jar:/path/activation.jar:/path/javax.mail.jar;" S1 <br />
    java S2


S2 -> S1 -> Webserver //order to run the files


//Installing Apache Server and hosting
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


