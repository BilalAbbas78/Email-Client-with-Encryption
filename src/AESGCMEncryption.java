import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESGCMEncryption {
    public static SecretKey key;
    static Cipher encryptionCipher;
//    static String iv = "349ED607B1BDF85B";

    public static String encrypt(byte[] messageInBytes) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        key = keyGen.generateKey();
//        byte[] messageInBytes = message.getBytes();
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
//        GCMParameterSpec spec = new GCMParameterSpec(128, iv.getBytes());
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public static byte[] decrypt(byte[] encryptedBytes, SecretKey key, byte[] iv) throws Exception {
//        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        return decryptionCipher.doFinal(encryptedBytes);
    }

//    public static void main(String[] args) {
//        try {
//            String message = "Hello World";
//            String encryptedMessage = encrypt(message);
//            String decryptedMessage = decrypt(encryptedMessage, key);
////            System.out.println("Message: " + message);
////            System.out.println("Encrypted Message: " + encryptedMessage);
//            System.out.println("Decrypted Message: " + decryptedMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}