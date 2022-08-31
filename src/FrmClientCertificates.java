import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Objects;

public class FrmClientCertificates extends JFrame {
    Connection connection;
    FrmClientCertificates() throws SQLException, ClassNotFoundException {
        connection = FrmDashboard.connection;
        setTitle("Client Certificates");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setVisible(true);

        DefaultTableModel model = new DefaultTableModel(
                new String [] {
                        "Client Name"
                }, 0
        );

        setTblClients(model);

        JScrollPane spTblClients = new JScrollPane();
        JTable tblClients = new JTable();
        tblClients.setModel(model);
        spTblClients.setViewportView(tblClients);
        spTblClients.setBounds(10, 10, 560, 200);
        add(spTblClients);

        JButton btnImport = new JButton("Import");
        btnImport.setBounds(160, 300, 100, 30);
        add(btnImport);

        JButton btnRemove = new JButton("Remove");
        btnRemove.setBounds(320, 300, 100, 30);
        add(btnRemove);

        btnImport.addActionListener(e -> {
            importClientCertificate();
            model.setRowCount(0);
            try {
                setTblClients(model);
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });

        btnRemove.addActionListener(e -> {
            int row = tblClients.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a client to remove");
                return;
            }
            String clientName = (String) tblClients.getValueAt(row, 0);
            removeClientCertificate(clientName);
            model.setRowCount(0);
            try {
                setTblClients(model);
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void removeClientCertificate(String clientName) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM ClientCertificates WHERE clientName = '" + clientName + "'");
            JOptionPane.showMessageDialog(null, "Client certificate removed successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void importClientCertificate() {
        X509Certificate certificate = MyCertificateGenerator.loadCertificateFromFile();
        if (certificate != null){
            try {
                if (isCertificatePresentInDB(certificate)){
                    JOptionPane.showMessageDialog(null, "Certificate is already present in the database");
                    return;
                }
                else {
                    Statement statement = connection.createStatement();
                    String sql = "INSERT INTO ClientCertificates VALUES ('" + certificate.getSubjectDN().getName().replaceFirst("DNQ=", "") + "','"   + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "')";
                    statement.executeUpdate(sql);
                    statement.close();
                }
            } catch (SQLException | CertificateEncodingException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(null, "Certificate loaded successfully");
        }
        else
            JOptionPane.showMessageDialog(null, "Certificate not loaded");
    }

    void setTblClients(DefaultTableModel model) throws SQLException, ClassNotFoundException {
//        Statement statement = FrmDashboard.connection.createStatement();
        Statement statement = Objects.requireNonNull(connection).createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM ClientCertificates");
        while (resultSet.next()) {
            model.addRow(new Object[] {
                    resultSet.getString("ClientName")
            });
        }
        statement.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new FrmClientCertificates().setVisible(true);
    }

    private boolean isCertificatePresentInDB(X509Certificate certificate) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ClientCertificates WHERE Certificate = '" + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "'");
            return resultSet.next();
        } catch (SQLException | CertificateEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
