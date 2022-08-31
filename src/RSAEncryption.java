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
//    static String publicKeyString, privateKeyString;
//    static PublicKey publicKey;
//    static PrivateKey privateKey;
//
//
//    RSAEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        readFromFile("publicKey.txt");
//        readFromFile("privateKey.txt");
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString)));
//        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));
//    }
//
//    public static void generateKeys() throws NoSuchAlgorithmException {
//        KeyPairGenerator keyPairGenerator =
//                KeyPairGenerator.getInstance("RSA");
//        SecureRandom secureRandom = new SecureRandom();
//        keyPairGenerator.initialize(2048, secureRandom);
//        KeyPair pair = keyPairGenerator.generateKeyPair();
//        publicKey = pair.getPublic();
//        publicKeyString =
//                Base64.getEncoder().encodeToString(publicKey.getEncoded());
////        System.out.println("public key = " + publicKeyString);
//        privateKey = pair.getPrivate();
//        privateKeyString =
//                Base64.getEncoder().encodeToString(privateKey.getEncoded());
////        System.out.println("private key = " + privateKeyString);
//        writeToFile("publicKey.txt", publicKeyString);
//        writeToFile("privateKey.txt", privateKeyString);
////        System.out.println("Success");
//        readFromFile("publicKey.txt");
//        readFromFile("privateKey.txt");
//    }
//
//    private static void writeToFile(String fileName, String content) {
//        try {
//            FileWriter fileWriter = new FileWriter("C:/Users/Syed Bilal Abbas/Desktop/Keys/" + fileName);
//            fileWriter.write(content);
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void readFromFile(String fileName) {
//        try {
//            FileReader fileReader = new FileReader("C:/Users/Syed Bilal Abbas/Desktop/Keys/" + fileName);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line;
//            if ((line = bufferedReader.readLine()) != null) {
////                System.out.println(line);
//                if (fileName.equals("publicKey.txt")) {
//                    publicKeyString = line;
////                    System.out.println("public key = " + publicKeyString);
//                } else {
//                    privateKeyString = line;
////                    System.out.println("private key = " + privateKeyString);
//                }
//            }
//            fileReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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