import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class FrmAddressBook extends JFrame {
    //    static AddressBook addressBook = new AddressBook();
    static Contact selfSelectedContact = null;

    FrmAddressBook() throws SQLException {

        setTitle("Address Book");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel lblAddressBook = new JLabel("Address Book");
        lblAddressBook.setFont(new Font("Arial", Font.BOLD, 20));
        lblAddressBook.setBounds(200, 10, 200, 30);
        add(lblAddressBook);

        JLabel lblSelfAddressBook = new JLabel("Self Address Book");
        lblSelfAddressBook.setBounds(10, 50, 200, 30);
        add(lblSelfAddressBook);

        JScrollPane spSelfAddressBook = new JScrollPane();
        spSelfAddressBook.setBounds(10, 80, 200, 200);
        JTable tblSelfAddressBook = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Receiver's Name");
        tblSelfAddressBook.setModel(model);
        spSelfAddressBook.setViewportView(tblSelfAddressBook);
        add(spSelfAddressBook);
//        setTblSelfAddressBook(model);

        JLabel lblAddSelfAddressReceiver = new JLabel("Add Receiver");
        lblAddSelfAddressReceiver.setBounds(10, 290, 200, 30);
        add(lblAddSelfAddressReceiver);

        JTextField txtAddSelfAddressReceiver = new JTextField();
        txtAddSelfAddressReceiver.setBounds(10, 320, 200, 30);
        add(txtAddSelfAddressReceiver);

        JButton btnAddSelfAddressReceiver = new JButton("Add");
        btnAddSelfAddressReceiver.setBounds(10, 360, 90, 30);
        add(btnAddSelfAddressReceiver);

        JButton btnDeleteSelfAddressReceiver = new JButton("Delete");
        btnDeleteSelfAddressReceiver.setBounds(120, 360, 90, 30);
        add(btnDeleteSelfAddressReceiver);

        JButton btnExportSelfAddressReceiver = new JButton("Export");
        btnExportSelfAddressReceiver.setBounds(70, 400, 90, 30);
        add(btnExportSelfAddressReceiver);




        JLabel lblOthersAddressBook = new JLabel("Others' Address Book");
        lblOthersAddressBook.setBounds(250, 50, 200, 30);
        add(lblOthersAddressBook);

        JScrollPane spOthersReceiver = new JScrollPane();
        spOthersReceiver.setBounds(250, 80, 200, 200);
        JTable tblOthersReceiver = new JTable();
        DefaultTableModel model2 = new DefaultTableModel();
        model2.addColumn("Receiver's Name");
        tblOthersReceiver.setModel(model2);
        spOthersReceiver.setViewportView(tblOthersReceiver);
        add(spOthersReceiver);

        JScrollPane spOthersBehalf = new JScrollPane();
        spOthersBehalf.setBounds(470, 80, 200, 200);
        JTable tblOthersBehalf = new JTable();
        DefaultTableModel model3 = new DefaultTableModel();
        model3.addColumn("Behalf's Name");
        tblOthersBehalf.setModel(model3);
        spOthersBehalf.setViewportView(tblOthersBehalf);
        add(spOthersBehalf);

        setTblOthers(model2);

        JButton btnImportOthersAddressBook = new JButton("Import Others' Address Book");
        btnImportOthersAddressBook.setBounds(250, 290, 200, 30);
        add(btnImportOthersAddressBook);

        Statement statement = FrmDashboard.connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM AddressBook WHERE user = '" + FrmLogin.username + "'");
        if (resultSet.next()) {
//            System.out.println("Address Book Exists");
//            System.out.println("hello "+resultSet.getString("contactsList"));
            String str = resultSet.getString("contactsList");
            byte[] bytes = Base64.getDecoder().decode(str);
            selfSelectedContact = Contact.deserialize(bytes);
        }
        else {
            selfSelectedContact = new Contact(FrmLogin.username);
            Statement statement2 = FrmDashboard.connection.createStatement();
            statement2.execute("INSERT INTO AddressBook (user, contactsList) VALUES ('" + FrmLogin.username + "', '" + Base64.getEncoder().encodeToString(Contact.serialize(selfSelectedContact)) + "')");
        }

        setTblSelfAddressBook(model);


        tblOthersReceiver.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                model3.setRowCount(0);
                String selectedReceiver = tblOthersReceiver.getValueAt(tblOthersReceiver.getSelectedRow(), 0).toString();
                for (Contact contact : GlobalClass.addressBook.contacts) {
                    if (contact.user.equals(selectedReceiver)) {
                        for (String str: contact.behalfList){
                            if (!str.equals(contact.user)) {
                                model3.addRow(new Object[]{str});
                            }
                        }
                    }
                }
            }
        });




        btnAddSelfAddressReceiver.addActionListener(e -> {
            if(txtAddSelfAddressReceiver.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Receiver's name is empty");
            }
            else {
                try {
                    selfSelectedContact.behalfList.add(txtAddSelfAddressReceiver.getText().trim());
                    Statement statement2 = FrmDashboard.connection.createStatement();
                    statement2.execute("UPDATE AddressBook SET contactsList = '" + Base64.getEncoder().encodeToString(Contact.serialize(selfSelectedContact)) + "' WHERE user = '" + FrmLogin.username + "'");
                    setTblSelfAddressBook(model);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                txtAddSelfAddressReceiver.setText("");
            }
        });

        btnDeleteSelfAddressReceiver.addActionListener(e -> {
            if(tblSelfAddressBook.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Please select a receiver");
            }
            else {
                selfSelectedContact.behalfList.remove(tblSelfAddressBook.getSelectedRow());
                Statement statement2 = null;
                try {
                    statement2 = FrmDashboard.connection.createStatement();
                    statement2.execute("UPDATE AddressBook SET contactsList = '" + Base64.getEncoder().encodeToString(Contact.serialize(selfSelectedContact)) + "' WHERE user = '" + FrmLogin.username + "'");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                setTblSelfAddressBook(model);
            }
        });

        btnExportSelfAddressReceiver.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a place to save the file");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Address Book", "ab"));
            fileChooser.setSelectedFile(new File(FrmLogin.username + ".ab"));
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToSave);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(selfSelectedContact);
                    objectOutputStream.close();
                    fileOutputStream.close();
                    JOptionPane.showMessageDialog(null, "Address Book Exported Successfully");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });




    }

    private void setTblOthers(DefaultTableModel model2) {
        model2.setRowCount(0);
        for (Contact contact : GlobalClass.addressBook.contacts) {
            if (!contact.user.equals(FrmLogin.username)) {
                model2.addRow(new Object[]{contact.user});
            }
        }
    }

    void setTblSelfAddressBook(DefaultTableModel model) {
        model.setRowCount(0);
        for (String behalf : selfSelectedContact.behalfList) {
            if (!behalf.equals(FrmLogin.username)) {
                model.addRow(new Object[]{behalf});
            }
        }
    }

    public static void main(String[] args) throws SQLException, MessagingException, ClassNotFoundException {
        new FrmDashboard();
        new FrmAddressBook().setVisible(true);
    }
}
