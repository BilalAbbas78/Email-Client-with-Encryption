import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Objects;

public class FrmDashboard extends JFrame {

    public static JTabbedPane jtp;
    static Connection connection;
    static DefaultTableModel model;

    FrmDashboard() throws ClassNotFoundException, SQLException {
//        FrmLogin.connection.close();
        connection = GlobalClass.connect();
        setTitle("Dashboard Form");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JXTabbedPane tabbedPane = new JXTabbedPane(JTabbedPane.LEFT);
        AbstractTabRenderer renderer = (AbstractTabRenderer)tabbedPane.getTabRenderer();
        renderer.setPrototypeText("This text is a prototype");
        renderer.setHorizontalTextAlignment(SwingConstants.LEADING);

//        tabbedPane.btnComposeEmail("Compose Email", null, null, "Compose Email");


        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab("Inbox", null, inboxPanel(), "Inbox");
        tabbedPane.addTab("Sent", null, sentPanel(), "Sent");
//        FlowLayout f = new FlowLayout (FlowLayout.CENTER, 5, 0);

        JPanel pnlTab = new JPanel ();
        pnlTab.setOpaque (false);
        JButton btnComposeEmail = new JButton ("Compose Email +");
        btnComposeEmail.setOpaque (false); //
        btnComposeEmail.setBorder (null);
        btnComposeEmail.setContentAreaFilled (false);
        btnComposeEmail.setFocusPainted (false);
        btnComposeEmail.setFocusable (false);
        pnlTab.add (btnComposeEmail);
        tabbedPane.setTabComponentAt (0, pnlTab);

        JPanel pnlImportOwnCertificate = new JPanel ();
        pnlImportOwnCertificate.setOpaque (false);
        JButton btnImportOwnCertificate = new JButton ("Import Own Certificate");
        btnImportOwnCertificate.setOpaque (false); //
        btnImportOwnCertificate.setBorder (null);
        btnImportOwnCertificate.setContentAreaFilled (false);
        btnImportOwnCertificate.setFocusPainted (false);
        btnImportOwnCertificate.setFocusable (false);
        pnlImportOwnCertificate.add (btnImportOwnCertificate);
        tabbedPane.setTabComponentAt (1, pnlImportOwnCertificate);

        JPanel pnlImportOwnPrivateKey = new JPanel ();
        pnlImportOwnPrivateKey.setOpaque (false);
        JButton btnImportOwnPrivateKey = new JButton ("Import Own Private Key");
        btnImportOwnPrivateKey.setOpaque (false); //
        btnImportOwnPrivateKey.setBorder (null);
        btnImportOwnPrivateKey.setContentAreaFilled (false);
        btnImportOwnPrivateKey.setFocusPainted (false);
        btnImportOwnPrivateKey.setFocusable (false);
        pnlImportOwnPrivateKey.add (btnImportOwnPrivateKey);
        tabbedPane.setTabComponentAt (2, pnlImportOwnPrivateKey);

        JPanel pnlManageClientCertificates = new JPanel ();
        pnlManageClientCertificates.setOpaque (false);
        JButton btnManageClientCertificates = new JButton ("Manage Client Certificates");
        btnManageClientCertificates.setOpaque (false); //
        btnManageClientCertificates.setBorder (null);
        btnManageClientCertificates.setContentAreaFilled (false);
        btnManageClientCertificates.setFocusPainted (false);
        btnManageClientCertificates.setFocusable (false);
        pnlManageClientCertificates.add (btnManageClientCertificates);
        tabbedPane.setTabComponentAt (3, pnlManageClientCertificates);

        ActionListener listenerComposeEmail = e -> new FrmComposeMail().setVisible(true);

        ActionListener listenerImportOwnCertificate = e -> {
            X509Certificate certificate = MyCertificateGenerator.loadCertificateFromFile();
            if (certificate != null){
                if (certificate.getSubjectDN().getName().replaceFirst("DNQ=", "").equals(FrmLogin.username)) {
                    try {
                        if (isCertificateSignedByRoot(certificate, GlobalClass.rootCertificate.getPublicKey())) {
                            try {
                                if (isCertificatePresentInDB(certificate)){
                                    JOptionPane.showMessageDialog(null, "Certificate is already present in the database");
                                    return;
                                }
                                else {
                                    Statement statement = connection.createStatement();
                                    String sql = "INSERT INTO SelfCertificates VALUES ('" + certificate.getSubjectDN().getName().replaceFirst("DNQ=", "") + "','"   + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "','')";
                                    statement.execute(sql);
                                }
                            } catch (SQLException | CertificateEncodingException ex) {
                                throw new RuntimeException(ex);
                            }
                            JOptionPane.showMessageDialog(null, "Certificate loaded successfully");
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Certificate is not signed by the root certificate");
                        }
                    } catch (SQLException | CertificateEncodingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Certificate is not issued to you");
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Certificate not loaded");
        };

        ActionListener listenerImportOwnPrivateKey = e -> {
            PrivateKey privateKey = MyCertificateGenerator.loadPrivateKeyFromFile();
            if (privateKey != null){
                try {
                    Statement statement = connection.createStatement();
//                        String sql = "UPDATE SelfCertificates SET PrivateKey = '" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "' WHERE clientName = '" + FrmLogin.username + "'";
                    String sql = "UPDATE SelfCertificates SET PrivateKey = '" + AESWithHash.encrypt(Base64.getEncoder().encodeToString(privateKey.getEncoded()), FrmLogin.password) + "' WHERE clientName = '" + FrmLogin.username + "'";
                    statement.executeUpdate(sql);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Certificate is not inserted into the database");
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null, "Private Key loaded successfully");
            }
            else
                JOptionPane.showMessageDialog(null, "Private Key not loaded");
        };

        ActionListener listenerManageClientCertificates = e -> {
            try {
                new FrmClientCertificates().setVisible(true);
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        };

        btnComposeEmail.addActionListener (listenerComposeEmail);
        btnImportOwnCertificate.addActionListener (listenerImportOwnCertificate);
        btnImportOwnPrivateKey.addActionListener (listenerImportOwnPrivateKey);
        btnManageClientCertificates.addActionListener (listenerManageClientCertificates);

        tabbedPane.setSelectedIndex(4);



        tabbedPane.setBounds(0, 0, 1500, 820);
        add(tabbedPane);


    }

    JPanel inboxPanel() throws SQLException {
        JPanel pnlInbox = new JPanel();
        pnlInbox.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1300, 820);
        model = new DefaultTableModel(
                new String [] {
                        "Subject", "From", "Date",
                }, 0);

        JTable tblInbox = new JTable();
        scrollPane.setViewportView(tblInbox);
        setTblInbox(model);
        tblInbox.setModel(model);
        pnlInbox.add(scrollPane);

        tblInbox.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                FrmViewMessage frmViewMessage = new FrmViewMessage();
                Inbox inbox = EmailReceiver.inboxList.get(tblInbox.getSelectedRow());
                try {
                    Statement statement = FrmDashboard.connection.createStatement();
                    statement.execute("SELECT privateKey FROM SelfCertificates WHERE clientName = '" + FrmLogin.username + "'");
                    ResultSet resultSet = statement.getResultSet();
                    PrivateKey privateKey = null;
                    if (resultSet.next()) {
                        if (!Objects.equals(resultSet.getString("privateKey"), "")) {
                            privateKey = MyCertificateGenerator.getPrivateKeyFromString(AESWithHash.decrypt(resultSet.getString("privateKey"), FrmLogin.password));
                        }
                    }
                    String theString = inbox.part;
                    String[] words = theString.split("\\|");
                    String RSADecrypted = RSAEncryption.decrypt(words[0], privateKey);
                    byte[] decodedKey = Base64.getDecoder().decode(RSADecrypted);
                    byte[] encryptedBytes = Base64.getDecoder().decode(words[1]);
                    byte[] AESDecrypted = AESGCMEncryption.decrypt(encryptedBytes, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
                    EmailContent emailContent = EmailContent.deserialize(AESDecrypted);
                    frmViewMessage.setMessage(emailContent.from, emailContent.date.toString(), emailContent.subject, emailContent.message);
                    frmViewMessage.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Private Key may not be available or is incorrect");
                }
            }
        });
        return pnlInbox;
    }

    JPanel sentPanel() throws SQLException {
        JPanel pnlSent = new JPanel();
        pnlSent.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1300, 820);
        model = new DefaultTableModel(
                new String [] {
                        "Subject", "To", "Date",
                }, 0);

        JTable tblSent = new JTable();
        scrollPane.setViewportView(tblSent);
        setTblSent(model);
        tblSent.setModel(model);
        pnlSent.add(scrollPane);

        tblSent.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
//                FrmViewMessage frmViewMessage = new FrmViewMessage();
//                MySent sent = EmailSender.sent.get(tblSent.getSelectedRow());
//                frmViewMessage.setMessage(sent.to, sent.date.toString(), sent.subject, sent.message);
//                frmViewMessage.setVisible(true);
            }
        });
        return pnlSent;
    }

    private void setTblSent(DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        EmailReceiver.sentList.clear();
        EmailReceiver.downloadEmails("imap", "localhost", "143", FrmLogin.username, FrmLogin.password);
        for (Sent sent : EmailReceiver.sentList) {
            model.addRow(new Object[]{sent.subject, sent.to, sent.date});
        }
    }

    static void setTblInbox(DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        EmailReceiver.inboxList.clear();
        EmailReceiver.downloadEmails("imap", "localhost", "143", FrmLogin.username, FrmLogin.password);
        for (Inbox inbox : EmailReceiver.inboxList) {
            model.addRow(new Object[]{inbox.subject, inbox.from, inbox.date});
        }
    }

    boolean isCertificatePresentInDB(X509Certificate certificate) throws SQLException, CertificateEncodingException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SelfCertificates WHERE certificate = '" + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "'");
        boolean isPresent = false;
        while (resultSet.next()) {
            isPresent = true;
        }
        return isPresent;
    }

    static boolean isCertificateSignedByRoot(X509Certificate certificate, PublicKey publicKey) throws SQLException, CertificateEncodingException {
        boolean isSigned = false;
            try {
                certificate.verify(publicKey);
                isSigned = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        return isSigned;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        new FrmLogin();
        new FrmDashboard().setVisible(true);
    }
}