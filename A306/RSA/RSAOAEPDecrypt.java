package RSA;

import java.io.IOException;
import java.math.BigInteger;


public class RSAOAEPDecrypt extends RSAOAEP
{
    private byte[] L;
    private byte[] lHash;
    private byte[] decryptedMessage;
    private byte[] decodedMessage;

    private int k;

    private RSAKey recipient;

    public RSAOAEPDecrypt(byte[] encryptedMessage, RSAKey recipient) throws IOException
    {
        this.recipient = recipient;
        if (recipient.getModulus().bitLength() % 8 != 0)
            throw new IllegalArgumentException("Keys invalid");

        this.k = recipient.getModulus().bitLength() / 8;

        this.L = new byte[]{(byte) 0x0};
        this.lHash = sha256(L);

        this.decryptedMessage = decryptRSA(encryptedMessage);
        this.decodedMessage = decodeOAEP();
    }
    public RSAOAEPDecrypt(byte[] encryptedMessage, byte[] label, RSAKey recipient) throws IOException
    {
        this.recipient = recipient;
        if (recipient.getModulus().bitLength() % 8 != 0)
            throw new IllegalArgumentException("Keys invalid");
        this.k = recipient.getModulus().bitLength() / 8;

        this.L = label;
        this.lHash = sha256(L);

        this.decryptedMessage = decryptRSA(encryptedMessage);
        this.decodedMessage = decodeOAEP();
    }

    /**
     * RSA Decrypt primitive.
     * Computes an integer representative of the encrypted message, c. Computes m = c^k (mod n),
     * where k is the recipients private k, and n the corresponding RSA modulus.
     * Outputs an integer representative of k length (length of RSA modulus).
     * @param encryptedMessage Encrypted message to be decrypted.
     * @return Returns the decrypted byte array.
     */
    public byte[] decryptRSA(byte[] encryptedMessage)
    {
        if (encryptedMessage.length != k)
            throw new IllegalArgumentException("Encrypted message length != RSA modulus length");

        BigInteger c = OS2IP(encryptedMessage);
        BigInteger m = c.modPow(recipient.getExponent(), recipient.getModulus());

        return I2OSP(m, k);
    }

    /**
     * RSAOAEP decode primitive.
     * Extract the first byte from the encrypted message, if not 0, output invalid.
     * Extract the next lHash bytes to get maskedSeed, and the last k - lHash - 1, where k denotes length of RSA modulus
     * to get maskedDB. Let seedMask be MGF(maskedDB) and the seed to be maskedSeed XOR seedMask. Let dbMask MGF(seed),
     * a byte string of k - lHash - 1 length. Let DB be maskedDB XOR dbMask. Extract M from DB, and output M.
     * @return Returns decoded message M.
     * @throws IOException I/O error.
     */
    private byte[] decodeOAEP() throws IOException
    {
        if (decryptedMessage[0] != 0)
            throw new ArithmeticException("Leftmost byte of decrypted message != 0");

        byte[] maskedSeed = new byte[lHash.length];
        byte[] maskedDB = new byte[k - lHash.length - 1];

        System.arraycopy(decryptedMessage, 1, maskedSeed, 0, maskedSeed.length);
        System.arraycopy(decryptedMessage, maskedSeed.length + 1, maskedDB, 0, maskedDB.length);
        byte[] seedMask = MGF(maskedDB, lHash.length, lHash.length);
        byte[] seed = xorByteArrays(maskedSeed, seedMask);

        byte[] dbMask = MGF(seed, k - lHash.length - 1, lHash.length);
        byte[] DB = xorByteArrays(maskedDB, dbMask);

        int messageIndex = calcMessageIndex(DB);

        byte[] M = new byte[DB.length - messageIndex];
        System.arraycopy(DB, messageIndex , M, 0, DB.length - messageIndex);

        return M;
    }
    private int calcMessageIndex(byte[] DB)
    {
        boolean check = false;
        int j = lHash.length;

        while (!check) {
            byte temp = DB[j];
            if (temp == (byte) 0x01)
                check = true;

            j++;

            if (j == DB.length)
                throw new ArithmeticException("OAEP-DECODE error; no 0x01 to seperate PS || 0x01 || M");
        }
        return j;
    }
    public byte[] getDecryptedMessage() { return this.decodedMessage; }
