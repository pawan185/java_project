
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static SecretKey secretKey;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            secretKey = new SecretKeySpec("0123456789abcdef".getBytes(), "AES");

            System.out.println("Connected to server. Start chatting!");

            Thread readerThread = new Thread(() -> {
                try {
                    String received;
                    while ((received = in.readLine()) != null) {
                        System.out.println(decrypt(received));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            String message;
            while ((message = console.readLine()) != null) {
                out.println(encrypt(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
