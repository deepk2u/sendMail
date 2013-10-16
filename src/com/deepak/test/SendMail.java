package com.deepak.test;

import java.security.Security;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPSSLTransport;

public class SendMail {

    private static final String emailMsgTxt = "<h3>PFA</h3>";
    private static final String emailSubjectTxt = "Report at " + new Date().toString();
    private static final String SSL_FACTORY = "com.deepak.test.SendMailSSLSocketFactory";
    private static final String sendTo = "<email>";

    public static void sendMail(String file) throws Exception {
        FileDataSource fileDataSource = new FileDataSource(file) {
            @Override
            public String getContentType() {
                return "application/octet-stream";
            }
        };
        new SendMail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, new DataHandler(fileDataSource));
        System.out.println("Sucessfully Sent mail to All Users");
    }

    public void sendSSLMessage(String recipients, String subject, String message, DataHandler dataHandler) throws MessagingException {

        Security.setProperty("ssl.SocketFactory.provider", SSL_FACTORY);

        final String smtpHostName = "<smtp>";
        final String smtpPort = "465";
        final String emailUsername = "<user>";
        final String emailPassword = "<pass>";
        final String emailFromAddress = "<from>";
        final String debug = "true";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.submitter", emailUsername);
        props.put("mail.smtp.user", emailUsername);
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.debug", debug);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.socketFactory.port", smtpPort);
        props.put("mail.smtp.ssl.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        session.setDebug(Boolean.parseBoolean(debug));

        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(emailFromAddress);
        msg.setFrom(addressFrom);

        StringTokenizer st = new StringTokenizer(recipients, ",");
        String[] recipientsArray = new String[st.countTokens()];
        int tokens = 0;
        while (st.hasMoreTokens()) {
            recipientsArray[tokens] = st.nextToken().trim();
            tokens++;
        }

        InternetAddress[] addressTo = new InternetAddress[recipientsArray.length];
        for (int i = 0; i < recipientsArray.length; i++) {
            addressTo[i] = new InternetAddress(recipientsArray[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        if (dataHandler != null) {
            Multipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(message, "text/html");
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(dataHandler);
            messageBodyPart.setFileName(dataHandler.getDataSource().getName());
            multipart.addBodyPart(messageBodyPart);
            msg.setContent(multipart, "text/html");
        } else {
            msg.setContent(message, "text/html");
        }

        SMTPSSLTransport transport = (SMTPSSLTransport) session.getTransport("smtps");
        transport.connect(smtpHostName, new Integer(smtpPort).intValue(), emailUsername, emailPassword);
        msg.saveChanges();
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();

    }
}
