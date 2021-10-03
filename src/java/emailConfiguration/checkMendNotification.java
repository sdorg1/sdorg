/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailConfiguration;

/**
 *
 * @author Joshua-TB
 */
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class checkMendNotification {

    private String toMail;
    private String toFName;
    private String toUserName;
    private String toPassword;
    private String subject;
    private String msg;

    public checkMendNotification(String toMail, String toFName, String toUserName, String toPassword, String subject, String msg) {
        this.toMail = toMail;
        this.toFName = toFName;
        this.toUserName = toUserName;
        this.toPassword = toPassword;
        this.subject = subject;
        this.msg = msg;
    }

    public String getToMail() {
        return toMail;
    }

    public void setToMail(String toMail) {
        this.toMail = toMail;
    }

    public String getToFName() {
        return toFName;
    }

    public void setToFName(String toFName) {
        this.toFName = toFName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToPassword() {
        return toPassword;
    }

    public void setToPassword(String toPassword) {
        this.toPassword = toPassword;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void sender() {
        //String to = "kndjoshua@gmail.com"; // to address. It can be any like gmail, hotmail etc.
        final String from = "sdorgsys@gmail.com"; // from address. As this is using Gmail SMTP.
        final String password = "F71CE35DAA9507B1B9EED0CD1418307E1908"; // password for from mail address. 
        System.out.println("setting email properties");
        Properties prop = new Properties();
        System.out.println("Intialize property");
        prop.put("mail.smtp.starttls.enable", "true");
        System.out.println(prop.get("mail.smtp.starttls.enable"));
        prop.put("mail.smtp.host", "smtp.elasticemail.com");
        System.out.println(prop.get("mail.smtp.host"));
        prop.put("mail.smtp.port", "2525");
        System.out.println(prop.get("mail.smtp.port"));
        prop.put("mail.smtp.auth", "true");
//  System.out.println(prop.get("mail.smtp.auth"));
//  prop.put("mail.smtp.socketFactory.port", "465");
//  System.out.println(prop.get("mail.smtp.socketFactory.port"));
//  prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//  System.out.println(prop.get("mail.smtp.socketFactory.class"));

        System.out.println("Initializing mail session");
        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("Password Auth intialization");
                return new PasswordAuthentication(from, password);
            }
        });

        System.out.println("Finish mail session intialization");

        try {
            System.out.println("Intialize mime object");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
            message.setSubject(subject);
            System.out.println("Finish mime message init");
            //String msg = "This email sent using JavaMailer API from Java Code!!!";
            System.out.println("Multipart init");
            MimeMultipart multipart = new MimeMultipart("related");

// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<img src=\"cid:image\"><br/>" + "<H2><b>Dear, " + toFName + "</b><br/><br/>" + msg + "</H2>"
                    + "<H1 style=\"width: 60%; border-color: #13294f; border-width: .25em; border-style: double;\">USERNAME: " + toUserName + "</H1> "
                    + "<H1 style=\"width: 60%; border-color: #13294f; border-width: .25em; border-style: double;\">PASSWORD: " + toPassword + "</H1><br/>"
                    + "<h2>Thank you for working with us. Please let us know if we can do anything else to help!</h2><hr>";
            messageBodyPart.setContent(htmlText, "text/html");

// Add it
            multipart.addBodyPart(messageBodyPart);

// Second part (EMBEDDED image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource("D:\\Auca JKI\\8th Semester\\Final Project\\Project\\EmailSender\\SDORGEmailSmallBanner.png");

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

// add the image to the multipart
            multipart.addBodyPart(messageBodyPart);

// put everything together
            message.setContent(multipart);

            System.out.println("Multipart init end");

            System.out.println("Calling email sender");
// Send message
            Transport.send(message);

            System.out.println("Mail successfully sent..");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
