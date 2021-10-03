/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import domain.User;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
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

/**
 *
 * @author elvis
 */
public class EmailSender {
    private static final String MAIL_USERNAME = "kwizeraelvis6@gmail.com";
    private static final String MAIL_PASSWORD = "735A26DDBBFA6611B057A76A5C3D3960A19E";
    
    private static final Properties mailerProperties = new Properties();
    private static Session mailSession;
    
//    static {
//        mailerProperties.put("mail.smtp.auth", true);
//        mailerProperties.put("mail.smtp.starttls.enable", "true");
//        mailerProperties.put("mail.smtp.host", "smtp.elasticemail.com");
//        mailerProperties.put("mail.smtp.port", "2525");
//        mailerProperties.put("mail.smtp.ssl.trust", "smtp.elasticemail.com");
////        mailerProperties.put("mail.smtp.username", "kwizeraelvis6@gmail.com");
////        mailerProperties.put("mail.smtp.password", "735A26DDBBFA6611B057A76A5C3D3960A19E");
//        
//        mailSession = Session.getInstance(mailerProperties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
//            }
//        
//        });
//    }
    
    public static void sendMail(String receiverEmail, String msg, String subject){
        mailerProperties.put("mail.smtp.starttls.enable", "true");
        mailerProperties.put("mail.smtp.host", "smtp.elasticemail.com");
        mailerProperties.put("mail.smtp.port", "2525");
        mailSession = Session.getInstance(mailerProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
            }
        
        });
        Message message = new MimeMessage(mailSession);
        try {
            message.setFrom(new InternetAddress(MAIL_USERNAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            message.setSubject(subject);
            
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            
            message.setContent(multipart);
            
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public static String generateVerificationMessage(User user){
        String message = String.format("Thank you for requesting membership with SDORG, To access your account please use the following credentials: \n Password ${password} \n Username: {username}", user.getPassword(), user.getStudent().getUser().getUsername());
        return message;
    }
}