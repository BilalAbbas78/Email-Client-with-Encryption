import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FrmAddressBook extends JFrame {
//    static AddressBook addressBook = new AddressBook();
    static Contact selfSelectedContact = null;

    FrmAddressBook() {

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
        setTblSelfAddressBook(model);

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

        setTblOthers(model2, model3);

        JButton btnImportOthersAddressBook = new JButton("Import Others' Address Book");
        btnImportOthersAddressBook.setBounds(250, 290, 200, 30);
        add(btnImportOthersAddressBook);





        btnAddSelfAddressReceiver.addActionListener(e -> {
            if(txtAddSelfAddressReceiver.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Receiver's name is empty");
            }
            else {
                selfSelectedContact.behalfList.add(txtAddSelfAddressReceiver.getText().trim());
                setTblSelfAddressBook(model);
                txtAddSelfAddressReceiver.setText("");
            }
        });

        btnDeleteSelfAddressReceiver.addActionListener(e -> {
            if(tblSelfAddressBook.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Please select a receiver");
            }
            else {
                selfSelectedContact.behalfList.remove(tblSelfAddressBook.getSelectedRow());
                setTblSelfAddressBook(model);
            }
        });




    }

    private void setTblOthers(DefaultTableModel model2, DefaultTableModel model3) {
        model2.setRowCount(0);
        model3.setRowCount(0);
        for (Contact contact : GlobalClass.addressBook.contacts) {
            if (!contact.user.equals(FrmLogin.username)) {
                model2.addRow(new Object[]{contact.user});
//                selfSelectedContact = contact;
                for (String behalf : contact.behalfList) {
                    if (!behalf.equals(contact.user)) {
                        model3.addRow(new Object[]{behalf});
                    }
                }
            }
        }
    }

    void setTblSelfAddressBook(DefaultTableModel model) {
        model.setRowCount(0);
        for (Contact contact : GlobalClass.addressBook.contacts) {
            if (contact.user.equals(FrmLogin.username)) {
                selfSelectedContact = contact;
                for (String behalf : contact.behalfList) {
                    if (!behalf.equals(contact.user)) {
                        model.addRow(new Object[]{behalf});
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new FrmAddressBook().setVisible(true);
    }
}
