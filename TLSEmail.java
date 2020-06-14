
import java.io.*;
import java.lang.*;
import java.util.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.Properties;

import javax.mail.*;

public class TLSEmail {

	/**
	   Outgoing Mail (SMTP) Server
	   requires TLS or SSL: smtp.gmail.com (use authentication)
	   Use Authentication: Yes
	   Port for TLS/STARTTLS: 587

	 */
	private static void sendEmail(Session session, String toEmail, String subject, String body)
	{
	    try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress("server1csec@gmail.com", "Please Don't Reply"));

	      msg.setReplyTo(InternetAddress.parse("server1csec@gmail.com", false));

	      msg.setSubject(subject, "UTF-8");

	      msg.setText(body, "UTF-8");

	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
	      System.out.println("Message is ready");
    	      Transport.send(msg);  

	      System.out.println("Email Sent Successfully!!");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }

	}

	private static String sendMail(String toEmail)
	{
		try{
		final String fromEmail = "server1csec@gmail.com"; //requires valid gmail id
		final String password = "NITCserver1IV"; // correct password for gmail id
		final String to = toEmail; // can be any email id 
		
		//System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
		props.put("mail.smtp.port", "587"); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
		
                //create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Random rand = new Random(System.currentTimeMillis());
		double times = System.currentTimeMillis();
		int g = (int)times;
		int rand1 = rand.nextInt(g);
		final String r = ""+ rand1;
		Session session = Session.getInstance(props, auth);
	
                sendEmail(session, toEmail,"Your OTP for verification", r);
               
                return r;
	} catch (Exception e) {
	      e.printStackTrace();
	    }
             return "None";    
		
	}
	public static String otp(String toEmail)
{
	String r = sendMail(toEmail);
	return r;
}
	 /*public static void main(String[] args) {
		
		System.out.println("Enter the person to send the message to");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try
		{
		String toEmail = br.readLine();
		sendMail(toEmail); } 
		 
	}//*/

	
}

