import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GlobalClass {
    public static Connection connection;
    public static Connection connect() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:EmailClient.db");
            return connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}