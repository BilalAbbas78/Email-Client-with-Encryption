import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class FrmSelectFrom extends JFrame {
    public FrmSelectFrom() {
        setTitle("Select From");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JTable tblFrom = new JTable();
        DefaultTableModel tblFromModel = new DefaultTableModel() {
            @Override
            public Class getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        tblFrom.setModel(tblFromModel);
        JScrollPane spFrom = new JScrollPane(tblFrom);
        spFrom.setBounds(10, 10, 570, 300);
        add(spFrom);

        tblFromModel.addColumn("Select");
        tblFromModel.addColumn("From");

        setTblFrom(tblFromModel);

    }

    void setTblFrom(DefaultTableModel tblFromModel) {
        tblFromModel.setRowCount(0);
        for (String behalf : FrmAddressBook.selfSelectedContact.behalfList) {
            if (!behalf.equals(FrmLogin.username)) {
                tblFromModel.addRow(new Object[]{false, behalf});
            }
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException, MessagingException {
        GlobalClass.connect();
        new FrmDashboard();
        new FrmAddressBook();
        new FrmSelectFrom().setVisible(true);
    }
}
