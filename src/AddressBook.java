import java.io.*;
import java.util.ArrayList;

public class AddressBook {
    ArrayList<Contact> contacts = new ArrayList<>();

    void addContact(Contact contact){
        contacts.add(contact);
    }

    ArrayList<String> getOthersUser(String fromBehalf) {
        ArrayList<String> others = new ArrayList<>();
        for (Contact contact : contacts) {
            if (!contact.user.equals(FrmLogin.username)) {
                for (String behalf : contact.behalfList) {
                    if (behalf.equals(fromBehalf)) {
                        others.add(contact.user);
                    }
                }
            }
        }
        return others;
    }

    String getSelfUser(String fromBehalf) {
        for (Contact contact : contacts) {
            if (contact.user.equals(FrmLogin.username)) {
                for (String behalf : contact.behalfList) {
                    if (behalf.equals(fromBehalf)) {
                        return contact.user;
                    }
                }
            }
        }
        return null;
    }

}

class Contact implements Serializable {
    String user;
    ArrayList<String> behalfList = new ArrayList<>();
    Contact(String user) {
        this.user = user;
        behalfList.add(user);
    }

    static byte[] serialize(Contact object){
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

    static Contact deserialize(byte[] bytesArray){
        Contact object1 = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytesArray);
            ObjectInputStream in = new ObjectInputStream(bis);
            object1 = (Contact) in.readObject();
            in.close();
        }
        catch(IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return object1;
    }
}
