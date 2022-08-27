import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FrmLogin extends JFrame {
    public static boolean isValid = false;
    public static String username, password;


    static Connection connection;

    FrmLogin() throws ClassNotFoundException {
        connection = GlobalClass.connect();
        setTitle("Login Form");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel lblLogin = new JLabel("Login Form");
        lblLogin.setFont(new Font("Arial", Font.BOLD, 20));
        lblLogin.setBounds(200, 30, 200, 30);
        add(lblLogin);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(100, 100, 100, 30);
        add(lblUsername);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(250, 100, 200, 30);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(100, 150, 100, 30);
        add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(250, 150, 200, 30);
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(250, 200, 100, 30);
        add(btnLogin);

        btnLogin.addActionListener(e -> {
            login(txtUsername.getText(), txtPassword.getText());
        });
    }

    private void login(String username, String password) {
        FrmLogin.username = username;
        FrmLogin.password = password;
        EmailReceiver.downloadEmails("imap", "localhost", "143", username, password);
        if (isValid) {
            JOptionPane.showMessageDialog(null, "Login Successful");
//            new FrmInbox().setVisible(true);
//            setVisible(false);
        }
        else {
//            JOptionPane.showMessageDialog(null, "Login Failed");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        new FrmLogin().setVisible(true);
    }
}
