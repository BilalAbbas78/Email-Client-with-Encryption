import javax.swing.*;

public class FrmSettings extends JFrame {
    public static String ipAddress = "localhost";
    public static String smtpPort = "587";
    FrmSettings(){
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setTitle("Settings");

        JLabel lblIpAddress = new JLabel("IP Address");
        lblIpAddress.setBounds(50, 50, 100, 30);
        add(lblIpAddress);

        JTextField txtIpAddress = new JTextField();
        txtIpAddress.setBounds(150, 50, 200, 30);
        add(txtIpAddress);

        JLabel lblSmtpPort = new JLabel("SMTP Port");
        lblSmtpPort.setBounds(50, 100, 100, 30);
        add(lblSmtpPort);

        JTextField txtSmtpPort = new JTextField();
        txtSmtpPort.setBounds(150, 100, 200, 30);
        add(txtSmtpPort);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(150, 150, 100, 30);
        add(btnSave);

        txtIpAddress.setText(ipAddress);
        txtSmtpPort.setText(smtpPort);

        btnSave.addActionListener(e -> {
            ipAddress = txtIpAddress.getText();
            smtpPort = txtSmtpPort.getText();
            dispose();
        });


    }

    public static void main(String[] args) {
        new FrmSettings().setVisible(true);
    }
}
