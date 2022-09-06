import org.apache.commons.io.FileUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.SecretKey;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;

public class EmailSender {
    static Properties prop = new Properties();
    static Session session;

    EmailSender() throws Exception {
    }

    void sendEmail() throws Exception {
        setProperties();
        createSession(FrmLogin.username, FrmLogin.password);
        sendMessage(FrmLogin.username, FrmComposeMail.to, FrmComposeMail.message, FrmComposeMail.subject);
    }

//    public static void main(String[] args) throws MessagingException {
//        createSession("account1@bilal.com", "123");
//        sendMessage("account1@bilal.com", "account2@bilal.com");
//    }

    public static void setProperties() {
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "localhost");
    }

    public static void createSession(String username, String password) {
        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static void sendMessage(String sender, String receiver, String msg, String subject) throws Exception {




        String receiveOnBehalf = receiver;

        receiver = GlobalClass.addressBook.getUserFromBehalf(receiver);

        if (receiver == null) {
            JOptionPane.showMessageDialog(null, "Receiver not found");
            return;
        }


        Message message = new MimeMessage(session);
        GlobalClass.receiver = receiver;
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(receiver));
        message.setSubject(subject);
        message.setSentDate(new java.util.Date());

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        // Now set the actual message
        messageBodyPart.setText("");
        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(messageBodyPart);
        // Part two is attachment
        messageBodyPart = new MimeBodyPart();

        EmailContent emailContent = new EmailContent(subject, msg, receiver, receiveOnBehalf, new java.util.Date());
        byte[] emailBytes = EmailContent.serialize(emailContent);

        String AESEncryptedString = AESGCMEncryption.encrypt(emailBytes);

        PublicKey publicKey = null;

        Statement statement = FrmDashboard.connection.createStatement();
        statement.execute("SELECT certificate FROM ClientCertificates WHERE clientName = '" + receiver + "'");
        ResultSet resultSet = statement.getResultSet();

        if (resultSet.next()) {
            X509Certificate certificate = MyCertificateGenerator.getCertificateFromString(resultSet.getString("certificate"));
            if (certificate != null)
                publicKey = certificate.getPublicKey();
        }


        PrivateKey privateKey = null;
        Statement statement2 = FrmDashboard.connection.createStatement();
        statement2.execute("SELECT privateKey FROM SelfCertificates WHERE clientName = '" + FrmLogin.username + "'");
        ResultSet resultSet2 = statement2.getResultSet();
        if (resultSet2.next()) {
            if (!Objects.equals(resultSet2.getString("privateKey"), "")) {
                privateKey = MyCertificateGenerator.getPrivateKeyFromString(AESWithHash.decrypt(resultSet2.getString("privateKey"), FrmLogin.password));
            }
        }




        SecretKey AESEncryptionKey = AESGCMEncryption.key;



        String RSAEncryptedString = RSAEncryption.encrypt(Base64.getEncoder().encodeToString(AESEncryptionKey.getEncoded()), publicKey);

//        System.out.println("AES Encrypted String: " + AESEncryptedString);
//        System.out.println("RSA Encrypted String: " + RSAEncryptedString);

        String EncryptedMessageString = RSAEncryptedString + "|" + AESEncryptedString;

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(EncryptedMessageString.getBytes(StandardCharsets.UTF_8));
        byte[] signature = sig.sign();
        String signatureString = Base64.getEncoder().encodeToString(signature);

        String EncryptedMessageStringWithSignature = EncryptedMessageString + "|" + signatureString;



        byte[] bytes = EncryptedMessageStringWithSignature.getBytes(StandardCharsets.UTF_8);
        messageBodyPart.setDataHandler(new DataHandler(bytes, "application/octet-stream"));
        messageBodyPart.setFileName("message");
        multipart.addBodyPart(messageBodyPart);

//        addAttachment(multipart, publicKey, "C:\\Users\\Syed Bilal Abbas\\Desktop\\Vivaldi.lnk", "Vivaldi.lnk");

        for (Attachment attachment: FrmComposeMail.attachments) {
            addAttachment(multipart, publicKey, attachment.filePath, attachment.fileName);
        }

        // Send the complete message parts
        message.setContent(multipart);
        Transport.send(message);
        FrmComposeMail.attachments.clear();
        JOptionPane.showMessageDialog(null, "Message sent successfully");
    }

    private static void addAttachment(Multipart multipart, PublicKey publicKey, String filepath, String filename) throws Exception {
//        DataSource source = new FileDataSource(filepath);
        File file = new File(filepath);
        String AESEncryptedString = AESGCMEncryption.encrypt(FileUtils.readFileToByteArray(file));
        SecretKey AESEncryptionKey = AESGCMEncryption.key;
        String RSAEncryptedString = RSAEncryption.encrypt(Base64.getEncoder().encodeToString(AESEncryptionKey.getEncoded()), publicKey);
        String EncryptedMessageString = RSAEncryptedString + "|" + AESEncryptedString;
        byte[] bytes = EncryptedMessageString.getBytes(StandardCharsets.UTF_8);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(bytes, "application/octet-stream"));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }





//public static void sendMessage(String sender, String receiver, String msg, String subject) throws MessagingException {
////        System.out.println(sender);
//        Message message = new MimeMessage(session);
//        GlobalClass.receiver = receiver;
////        System.out.println(session.getProperty("mail.from"));
//        message.setFrom(new InternetAddress(sender));
//        message.setRecipients(
//                Message.RecipientType.TO, InternetAddress.parse(receiver));
//        message.setSubject(subject);
//        message.setSentDate(new java.util.Date());
//
//
//
//        if (!FrmComposeMail.filePath.equals("")){
//
//            // Create the message part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            // Now set the actual message
//            messageBodyPart.setText(msg);
//
//            // Create a multipart message
//            Multipart multipart = new MimeMultipart();
//
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Part two is attachment
//            messageBodyPart = new MimeBodyPart();
//            String filePath = FrmComposeMail.filePath;
//            DataSource source = new FileDataSource(filePath);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(FrmComposeMail.fileName);
//            multipart.addBodyPart(messageBodyPart);
//
//            // Send the complete message parts
//            message.setContent(multipart);
//            FrmComposeMail.filePath = "";
//            FrmComposeMail.fileName = "";
//        }
//        else {
//            message.setText(msg);
//        }
//
//
//
//
////        String msg = "This is my fourth email using JavaMailer";
//
////        message.setText(msg);
//
////        MimeBodyPart mimeBodyPart = new MimeBodyPart();
////        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
////
////        Multipart multipart = new MimeMultipart();
////        multipart.addBodyPart(mimeBodyPart);
////
////        message.setContent(multipart);
////
//        Transport.send(message);
//        JOptionPane.showMessageDialog(null, "Message sent successfully");
//
//    }
}
