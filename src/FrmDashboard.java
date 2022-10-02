import javax.crypto.spec.SecretKeySpec;
import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class FrmDashboard extends JFrame {

    public static JTabbedPane jtp;
    static Connection connection;
    static DefaultTableModel mdlInbox, mdlSent;
    static Inbox selectedInbox;
    static PrivateKey privateKey = null;
    static JTable tblInbox;

    FrmDashboard() throws ClassNotFoundException, SQLException, MessagingException {
//        FrmLogin.connection.close();

        connection = GlobalClass.connect();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setTitle();

        JXTabbedPane tabbedPane = new JXTabbedPane(JTabbedPane.LEFT);
        AbstractTabRenderer renderer = (AbstractTabRenderer)tabbedPane.getTabRenderer();
        renderer.setPrototypeText("This text is a prototype");
        renderer.setHorizontalTextAlignment(SwingConstants.LEADING);

//        tabbedPane.btnComposeEmail("Compose Email", null, null, "Compose Email");


        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
        tabbedPane.addTab ("test", null);
//        tabbedPane.addTab ("test", null);
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

        JPanel pnlAddressBook = new JPanel ();
        pnlAddressBook.setOpaque (false);
        JButton btnAddressBook = new JButton ("Address Book");
        btnAddressBook.setOpaque (false); //
        btnAddressBook.setBorder (null);
        btnAddressBook.setContentAreaFilled (false);
        btnAddressBook.setFocusPainted (false);
        btnAddressBook.setFocusable (false);
        pnlAddressBook.add (btnAddressBook);
        tabbedPane.setTabComponentAt (4, pnlAddressBook);

//        JPanel pnlSettings = new JPanel ();
//        pnlSettings.setOpaque (false);
//        JButton btnSettings = new JButton ("Settings");
//        btnSettings.setOpaque (false); //
//        btnSettings.setBorder (null);
//        btnSettings.setContentAreaFilled (false);
//        btnSettings.setFocusPainted (false);
//        btnSettings.setFocusable (false);
//        pnlSettings.add (btnSettings);
//        tabbedPane.setTabComponentAt (5, pnlSettings);

        ActionListener listenerComposeEmail = e -> new FrmComposeMail().setVisible(true);

        ActionListener listenerAddressBook = e -> {
            try {
                new FrmAddressBook().setVisible(true);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

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
                                    setTitle("Dashboard Form - User: " + certificate.getSubjectDN().getName().replaceFirst("DNQ=", "") + "   Expiry Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(certificate.getNotAfter()));
                                    JOptionPane.showMessageDialog(null, "Certificate loaded successfully");
                                }
                            } catch (SQLException | CertificateEncodingException ex) {
                                throw new RuntimeException(ex);
                            }
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
                X509Certificate certificate = null;
                try {
                    Statement statement1 = connection.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM SelfCertificates WHERE clientName = '" + FrmLogin.username + "'");
                    if (resultSet1.next()){
                        certificate = MyCertificateGenerator.getCertificateFromString(resultSet1.getString("Certificate"));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (certificate != null) {
                    // create a challenge
                    byte[] challenge = new byte[10000];
                    ThreadLocalRandom.current().nextBytes(challenge);

                    boolean keyPairMatches;
                    // sign using the private key
                    try {
                        Signature sig = Signature.getInstance("SHA256withRSA");
                        sig.initSign(privateKey);
                        sig.update(challenge);
                        byte[] signature = sig.sign();


                        Signature sig2 = Signature.getInstance("SHA256withRSA");
                        sig2.initVerify(certificate.getPublicKey());
                        sig2.update(challenge);
                        keyPairMatches = sig2.verify(signature);
                    }
                    catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException ex) {
                        throw new RuntimeException(ex);
                    }

                    if (keyPairMatches){
                        try {
                            Statement statement = connection.createStatement();
//                        String sql = "UPDATE SelfCertificates SET PrivateKey = '" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "' WHERE clientName = '" + FrmLogin.username + "'";
                            String sql = "UPDATE SelfCertificates SET PrivateKey = '" + AESWithHash.encrypt(Base64.getEncoder().encodeToString(privateKey.getEncoded()), FrmLogin.password) + "' WHERE clientName = '" + FrmLogin.username + "'";
                            statement.executeUpdate(sql);
                            JOptionPane.showMessageDialog(null, "Private Key loaded successfully");
                        }
                        catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Certificate is not inserted into the database");
                            throw new RuntimeException(ex);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Private key does not match the certificate");
                    }
                }
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

//        ActionListener listenerSettings = e -> {
//                new FrmSettings().setVisible(true);
//        };

        btnComposeEmail.addActionListener (listenerComposeEmail);
        btnImportOwnCertificate.addActionListener (listenerImportOwnCertificate);
        btnImportOwnPrivateKey.addActionListener (listenerImportOwnPrivateKey);
        btnManageClientCertificates.addActionListener (listenerManageClientCertificates);
        btnAddressBook.addActionListener (listenerAddressBook);
//        btnSettings.addActionListener (listenerSettings);

        tabbedPane.setSelectedIndex(5);



        tabbedPane.setBounds(0, 0, 1500, 820);
        add(tabbedPane);


    }

    public static void deleteSelectedMessage() {

        int row = tblInbox.getSelectedRow();

        int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure want to delete this email?","Delete", JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            try {
                EmailReceiver.inboxList.get(row).message.setFlag(Flags.Flag.DELETED, true);
                EmailReceiver.inboxList.remove(row);
                mdlInbox.removeRow(row);
                JOptionPane.showMessageDialog(null, "Message Deleted successfully");
            } catch (MessagingException ex) {
                JOptionPane.showMessageDialog(null, "Message can't be deleted");
                ex.printStackTrace();
            }
        }


    }

    JPanel inboxPanel() throws SQLException, MessagingException {
        JPanel pnlInbox = new JPanel();
        pnlInbox.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1300, 820);
        mdlInbox = new DefaultTableModel(
                new String [] {
                        "Subject", "From", "Date",
                }, 0);

        tblInbox = new JTable();
        scrollPane.setViewportView(tblInbox);
        setTblInbox(mdlInbox);
        tblInbox.setModel(mdlInbox);
        pnlInbox.add(scrollPane);

        tblInbox.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                selectedInbox = EmailReceiver.inboxList.get(tblInbox.getSelectedRow());
                try {
                    Statement statement = FrmDashboard.connection.createStatement();
                    statement.execute("SELECT privateKey FROM SelfCertificates WHERE clientName = '" + FrmLogin.username + "'");
                    ResultSet resultSet = statement.getResultSet();
                    if (resultSet.next()) {
                        if (!Objects.equals(resultSet.getString("privateKey"), "")) {
                            privateKey = MyCertificateGenerator.getPrivateKeyFromString(AESWithHash.decrypt(resultSet.getString("privateKey"), FrmLogin.password));
                        }
                    }

                    String from = selectedInbox.from;
                    from = from.replaceFirst("\"", "");
                    from = from.substring(0, from.indexOf("\""));
                    PublicKey senderPublicKey = null;
                    Statement statement2 = FrmDashboard.connection.createStatement();
                    statement2.execute("SELECT certificate FROM ClientCertificates WHERE clientName = '" + from + "'");
                    ResultSet resultSet2 = statement2.getResultSet();
                    if (resultSet2.next()) {
                        if (!Objects.equals(resultSet2.getString("certificate"), "")) {
                            X509Certificate certificate = MyCertificateGenerator.getCertificateFromString(resultSet2.getString("certificate"));
                            if (certificate != null)
                                senderPublicKey = certificate.getPublicKey();
                        }
                    }




                    String theString = selectedInbox.part;
                    String[] words = theString.split("\\|");



                    if (senderPublicKey == null){
                        JOptionPane.showMessageDialog(null, "Sender's public key is not found");
                        return;
                    }
                    else {
                        String strToVerify = words[0] + "|" + words[1];
                        System.out.println(strToVerify);
                        byte[] signatureBytes = Base64.getDecoder().decode(words[2].getBytes());
                        Signature sig2 = Signature.getInstance("SHA256withRSA");
                        sig2.initVerify(senderPublicKey);
                        sig2.update(strToVerify.getBytes(StandardCharsets.UTF_8));
                        boolean keyPairMatches = sig2.verify(signatureBytes);
                        System.out.println("Verifying signature: " + keyPairMatches);
                    }




                    String RSADecrypted = RSAEncryption.decrypt(words[0], privateKey);

                    String[] words2 = RSADecrypted.split("\\|");

                    System.out.println("Decrypted: " + RSADecrypted);
                    System.out.println("Decrypted 0: " + words2[0]);
//                    System.out.println("Decrypted 1: " + words2[1]);

                    byte[] iv = Base64.getDecoder().decode(words2[1]);


                    byte[] decodedKey = Base64.getDecoder().decode(words2[0]);
                    byte[] encryptedBytes = Base64.getDecoder().decode(words[1]);
                    byte[] AESDecrypted = AESGCMEncryption.decrypt(encryptedBytes, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"), iv);
                    EmailContent emailContent = EmailContent.deserialize(AESDecrypted);
                    FrmViewMessage frmViewMessage = new FrmViewMessage(emailContent.from, emailContent.to, emailContent.date.toString(), emailContent.subject, emailContent.message, selectedInbox.parts);
//                    frmViewMessage.setMessage(;
                    frmViewMessage.setVisible(true);
                }
                catch (SignatureException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Message is not signed by the sender");
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Private Key may not be available or is incorrect");
                }
            }
        });
        return pnlInbox;
    }

    JPanel sentPanel() throws SQLException, MessagingException {
        JPanel pnlSent = new JPanel();
        pnlSent.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1300, 820);
        mdlSent = new DefaultTableModel(
                new String [] {
                        "Subject", "To", "Date",
                }, 0);

        JTable tblSent = new JTable();
        scrollPane.setViewportView(tblSent);
        setTblSent(mdlSent);
        tblSent.setModel(mdlSent);
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

    private void setTblSent(DefaultTableModel model) throws SQLException, MessagingException {
        model.setRowCount(0);
        EmailReceiver.sentList.clear();
        EmailReceiver.downloadEmails("imap", FrmSettings.ipAddress, FrmSettings.imapPort, FrmLogin.username, FrmLogin.password);
        for (Sent sent : EmailReceiver.sentList) {
            model.addRow(new Object[]{sent.subject, sent.to, sent.date});
        }
    }

    static void setTblInbox(DefaultTableModel model) throws SQLException, MessagingException {
        model.setRowCount(0);
        EmailReceiver.inboxList.clear();
        EmailReceiver.downloadEmails("imap", FrmSettings.ipAddress, FrmSettings.imapPort, FrmLogin.username, FrmLogin.password);
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

    void setTitle() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SelfCertificates WHERE clientName = '" + FrmLogin.username + "'");
        if (resultSet.next()) {
            X509Certificate certificate = MyCertificateGenerator.getCertificateFromString(resultSet.getString("certificate"));
            if (certificate != null) {
                setTitle("Dashboard Form - User: " + resultSet.getString("clientName") + "   Expiry Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(certificate.getNotAfter()));
            }
        }
        else {
            setTitle("Dashboard");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, MessagingException {
//        new FrmLogin();
        new FrmDashboard().setVisible(true);
    }
}