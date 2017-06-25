package com.dq.arq.sme.services;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dq.arq.sme.util.UtilConstants;

@Service
public class MailService {

	final static Logger logger = LoggerFactory.getLogger(MailService.class);


	public void sendHtmlEmail(String subject, String message) throws AddressException,
	MessagingException {

		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", UtilConstants.hostname);
		properties.put("mail.smtp.port", UtilConstants.port);
		properties.put("mail.smtp.auth", UtilConstants.auth);
		//  properties.put("mail.smtp.starttls.enable", "true");

		// creates a new session with an authenticator

		Session session = Session.getInstance(properties);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(UtilConstants.fromAddress));


		if(!UtilConstants.toAddresses.equals(""))
		{
			String[] toAddressesSize =  UtilConstants.toAddresses.split(",");
			InternetAddress[] toAddresses=new InternetAddress[toAddressesSize.length];
			int i=0;
			for(String toAddress:UtilConstants.toAddresses.split(","))
			{
				toAddresses[i++]=new InternetAddress(toAddress);
			}
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
		}


		if(!UtilConstants.ccAddresses.equals(""))
		{
			String[] ccAddressesSize =  UtilConstants.ccAddresses.split(",");
			InternetAddress[] ccAddresses=new InternetAddress[ccAddressesSize.length];
			int i=0;
			for(String ccAddress:UtilConstants.ccAddresses.split(","))
			{
				ccAddresses[i++]=new InternetAddress(ccAddress);
			}
			msg.setRecipients(Message.RecipientType.CC, ccAddresses);
		}


		if(!UtilConstants.bccAddresses.equals(""))
		{
			String[] bccAddressesSize =  UtilConstants.bccAddresses.split(",");
			InternetAddress[] bccAddresses=new InternetAddress[bccAddressesSize.length];
			int i=0;
			for(String bccAddress:UtilConstants.bccAddresses.split(","))
			{
				bccAddresses[i++]=new InternetAddress(bccAddress);
			}
			msg.setRecipients(Message.RecipientType.BCC, bccAddresses);
		}

		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(message, "text/html");
		Transport.send(msg);
		
		logger.info("Email sent from: "+UtilConstants.fromAddress+" to: "+UtilConstants.toAddresses + " cc: "+UtilConstants.ccAddresses+" bcc: "+UtilConstants.bccAddresses+" successfully");

	}
	
	public void sendHtmlEmail(String subject, String message,String recipient) throws AddressException,
	MessagingException {
		
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", UtilConstants.hostname);
		properties.put("mail.smtp.port", UtilConstants.port);
		properties.put("mail.smtp.auth", UtilConstants.auth);
		//  properties.put("mail.smtp.starttls.enable", "true");
		
		// creates a new session with an authenticator
		
		Session session = Session.getInstance(properties);
		
		// creates a new e-mail message
		Message msg = new MimeMessage(session);
		
		msg.setFrom(new InternetAddress(UtilConstants.fromAddress));
		
		
		if(!recipient.equals(""))
		{
			String[] toAddressesSize =  recipient.split(",");
			InternetAddress[] toAddresses=new InternetAddress[toAddressesSize.length];
			int i=0;
			for(String toAddress:recipient.split(","))
			{
				toAddresses[i++]=new InternetAddress(toAddress);
			}
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
		}
		
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(message, "text/html");
		
		Transport.send(msg);
		
		logger.info("Email sent from: "+UtilConstants.fromAddress+" to: "+recipient);
		
	}
	
	public static void sendAttachmentEmail(String recipient,String subject, String body,String attachmentName) throws AddressException, MessagingException{
		
			// sets SMTP server properties
			Properties properties = new Properties();
			properties.put("mail.smtp.host", UtilConstants.hostname);
			properties.put("mail.smtp.port", UtilConstants.port);
			properties.put("mail.smtp.auth", UtilConstants.auth);
			//  properties.put("mail.smtp.starttls.enable", "true");

			// creates a new session with an authenticator

			Session session = Session.getInstance(properties);
			
			
			

			// creates a new e-mail message
			Message msg = new MimeMessage(session);
			
			msg.setFrom(new InternetAddress(UtilConstants.fromAddress));
			
			


			if(!recipient.equals(""))
			{
				String[] toAddressesSize =  recipient.split(",");
				InternetAddress[] toAddresses=new InternetAddress[toAddressesSize.length];
				int i=0;
				for(String toAddress:recipient.split(","))
				{
					toAddresses[i++]=new InternetAddress(toAddress);
				}
				msg.setRecipients(Message.RecipientType.TO, toAddresses);
			}


			
			
			
	         msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		     msg.addHeader("format", "flowed");
		     msg.addHeader("Content-Transfer-Encoding", "8bit");
		      


		     //msg.setSubject(subject, "UTF-8");
		     msg.setSubject(subject);

		     msg.setSentDate(new Date());
		      
	         // Create the message body part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setContent(body, "text/html");
	         
	         // Create a multipart message for attachment
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Second part is attachment
	         messageBodyPart = new MimeBodyPart();
	         
	         DataSource source = new FileDataSource(attachmentName);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	        
	         if(attachmentName.contains("/"))
				{
					String path[] = attachmentName.split("\\/");
					attachmentName = path[path.length-1];
				}
	         
	         messageBodyPart.setFileName(attachmentName);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         msg.setContent(multipart);

	         // Send message
	         Transport.send(msg);
	         logger.info("Email sent from: "+UtilConstants.fromAddress+" to: "+recipient);
	      
	}
}
