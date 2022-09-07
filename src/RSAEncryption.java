import javax.crypto.Cipher;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncryption {

    public static String encrypt(String message, PublicKey publicKey) throws Exception {
        if (publicKey == null){
//            JOptionPane.showMessageDialog(null, "Receiver's certificate is not found");
            return null;
        }
        else {
            //Encrypt message
            Cipher encryptionCipher = Cipher.getInstance("RSA");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMessage =
                    encryptionCipher.doFinal(message.getBytes());
            //        System.out.println("encrypted message = " + encryption);
            return Base64.getEncoder().encodeToString(encryptedMessage);
        }
    }

    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        //Decrypt message
        Cipher decryptionCipher = Cipher.getInstance("RSA");
        decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage =
                decryptionCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        //        System.out.println("decrypted message = " + decryption);
        return new String(decryptedMessage);
    }

//    public static void main(String[] args) throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(1024, new SecureRandom());
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        String message = "Hello World";
//        String encryptedMessage = encrypt(message, publicKey);
//        String decryptedMessage = decrypt(encryptedMessage, privateKey);
//        System.out.println("Message = " + message);
//        System.out.println("Encrypted Message = " + encryptedMessage);
//        System.out.println("Decrypted Message = " + decryptedMessage);
//
//
//    }
}