import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class FrmSelectTo extends JFrame {
    public FrmSelectTo() {
        setTitle("Select To");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JTable tblTo = new JTable();
        //noinspection rawtypes
        DefaultTableModel tblToModel = new DefaultTableModel() {
            @Override
            public Class getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        tblTo.setModel(tblToModel);
        JScrollPane spTo = new JScrollPane(tblTo);
        spTo.setBounds(10, 10, 570, 300);
        add(spTo);

        tblToModel.addColumn("Select");
        tblToModel.addColumn("To");

        setTblTo(tblToModel);

        JButton btnSelect = new JButton("Select");
        btnSelect.setBounds(250, 320, 100, 30);
        add(btnSelect);
    }

    void setTblTo(DefaultTableModel tblToModel) {
        tblToModel.setRowCount(0);
        for (Contact contact : GlobalClass.addressBook.contacts) {
            if (!contact.user.equals(FrmLogin.username)) {
                for (String str: contact.behalfList){
                    if (!str.equals(contact.user)) {
                        tblToModel.addRow(new Object[]{false, str});

                    }
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, MessagingException {
        GlobalClass.connect();
        new FrmDashboard();
        new FrmAddressBook();
        new FrmSelectTo().setVisible(true);
    }
}
