import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.Date;

public class EmailContent implements Serializable {
//    static byte[] byteArray;

    String subject, message, from;
    Date date;
    EmailContent(String subject, String message, String from, Date date){
        this.subject = subject;
        this.message = message;
        this.from = from;
        this.date = date;
    }

    static byte[] serialize(EmailContent object){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.close();
            return bos.toByteArray();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static EmailContent deserialize(byte[] bytesArray){
        EmailContent object1 = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytesArray);
            ObjectInputStream in = new ObjectInputStream(bis);
            object1 = (EmailContent) in.readObject();
            in.close();
        }
        catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return object1;
    }



//    public static void main(String[] args) {
//        EmailContent emailContent = new EmailContent("Subject", "Hello msg", "a", new Date());
//        EmailContent.deserialize(EmailContent.serialize(emailContent));
//    }

}

