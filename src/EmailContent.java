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

        // Serialization
        try
        {
            //Saving of object in a file
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            // Method for serialization of object
            out.writeObject(object);

//            System.out.println(byteArray);

            out.close();
            System.out.println("Object has been serialized");
            return bos.toByteArray();


        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        return null;


    }

    static EmailContent deserialize(byte[] bytesArray){
        EmailContent object1 = null;

        // Deserialization
        try
        {
            // Reading the object from a file
            ByteArrayInputStream bis = new ByteArrayInputStream(bytesArray);
            ObjectInputStream in = new ObjectInputStream(bis);

            // Method for deserialization of object
            object1 = (EmailContent) in.readObject();

            in.close();

        }

        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println("IOException is caught");
        }
        return object1;
    }

    public static void main(String[] args) {
        EmailContent emailContent = new EmailContent("Subject", "Hello msg", "a", new Date());
        EmailContent.deserialize(EmailContent.serialize(emailContent));
    }

}
