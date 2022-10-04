import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class FrmSelectFrom extends JFrame {
    public FrmSelectFrom() {
        setTitle("Select From");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JTable tblFrom = new JTable();
        //noinspection rawtypes
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

        JButton btnSelect = new JButton("Select");
        btnSelect.setBounds(250, 320, 100, 30);
        add(btnSelect);

        btnSelect.addActionListener(e -> {
            FrmComposeMail.txtFrom.setText("");
            for (int i = 0; i < tblFromModel.getRowCount(); i++) {
                if (tblFromModel.getValueAt(i, 0).equals(true)) {
                    FrmComposeMail.txtFrom.setText(FrmComposeMail.txtFrom.getText() + tblFromModel.getValueAt(i, 1) + "; ");
                }
            }
            setVisible(false);
        });

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
        new FrmSelectFrom().setVisible(true);
    }
}
