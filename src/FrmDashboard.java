import org.openhab.io.jetty.certificate.internal.CertificateGenerator;

import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class FrmDashboard extends JFrame {

    public static JTabbedPane jtp;
    static Connection connection;

    FrmDashboard() throws ClassNotFoundException {
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

//        tabbedPane.addTab("Compose Email", null, null, "Compose Email");


        tabbedPane.addTab ("test", null);
        tabbedPane.addTab("Inbox", null, inboxPanel(), "Inbox");
        tabbedPane.addTab("Sent", null, new JPanel(), "Sent");
//        FlowLayout f = new FlowLayout (FlowLayout.CENTER, 5, 0);

        // Make a small JPanel with the layout and make it non-opaque
        JPanel pnlTab = new JPanel ();
        pnlTab.setOpaque (false);
        // Create a JButton for adding the tabs
        JButton addTab = new JButton ("Compose Email +");
        addTab.setOpaque (false); //
        addTab.setBorder (null);
        addTab.setContentAreaFilled (false);
        addTab.setFocusPainted (false);
        addTab.setFocusable (false);

        pnlTab.add (addTab);
        tabbedPane.setTabComponentAt (0, pnlTab);

        ActionListener listener = e -> {
            addTab.setFocusable (false);

            new FrmComposeMail().setVisible(true);

            String title = "Tab " + String.valueOf (tabbedPane.getTabCount () - 1);
//            tabbedPane.addTab (title, new JLabel (title));
            System.out.println(title);
        };
//        addTab.setFocusable (false);
        addTab.addActionListener (listener);
//        addTab.setFocusable (false);

        tabbedPane.setSelectedIndex(1);






//        tabbedPane.addTab("Long text", UIManager.getIcon("OptionPane.warningIcon"), pnl2, "Warning tool tip");
//        tabbedPane.addTab("This is a really long text", UIManager.getIcon("OptionPane.errorIcon"), pnl3, "Error tool tip");






        tabbedPane.setBounds(0, 0, 1500, 820);
        add(tabbedPane);


    }

    JPanel inboxPanel() {
        JPanel pnlInbox = new JPanel();
        pnlInbox.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1300, 350);
        DefaultTableModel model = new DefaultTableModel(
                new String [] {
                "Subject", "From", "Date",
        }, 0);

        JTable tblInbox = new JTable();
        scrollPane.setViewportView(tblInbox);
        setTblInbox(model);
        tblInbox.setModel(model);
        pnlInbox.add(scrollPane);

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setBounds(10, 350, 1000, 30);
        pnlInbox.add(lblFrom);

        JLabel lblDate = new JLabel("Date:");
        lblDate.setBounds(10, 380, 1000, 30);
        pnlInbox.add(lblDate);

        JLabel lblSubject = new JLabel("Subject:");
        lblSubject.setBounds(10, 410, 1000, 30);
        pnlInbox.add(lblSubject);

        JButton btnLoadClientCertificate = new JButton("Load Client Certificate");
        btnLoadClientCertificate.setBounds(500, 410, 200, 30);
        pnlInbox.add(btnLoadClientCertificate);

        JTextArea txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        txtMessage.setEditable(false);
        JScrollPane spTxtMessage = new JScrollPane(txtMessage);
        spTxtMessage.setBounds(0, 450, 1300, 370);
        pnlInbox.add(spTxtMessage);

        tblInbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblInbox.getSelectedRow();
                txtMessage.setText("Message: " + EmailReceiver.inbox.get(row).message);
                lblFrom.setText("From: " + EmailReceiver.inbox.get(row).from);
                lblDate.setText("Date: " + EmailReceiver.inbox.get(row).date);
                lblSubject.setText("Subject: " + EmailReceiver.inbox.get(row).subject);
            }
        });

        btnLoadClientCertificate.addActionListener(e -> {
            X509Certificate certificate = MyCertificateGenerator.loadCertificateFromFile();
            if (certificate != null){
                try {
                    if (isCertificatePresentInDB(certificate)){
                        JOptionPane.showMessageDialog(null, "Certificate is already present in the database");
                        return;
                    }
                    else {
                        PrivateKey privateKey = MyCertificateGenerator.privateKey;
                        Statement statement = connection.createStatement();
                        String sql = "INSERT INTO ImportedClientCertificates VALUES ('" + certificate.getSubjectDN().getName().replaceFirst("DNQ=", "") + "','"   + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "','" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "')";
                        statement.executeUpdate(sql);
//                        JOptionPane.showMessageDialog(null, "Certificate is inserted into the database");
                    }
                } catch (SQLException | CertificateEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null, "Certificate loaded successfully");
            }
            else
                JOptionPane.showMessageDialog(null, "Certificate not loaded");
        });

        return pnlInbox;
    }

    void setTblInbox(DefaultTableModel model) {
        EmailReceiver.downloadEmails("imap", "localhost", "143", FrmLogin.username, FrmLogin.password);
        for (MyInbox inbox : EmailReceiver.inbox) {
            model.addRow(new Object[]{inbox.subject, inbox.from, inbox.date});
        }
    }

    boolean isCertificatePresentInDB(X509Certificate certificate) throws SQLException, CertificateEncodingException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM ImportedClientCertificates WHERE certificate = '" + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "'");
        boolean isPresent = false;
        while (resultSet.next()) {
            isPresent = true;
        }
        return isPresent;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        new FrmDashboard().setVisible(true);
    }
}