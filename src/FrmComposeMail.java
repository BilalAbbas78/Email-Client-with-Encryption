import javax.swing.*;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

class Attachment {
    String fileName, filePath;
    Attachment(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}

public class FrmComposeMail extends JFrame {
    private JPanel myPanel;
    private JTextField txtTo, txtFrom;
    private JLabel lblTo, lblFrom;
    private JLabel lblMessage;
    private JTextArea txtMessage;
    private JButton btnSend;
    private JButton btnExit;
    private JTextField txtSubject;
    private JLabel lblSubject;
    private JButton btnSelectAttachment;
    public static JTextArea txtRecipientsList;

    public static ArrayList<Attachment> attachments = new ArrayList<>();

    public static String from, to, subject, message;
    public static String filePath = "";
    public static String fileName = "";

    FrmComposeMail() {
        setTitle("Compose Mail");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
//        setContentPane(myPanel);
        setLayout(null);

        lblFrom = new JLabel("From");
        txtFrom = new JTextField();
        lblTo = new JLabel("To");
        txtTo = new JTextField();
        lblSubject = new JLabel("Subject");
        txtSubject = new JTextField();
        lblMessage = new JLabel("Message");
        txtMessage = new JTextArea();
        btnSend = new JButton("Send");
        btnExit = new JButton("Exit");
        btnSelectAttachment = new JButton("Select Attachment");

        lblFrom.setBounds(10, 10, 100, 30);
        add(lblFrom);

        txtFrom.setBounds(110, 10, 200, 30);
        add(txtFrom);

        lblTo.setBounds(10, 50, 100, 30);
        add(lblTo);

        JLabel lblRecipientsList = new JLabel("Recipients List");
        lblRecipientsList.setBounds(10, 90, 100, 30);
        add(lblRecipientsList);

        txtRecipientsList = new JTextArea();
        JScrollPane spRecipientsList = new JScrollPane(txtRecipientsList);
        spRecipientsList.setBounds(110, 90, 200, 30);
        spRecipientsList.setViewportView(txtRecipientsList);
        add(spRecipientsList);


        txtTo.setBounds(110, 50, 200, 30);
        add(txtTo);

        lblSubject.setBounds(10, 130, 100, 30);
        add(lblSubject);

        txtSubject.setBounds(110, 130, 200, 30);
        add(txtSubject);

        lblMessage.setBounds(10, 170, 100, 30);
        add(lblMessage);

        txtMessage.setBounds(110, 170, 200, 200);
        add(txtMessage);

        btnSelectAttachment.setBounds(110, 380, 200, 30);
        add(btnSelectAttachment);

        btnSend.setBounds(110, 420, 100, 30);
        add(btnSend);

        btnExit.setBounds(220, 420, 100, 30);
        add(btnExit);

        txtSubject.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtRecipientsList.setText("");
                txtRecipientsList.setText(GlobalClass.addressBook.getOthersUser(txtTo.getText()));
            }
        });

        btnSelectAttachment.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);

            if (fileChooser.getSelectedFile() != null) {
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                fileName = fileChooser.getSelectedFile().getName();

                attachments.add(new Attachment(fileName, filePath));
            }


//            File file = fileChooser.getSelectedFile();
//            filePath = file.getAbsolutePath();
//            fileName = file.getName();

            //                // Create the message part
//                BodyPart messageBodyPart = new MimeBodyPart();
//                // Now set the actual message
//                messageBodyPart.setText("");
//                // Create a multipart message
//            Multipart multipart = new MimeMultipart();
//                // Set text message part
//                multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
//                messageBodyPart = new MimeBodyPart();
//                byte[] bytes = Files.readAllBytes(Paths.get(filePath));
//                messageBodyPart.setDataHandler(new DataHandler(bytes, "application/octet-stream"));
//                messageBodyPart.setFileName(fileName);
//                multipart.addBodyPart(messageBodyPart);

//            Attachment attachment = new Attachment(multipart, filePath);
//            attachments.add(attachment);
//
//            System.out.println(multipart);
//            filepath = file.
//            txtMessage.append("\nAttachment: " + file.getName());
        });

        btnSend.addActionListener(e -> {
            from = txtFrom.getText();
            to = txtTo.getText();
            message = txtMessage.getText();
            subject = txtSubject.getText();

            if (from.equals("") || to.equals("") || message.equals("") || subject.equals("")) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
            } else {
                boolean isClientExists = false;
                try {
                    Statement statement = FrmDashboard.connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM ClientCertificates WHERE clientName = '" + to + "'");
                    isClientExists = resultSet.next();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
//                if (isClientExists) {
                    try {
                        new EmailSender().sendEmail();
                        attachments.clear();
                        FrmDashboard.setTblInbox(FrmDashboard.mdlInbox);
                        setVisible(false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Message can't send");
                        ex.printStackTrace();
                    }
                    txtFrom.setText("");
                    txtTo.setText("");
                    txtMessage.setText("");
                    txtSubject.setText("");
//                }
//                else {
//                    dispose();
//                    JOptionPane.showMessageDialog(null, "Please load Client Certificate first");
//                }
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
