import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

class MyInbox {
    String from, date, subject, part;
    MyInbox(String from, String date, String subject, String part) {
        this.from = from;
        this.date = date;
        this.subject = subject;
        this.part = part;
    }
}


public class EmailReceiver {

    public static ArrayList<MyInbox> inbox = new ArrayList<>();
    public static Folder folderInbox;
    public static Store store;

    private static Properties getServerProperties(String protocol, String host,
                                                  String port) {
        Properties properties = new Properties();

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        /*
 SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
*/
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));

        return properties;
    }

    public static void downloadEmails(String protocol, String host, String port,
                                      String userName, String password) throws SQLException {
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);

        try {
            // connects to the message store
            store = session.getStore(protocol);
            store.connect(userName, password);

            FrmLogin.isValid = true;

            // opens the inbox folder
            folderInbox = store.getFolder("INBOX");
//            folderInbox.open(Folder.READ_ONLY);
            folderInbox.open(Folder.READ_WRITE);
            folderInbox.expunge();

            // search for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

            // fetches new messages from server
            Message[] messages = folderInbox.search(unseenFlagTerm);
//            Message[] messages = folderInbox.getMessages();
            inbox.clear();

            for (Message message : messages) {
//                String attachment = "No";
                Address[] fromAddress = message.getFrom();
                String sender = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = String.valueOf(message.getSentDate());

                String contentType = message.getContentType();
                String messageContent = "";

                // store attachment file name, separated by comma
                String attachFiles = "";

                EmailContent emailContent = null;

                MimeBodyPart part = null;
                if (contentType.contains("multipart")) {
//                    attachment = "Yes";
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment


//                            System.out.println(fileName);
//                            System.out.println(part.getInputStream());



                            messageContent = "";





                            attachFiles += part.getFileName() + ", ";

                        } else {
                            // this part may be the message content
//                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

//                if (emailContent != null) {

                message.setFlag(Flags.Flag.SEEN, true);

                InputStream fileNme = part.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(fileNme, writer, "UTF-8");
                String theString = writer.toString();
                System.out.println(theString);

                Statement statement1 = FrmDashboard.connection.createStatement();
                statement1.executeUpdate("INSERT INTO Emails VALUES ('" + FrmLogin.username + "', '" + sender + "', '" + sentDate + "', '" + subject + "', '" + theString + "')");
                statement1.close();

//                inbox.add(new MyInbox(sender, sentDate, subject, theString));

                System.out.println(sender + " " + sentDate + " " + subject);
            }
//            folderInbox.close(false);
//            store.close();
        } catch (NoSuchProviderException ex) {
            ex.printStackTrace();
        } catch (MessagingException ex) {
            FrmLogin.isValid = false;
            JOptionPane.showMessageDialog(null, "Enter valid username and password", "Invalid Credentials", JOptionPane.ERROR_MESSAGE);
//            System.out.println("Could not connect to the message store");
//            ex.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Statement statement1 = FrmDashboard.connection.createStatement();
            ResultSet resultSet = statement1.executeQuery("SELECT * FROM Emails WHERE clientName = '" + FrmLogin.username + "'");
            while (resultSet.next()) {
                inbox.add(new MyInbox(resultSet.getString("senderName"), resultSet.getString("sentDate"), resultSet.getString("subject"), resultSet.getString("body")));
            }
        }
    }

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private static String parseAddresses(Address[] address) {
        StringBuilder listAddress = new StringBuilder();

        if (address != null) {
            for (Address value : address) {
                listAddress.append(value.toString()).append(", ");
            }
        }
        if (listAddress.length() > 1) {
            listAddress = new StringBuilder(listAddress.substring(0, listAddress.length() - 2));
        }

        return listAddress.toString();
    }

    /**
     * Test downloading e-mail messages
     */
//    public static void main(String[] args) throws SQLException {
//        // for POP3
//        //String protocol = "pop3";
//        //String host = "pop.gmail.com";
//        //String port = "995";
//
//        // for IMAP
//        String protocol = "imap";
//        String host = "localhost";
//        String port = "143";
//
////        String userName = "account1@bilal.com";
////        String password = "123";
//        String userName = FrmLogin.username;
//        String password = FrmLogin.password;
//
//        System.out.println("Downloading emails from " + userName + "...");
//
//        EmailReceiver receiver = new EmailReceiver();
//        receiver.downloadEmails(protocol, host, port, userName, password);
//    }
}