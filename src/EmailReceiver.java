import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

class MyInbox {
    String from, date, subject, message, attachment;
    Message message1;
    MimeBodyPart part;
    MyInbox(String from, String date, String subject, String message, String attachment, MimeBodyPart part, Message message1) {
        this.from = from;
        this.date = date;
        this.subject = subject;
        this.message = message;
        this.attachment = attachment;
        this.part = part;
        this.message1 = message1;
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
                                      String userName, String password) {
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

            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();
            inbox.clear();

            for (int i = 0; i < messages.length; i++) {
                String attachment = "No";
                Message message = messages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = String.valueOf(message.getSentDate());

                String contentType = message.getContentType();
                String messageContent = "";

                // store attachment file name, separated by comma
                String attachFiles = "";

                MimeBodyPart part = null;
                if (contentType.contains("multipart")) {
                    attachment = "Yes";
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();



                            attachFiles += fileName + ", ";

//                            part.saveFile("D:/Attachments" + File.separator + part.getFileName());


//                            part.saveFile("D:/" + File.separator + fileName);
//                            part.saveFile("D:/Attachments/" + part.getFileName().replaceFirst("D:/", ""));

                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
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



                inbox.add(new MyInbox(from, sentDate, subject, messageContent, attachment, part, message));



//            for (Message msg : messages) {
//                Address[] fromAddress = msg.getFrom();
//                String from = fromAddress[0].toString();
//                String subject = msg.getSubject();
//                String toList = parseAddresses(msg
//                        .getRecipients(RecipientType.TO));
//                String ccList = parseAddresses(msg
//                        .getRecipients(RecipientType.CC));
//                String sentDate = msg.getSentDate().toString();
//
//                String contentType = msg.getContentType();
//                String messageContent = "";
//
////                if (contentType.contains("text/plain")
////                        || contentType.contains("text/html")) {
//                try {
//                    Object content = msg.getContent();
//                    if (content != null) {
//                        messageContent = content.toString();
//                        inbox.add(new MyInbox(from, sentDate, subject, messageContent));
//                    }
//                } catch (Exception ex) {
//                    messageContent = "[Error downloading content]";
//                    ex.printStackTrace();
//                }
////
//
//
//                if (contentType.contains("multipart")) {
//                    // this message may contain attachment
//                    Multipart multiPart = (Multipart) msg.getContent();
//
//                    for (int i = 0; i < multiPart.getCount(); i++) {
//                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
//                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
//                            part.saveFile("D:/Attachments/" + part.getFileName().replaceFirst("D:/", ""));
//                        }
//                    }
//                }


                // print out details of each message
//                System.out.println("Message #" + (i + 1) + ":");
//                System.out.println("\t From: " + from);
//                System.out.println("\t To: " + toList);
//                System.out.println("\t CC: " + ccList);
//                System.out.println("\t Subject: " + subject);
//                System.out.println("\t Sent Date: " + sentDate);
//                System.out.println("\t Message: " + messageContent);
            }

            // disconnect
//            folderInbox.close(false);
//            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            FrmLogin.isValid = false;
            JOptionPane.showMessageDialog(null, "Enter valid username and password", "Invalid Credentials", JOptionPane.ERROR_MESSAGE);
//            System.out.println("Could not connect to the message store");
//            ex.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
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
    public static void main(String[] args) {
        // for POP3
        //String protocol = "pop3";
        //String host = "pop.gmail.com";
        //String port = "995";

        // for IMAP
        String protocol = "imap";
        String host = "localhost";
        String port = "143";

//        String userName = "account1@bilal.com";
//        String password = "123";
        String userName = FrmLogin.username;
        String password = FrmLogin.password;

        System.out.println("Downloading emails from " + userName + "...");

        EmailReceiver receiver = new EmailReceiver();
        receiver.downloadEmails(protocol, host, port, userName, password);
    }
}