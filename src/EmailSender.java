import org.apache.commons.io.FileUtils;

import javax.activation.DataHandler;
import javax.crypto.SecretKey;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;

public class EmailSender {
    static Properties prop = new Properties();
    static Session session;

    void sendEmail() throws Exception {
        setProperties();
        createSession(FrmLogin.username, FrmLogin.password);
        sendMessage(FrmComposeMail.from, FrmComposeMail.to, FrmComposeMail.message, FrmComposeMail.subject);
    }

    public static void setProperties() {
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", FrmSettings.ipAddress);
        prop.put("mail.smtp.port", FrmSettings.smtpPort);
        prop.put("mail.smtp.ssl.trust", FrmSettings.ipAddress);
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
        ArrayList<String> receivers = new ArrayList<>();
        receivers = GlobalClass.addressBook.getOthersUser(receiver);

        String sendOnBehalf = sender;
        sender = GlobalClass.addressBook.getSelfUser(sender);

        if (receivers.size() == 0) {
            JOptionPane.showMessageDialog(null, "Receiver not found");
            return;
        } else if (sender == null) {
            JOptionPane.showMessageDialog(null, "Sender not found");
            return;
        }


        for (String myReceiver: receivers) {
            receiver = myReceiver;
            Message message = new MimeMessage(session);
//        GlobalClass.receiver = receiver;
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

            EmailContent emailContent = new EmailContent(subject, msg, sendOnBehalf, receiveOnBehalf, new java.util.Date());
            byte[] emailBytes = EmailContent.serialize(emailContent);

            String AESEncryptedString = AESGCMEncryption.encrypt(emailBytes);
            SecretKey AESEncryptionKey = AESGCMEncryption.key;
            String iv = Base64.getEncoder().encodeToString(AESGCMEncryption.encryptionCipher.getIV());

            System.out.println(Base64.getEncoder().encodeToString(AESEncryptionKey.getEncoded()));
            System.out.println("iv: " + iv);


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







            String RSAEncryptedString = RSAEncryption.encrypt(Base64.getEncoder().encodeToString(AESEncryptionKey.getEncoded()) + "|" + iv, publicKey);

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
        }


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
}
