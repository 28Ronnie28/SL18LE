package net.ddns.pcuniverse.studentlivelecturer.main;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {

    public static Boolean emailMessage(String studentName, String studentEmail, String recipientEmail, String subject, String body) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.user", "studentliverecovery@gmail.com");
            props.put("mail.smtp.password", "recoverystudentlive");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.auth", true);
            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "studentliverecovery", "recoverystudentlive");
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Message from lecturer");
            message.setText("Message sent from: " + studentName + " (" + studentEmail + ")\n\nMessage subject: \"" + subject + "\"\n\nMessage: \"" + body + "\"");
            transport.sendMessage(message, message.getAllRecipients());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
