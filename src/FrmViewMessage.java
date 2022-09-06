import org.apache.commons.io.IOUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class FrmViewMessage extends JFrame {
    static JTextArea txtMessage;
    static JLabel lblFrom, lblTo, lblDate, lblSubject;
    public static JButton btnDownloadAttachments;
    static ArrayList<MimeBodyPart> parts;
    static ArrayList<JButton> buttons;

    public static class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
//            System.out.println(buttons.indexOf(event.getSource()));
            //noinspection SuspiciousMethodCalls
            saveAttachment(parts.get(buttons.indexOf(event.getSource())));

        }

        private void saveAttachment(MimeBodyPart part) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showSaveDialog(null);

            try {
                InputStream fileNme = part.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(fileNme, writer, "UTF-8");
                String theString = writer.toString();
                String[] words = theString.split("\\|");
                String RSADecrypted = RSAEncryption.decrypt(words[0], FrmDashboard.privateKey);
                byte[] decodedKey = Base64.getDecoder().decode(RSADecrypted);
                byte[] encryptedBytes = Base64.getDecoder().decode(words[1]);
                byte[] AESDecrypted = AESGCMEncryption.decrypt(encryptedBytes, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));

                File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + "\\" + part.getFileName());
                OutputStream out = new FileOutputStream(file);
                IOUtils.write(AESDecrypted, out);
                out.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saving attachment");
                ex.printStackTrace();
                return;
            }
            JOptionPane.showMessageDialog(null, "Attachment(s) saved successfully");
        }
    }




    FrmViewMessage(String from, String to, String date, String subject, String message, ArrayList<MimeBodyPart> parts) throws MessagingException {
        setSize(600, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("View Message");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        lblFrom = new JLabel("From:");
        lblFrom.setBounds(10, 10, 500, 30);
        add(lblFrom);

        lblTo = new JLabel("To:");
        lblTo.setBounds(10, 40, 500, 30);
        add(lblTo);

        lblDate = new JLabel("Date:");
        lblDate.setBounds(10, 70, 500, 30);
        add(lblDate);

        lblSubject = new JLabel("Subject:");
        lblSubject.setBounds(10, 100, 500, 30);
        add(lblSubject);

        JLabel lblMessage = new JLabel("Message:");
        lblMessage.setBounds(10, 130, 400, 30);
        add(lblMessage);

        txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        JScrollPane spTxtMessage = new JScrollPane(txtMessage);
        txtMessage.setEditable(false);
        spTxtMessage.setBounds(10, 160, 570, 340);
        add(spTxtMessage);


        lblFrom.setText("From: " + from);
        lblTo.setText("To: " + to);
        lblDate.setText("Date: " + date);
        lblSubject.setText("Subject: " + subject);
        txtMessage.setText(message);
        FrmViewMessage.parts = parts;


        final int numberOfButtons=parts.size();
        JPanel panel = new JPanel();
        buttons = new ArrayList<>();
        ButtonHandler handler= new ButtonHandler();
        for(int i = 0; i < numberOfButtons; i++){
            buttons.add(new JButton(parts.get(i).getFileName()));
            buttons.get(i).addActionListener(handler);
            panel.add(buttons.get(i));
        }
        JScrollPane scrollPane=new JScrollPane(panel);
        scrollPane.setBounds(10, 510, 570, 60);
        add(scrollPane);







        btnDownloadAttachments = new JButton("Download Attachments");
        btnDownloadAttachments.setBounds(380, 90, 200, 30);
        add(btnDownloadAttachments);

        if (parts.size() == 0) {
            btnDownloadAttachments.setVisible(false);
        }
        else {
            btnDownloadAttachments.setText("Download Attachments (" + parts.size() + ")");
        }

        btnDownloadAttachments.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showSaveDialog(null);

            for (MimeBodyPart part : FrmDashboard.selectedInbox.parts) {
                try {
                    InputStream fileNme = part.getInputStream();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(fileNme, writer, "UTF-8");
                    String theString = writer.toString();

                    String[] words = theString.split("\\|");
                    String RSADecrypted = RSAEncryption.decrypt(words[0], FrmDashboard.privateKey);
                    byte[] decodedKey = Base64.getDecoder().decode(RSADecrypted);
                    byte[] encryptedBytes = Base64.getDecoder().decode(words[1]);
                    byte[] AESDecrypted = AESGCMEncryption.decrypt(encryptedBytes, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));

                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + "\\" + part.getFileName());
                    OutputStream out = new FileOutputStream(file);
                    IOUtils.write(AESDecrypted, out);
                    out.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saving attachment");
                    ex.printStackTrace();
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Attachment(s) saved successfully");
        });
    }

    public void setMessage(String from, String date, String subject, String message, ArrayList<MimeBodyPart> parts) {
        lblFrom.setText("From: " + from);
        lblDate.setText("Date: " + date);
        lblSubject.setText("Subject: " + subject);
        txtMessage.setText(message);
        FrmViewMessage.parts = parts;
    }

    public static void main(String[] args) throws MessagingException {
        FrmViewMessage frmViewMessage = new FrmViewMessage("abc", "def", "ghi", "jhk", "lmn", new ArrayList<MimeBodyPart>());
        frmViewMessage.setVisible(true);
    }
}
