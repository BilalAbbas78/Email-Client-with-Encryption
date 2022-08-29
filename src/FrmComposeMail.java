import javax.mail.MessagingException;
import javax.swing.*;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class FrmComposeMail extends JFrame {
    private JPanel myPanel;
    private JTextField txtTo;
    private JLabel lblTo;
    private JLabel lblMessage;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JButton btnExit;
    private JTextField txtSubject;
    private JLabel lblSubject;
    private JButton btnSelectAttachment;

    public static String to, subject, message;
    public static String filePath = "";
    public static String fileName = "";

    FrmComposeMail() {
        setTitle("Compose Mail");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(myPanel);

        btnSelectAttachment.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
            File file = fileChooser.getSelectedFile();
            filePath = file.getAbsolutePath();
            fileName = file.getName();
//            filepath = file.
//            txtMessage.append("\nAttachment: " + file.getName());
        });

        btnSend.addActionListener(e -> {
            to = txtTo.getText();
            message = txtMessage.getText();
            subject = txtSubject.getText();

            if (to.equals("") || message.equals("") || subject.equals("")) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
            } else {
                try {
                    new EmailSender();
                    setVisible(false);
                } catch (MessagingException ex) {
                    JOptionPane.showMessageDialog(null, "Message can't send");
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                txtTo.setText("");
                txtMessage.setText("");
                txtSubject.setText("");
            }
//            EmailSender.sendMessage(FrmMain.username, to, message);
        });

        btnExit.addActionListener(e -> {
            setVisible(false);
        });
    }

    public static void main(String[] args) {
        new FrmComposeMail().setVisible(true);
    }
}
