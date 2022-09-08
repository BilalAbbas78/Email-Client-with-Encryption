import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.Base64;

public class GlobalClass {
    public static Connection connection;
    public static X509Certificate rootCertificate;
    public static String receiver;
    static AddressBook addressBook = new AddressBook();

    public static Connection connect() throws ClassNotFoundException, SQLException {



        rootCertificate = null;
        try {
            rootCertificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream("root.cer"));
        }
        catch (FileNotFoundException | CertificateException e) {
            throw new RuntimeException(e);
        }
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:EmailClient.db");
            initializeAddressBook();
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        return null;
    }

    private static void initializeAddressBook() throws SQLException {

        addressBook.contacts.clear();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM AddressBook");
        while (resultSet.next()) {
            addressBook.contacts.add(Contact.deserialize(Base64.getDecoder().decode(resultSet.getString("contactsList"))));
        }
        System.out.println("Address Book Initialized");
        for (Contact contact : addressBook.contacts) {
            System.out.println(contact.user);
        }

        Contact c = new Contact("user2@xyz.com");
        c.behalfList.add("ewq");
        c.behalfList.add("dsa");
        addressBook.addContact(c);

//
//        Contact contact = new Contact("user2@xyz.com");
//        contact.behalfList.add("abc");
//        Contact contact2 = new Contact("user1@xyz.com");
//        contact2.behalfList.add("qwe");
//        addressBook.addContact(contact);
//        addressBook.addContact(contact2);

    }

    GlobalClass() {
    }
}