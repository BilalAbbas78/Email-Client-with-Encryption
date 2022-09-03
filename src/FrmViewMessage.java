import org.apache.commons.io.IOUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Base64;

public class FrmViewMessage extends JFrame {
    static JTextArea txtMessage;
    static JLabel lblFrom, lblDate, lblSubject;
    public static JButton btnDownloadAttachments;
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
        lblMessage.setBounds(10, 100, 400, 30);
        add(lblMessage);

        txtMessage = new JTextArea();
        txtMessage.setLineWrap(true);
        JScrollPane spTxtMessage = new JScrollPane(txtMessage);
        txtMessage.setEditable(false);
        spTxtMessage.setBounds(10, 130, 570, 420);
        add(spTxtMessage);

        btnDownloadAttachments = new JButton("Download Attachments");
        btnDownloadAttachments.setBounds(380, 90, 200, 30);
        add(btnDownloadAttachments);

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

    public void setMessage(String from, String date, String subject, String message) {
        lblFrom.setText("From: " + from);
        lblDate.setText("Date: " + date);
        lblSubject.setText("Subject: " + subject);
        txtMessage.setText(message);
    }

    public static void main(String[] args) {
        new FrmViewMessage().setVisible(true);
//        setMessage("abc", "def", "ghi", "jhk");
    }
}
