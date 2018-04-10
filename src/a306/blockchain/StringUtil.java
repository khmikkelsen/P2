package a306.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes());

            return byteArrayToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] concatenateByteArrays(byte[]... arrays) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[] array : arrays) {
            try {
                outputStream.write(array);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            byte[] result = outputStream.toByteArray();
            outputStream.close();

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
