import javax.swing.*;

public class FrmViewMessage extends JFrame {
    static JTextArea txtMessage;
    static JLabel lblFrom, lblDate, lblSubject;
    FrmViewMessage() {
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("View Message");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        lblFrom = new JLabel("From:");
        lblFrom.setBounds(10, 10, 500, 30);
        add(lblFrom);

        lblDate = new JLabel("Date:");
        lblDate.setBounds(10, 40, 500, 30);
        add(lblDate);

        lblSubject = new JLabel("Subject:");
        lblSubject.setBounds(10, 70, 500, 30);
        add(lblSubject);

        JLabel lblMessage = new JLabel("Message:");
        lblMessage.setBounds(10, 100, 500, 30);
        add(lblMessage);

        txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        JScrollPane spTxtMessage = new JScrollPane(txtMessage);
        txtMessage.setEditable(false);
        spTxtMessage.setBounds(10, 130, 570, 420);
        add(spTxtMessage);
    }

    public static void setMessage(String from, String date, String subject, String message) {
        lblFrom.setText("From: " + from);
        lblDate.setText("Date: " + date);
        lblSubject.setText("Subject: " + subject);
        txtMessage.setText(message);
    }

    public static void main(String[] args) {
        new FrmViewMessage().setVisible(true);
        setMessage("abc", "def", "ghi", "jhk");
    }
}
