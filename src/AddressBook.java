import java.util.ArrayList;

public class AddressBook {
    ArrayList<Contact> contacts = new ArrayList<>();

    void addContact(Contact contact){
        contacts.add(contact);
    }

    String getUserFromBehalf(String fromBehalf) {
        for (Contact contact : contacts) {
            for (String behalf : contact.behalfList) {
                if (behalf.equals(fromBehalf)) {
                    return contact.user;
                }
            }
        }
        return null;
    }

}

class Contact {
    String user;
    ArrayList<String> behalfList = new ArrayList<>();
    Contact(String user) {
        this.user = user;
    }
}
