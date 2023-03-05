package com.example.stock.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.stock.model.StockModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	public static void sendEmailStock(String bodyHtml) throws Exception {

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        //session.setDebug(true);
        try {
        	
        	//get obj for send mail from config
        	ObjectMapper mapper = new ObjectMapper();
        	StockModel obj = mapper.readValue(new File("C:/check_stock/stock.json"), StockModel.class);
			
			if(obj==null) {
				throw new Exception("Obj config is null");
			}
		
        	
        	Properties properties = System.getProperties();

            // Setup mail server
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass 
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(obj.getEmailSender(), obj.getEmailPassword());
                }

            });
        	
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(obj.getEmailSender()));

            // Set To: header field of the header.
            if(obj.getEmailReceiver()!=null) {
            	for(String rec : obj.getEmailReceiver()) {
            		message.addRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            	}
            }
            
            SimpleDateFormat sdf1 = new SimpleDateFormat();
            sdf1.applyPattern("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dateTimeStr = sdf1.format(date);

            // Set Subject: header field
            message.setSubject("Check Stock Report : "+dateTimeStr);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart attachmentPart = new MimeBodyPart();

            MimeBodyPart textPart = new MimeBodyPart();
            
            String body = 
            		"<p><b>To Whom It May Concern</b><br>" + 
            		"<b>Report check stock at <font style=\"color:green\">"+dateTimeStr+"</font></b><br>" + 
            		" </p>" + 
            		(bodyHtml==null?"":bodyHtml)+
            		"<br>" + 
            		"<br>" + 
            		"<b>Best regards,</b><br>" + 
            		"<b>Automatic system</b><br>" + 
            		"<b>Email: stocks.sps@gmail.com</b>";

            try {

                File f =new File(obj.getExcelPath()+"result.xlsx");

                attachmentPart.attachFile(f);
                textPart.setContent(body,"text/html; charset=utf-8" );
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
                
               // Send the actual HTML message, as big as you like

            } catch (IOException e) {

                e.printStackTrace();

            }
            
            message.setContent(body ,"text/html; charset=utf-8");

            message.setContent(multipart);

            logger.info("sending Email Stock ...");
            // Send message
            Transport.send(message);
            logger.info("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            logger.error(mex.getMessage());
        }
    }
	
	public static void sendEmailStockMulti(String bodyHtml) throws Exception {

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        //session.setDebug(true);
        try {
        	
        	//get obj for send mail from config
        	ObjectMapper mapper = new ObjectMapper();
        	StockModel obj = mapper.readValue(new File("C:/check_stock/stock.json"), StockModel.class);
			
			if(obj==null) {
				throw new Exception("Obj config is null");
			}
		
        	
        	Properties properties = System.getProperties();

            // Setup mail server
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass 
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(obj.getEmailSender(), obj.getEmailPassword());
                }

            });
        	
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(obj.getEmailSender()));

            // Set To: header field of the header.
            if(obj.getEmailReceiver()!=null) {
            	for(String rec : obj.getEmailReceiver2()) {
            		message.addRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            	}
            }
            
            SimpleDateFormat sdf1 = new SimpleDateFormat();
            sdf1.applyPattern("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dateTimeStr = sdf1.format(date);

            // Set Subject: header field
            message.setSubject("Check Stock Multiple Report : "+dateTimeStr);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart attachmentPart = new MimeBodyPart();

            MimeBodyPart textPart = new MimeBodyPart();
            
            String body = 
            		"<p><b>To Whom It May Concern</b><br>" + 
            		"<b>Report check stock multiple at <font style=\"color:green\">"+dateTimeStr+"</font></b><br>" + 
            		" </p>" + 
            		(bodyHtml==null?"":bodyHtml)+
            		"<br>" + 
            		"<br>" + 
            		"<b>Best regards,</b><br>" + 
            		"<b>Automatic system</b><br>" + 
            		"<b>Email: stocks.sps@gmail.com</b>";

            try {
            	
            	//remove old file
            	Path path = Paths.get(obj.getPathMultiResult()+"result.zip");
            	Files.deleteIfExists(path);
            	
            	FilesUtils.zip(obj.getExcelMultiPath(), obj.getPathMultiResult()+"result.zip");

                File f =new File(obj.getPathMultiResult()+"result.zip");

                attachmentPart.attachFile(f);
                textPart.setContent(body,"text/html; charset=utf-8" );
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
                
               // Send the actual HTML message, as big as you like

            } catch (IOException e) {

                e.printStackTrace();

            }
            
            message.setContent(body ,"text/html; charset=utf-8");

            message.setContent(multipart);

            logger.info("sending Email Stock Multi ...");
            // Send message
            Transport.send(message);
            logger.info("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            logger.error(mex.getMessage());
        }
    }
	
}
