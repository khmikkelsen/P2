package A306.blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to apply the sha256 algorithm onto a message.
 */
public class StringUtil {

    /**
     * The function applySha256: creates an instance of the sha256 message digest algorithm.
     * First) the message is converted to a sequence of bytes and a byte array is returned.
     * Second) the sha256 algorithm is used upon this array and a hash is returned.
     * @param input String to create hash
     * @return Hashed string in hex format.
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] inputBytes = input.getBytes();
            byte[] hash = digest.digest(inputBytes);

            return byteArrayToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The function byteArrayToHex takes a hash and converts i into hex.
     * The byte [] is formatted into hex and then concatenated together.
     * The format function takes as its arguments 02x and b, where 0 means add zeros as padding; 2 means number of
     * intended digits; x means the intended format is hexadecimal; the b is the byte to convert.
     * @param a byte[] to convert to hex String
     * @return A hex String
     */
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }


   /*
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
    */
}
