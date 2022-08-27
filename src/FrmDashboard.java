import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

public class FrmDashboard extends JFrame {

    public static JTabbedPane jtp;

    FrmDashboard() {
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

        tabbedPane.addTab("Inbox", null, inboxPanel(), "Inbox");
        tabbedPane.addTab("Sent", null, new JPanel(), "Sent");
//        tabbedPane.addTab("Long text", UIManager.getIcon("OptionPane.warningIcon"), pnl2, "Warning tool tip");
//        tabbedPane.addTab("This is a really long text", UIManager.getIcon("OptionPane.errorIcon"), pnl3, "Error tool tip");

        tabbedPane.setBounds(0, 0, 1500, 820);
        add(tabbedPane);


    }

    JPanel inboxPanel() {
        JPanel pnlInbox = new JPanel();
        pnlInbox.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1500, 350);
        DefaultTableModel model = new DefaultTableModel(
                new String [] {
                "Subject", "From", "Date",
        }, 0);

        JTable tblInbox = new JTable();
        scrollPane.setViewportView(tblInbox);
        tblInbox.setModel(model);
        pnlInbox.add(scrollPane);

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setBounds(10, 350, 100, 30);
        pnlInbox.add(lblFrom);

        JLabel lblDate = new JLabel("Date:");
        lblDate.setBounds(10, 380, 100, 30);
        pnlInbox.add(lblDate);

        JLabel lblSubject = new JLabel("Subject:");
        lblSubject.setBounds(10, 410, 100, 30);
        pnlInbox.add(lblSubject);

        JTextArea txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        txtMessage.setEditable(false);
        JScrollPane spTxtMessage = new JScrollPane(txtMessage);
        spTxtMessage.setBounds(0, 450, 1300, 370);
        pnlInbox.add(spTxtMessage);




//        JLabel lblInbox = new JLabel("Inbox");
//        lblInbox.setBounds(200, 30, 200, 30);
//        pnlInbox.add(lblInbox);
        return pnlInbox;
    }

    public static void main(String[] args) {
        new FrmDashboard().setVisible(true);
    }
}