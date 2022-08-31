import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GlobalClass {
    public static Connection connection;
    public static X509Certificate rootCertificate;
    public static String receiver;
    public static Connection connect() throws ClassNotFoundException {
        rootCertificate = null;
        try {
            rootCertificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream("root.cer"));
        } catch (FileNotFoundException | CertificateException e) {
            throw new RuntimeException(e);
        }
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:EmailClient.db");
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    GlobalClass() {
    }
}