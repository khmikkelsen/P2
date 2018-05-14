package RSA;

import java.io.IOException;
import java.math.BigInteger;


public class RSAOAEPDecrypt extends RSAOAEP
{
    private byte[] L;
    private byte[] lHash;

    private BigInteger nPub;
    private BigInteger kPriv;
    private int k;

    private byte[] encryptedMessage;
    private byte[] decryptedMessage;
    private byte[] DM;

    public RSAOAEPDecrypt(byte[] encryptedMessage, BigInteger publicN, BigInteger publicK) throws IOException
    {
        this.nPub = publicN;
        this.kPriv = publicK;
        if (publicK.bitCount() % 8 != 0 || publicN.bitCount() % 8 != 0)
            throw new IllegalArgumentException("Keys invalid");

        this.k = nPub.bitLength() / 8;

        this.L = new byte[]{(byte) 0x0};
        this.lHash = sha256(L);

        this.encryptedMessage = encryptedMessage;
        this.decryptedMessage = decryptRSA();
        this.DM = decodeOAEP();
    }
    public RSAOAEPDecrypt(byte[] encryptedMessage, byte[] label, BigInteger publicN, BigInteger publicK) throws IOException
    {
        this.nPub = publicN;
        this.kPriv = publicK;
        this.k = nPub.bitLength() / 8;

        this.L = label;
        this.lHash = sha256(L);

        this.encryptedMessage = encryptedMessage;
        this.decryptedMessage = decryptRSA();
        this.DM = decodeOAEP();
    }
    /*
     * RSA Decrypt primitive
     * Computes an integer representative of the encrypted message, c. Computes m = c^k (mod n),
     * where k is the recipients private k, and n the corresponding RSA modulus.
     * Outputs an integer representative of k length (length of RSA modulus).
     */
    private byte[] decryptRSA()
    {
        if (encryptedMessage.length != k)
            throw new IllegalArgumentException("Encrypted message length != RSA modulus length");

        BigInteger c = OS2IP(encryptedMessage);

        if(c.compareTo(BigInteger.ZERO) <= 0 )
            throw new ArithmeticException();

        BigInteger m = c.modPow(kPriv, nPub);

        return I2OSP(m, k);
    }
    /*
     * RSAOAEP decode primitive
     * Extract the first byte from the encrypted message, if not 0, output invalid.
     * Extract the next lHash bytes to get maskedSeed, and the last k - lHash - 1, where k denotes length of RSA modulus
     * to get maskedDB. Let seedMask be MGF(maskedDB) and the seed to be maskedSeed XOR seedMask. Let dbMask MGF(seed),
     * a byte string of k - lHash - 1 length. Let DB be maskedDB XOR dbMask. Extract M from DB, and output M.
     */
    private byte[] decodeOAEP() throws IOException
    {
        if (decryptedMessage[0] != 0)
            throw new ArithmeticException("Leftmost byte of decrypted message != 0");

        byte[] maskedSeed = new byte[lHash.length];
        System.arraycopy(decryptedMessage, 1, maskedSeed, 0, maskedSeed.length);
        byte[] maskedDB = new byte[k - lHash.length - 1];
        System.arraycopy(decryptedMessage, maskedSeed.length + 1, maskedDB, 0, maskedDB.length);

        byte[] seedMask = MGF(maskedDB, lHash.length, lHash.length);
        byte[] seed = xorByteArrays(maskedSeed, seedMask);

        byte[] dbMask = MGF(seed, k - lHash.length - 1, lHash.length);
        byte[] DB = xorByteArrays(maskedDB, dbMask);

        int j = lHash.length;

        boolean check = false;

        while (!check)
        {
            byte temp = DB[j];
            if (temp == (byte) 0x01)
                check = true;

            j++;

            if (j == DB.length)
                throw new ArithmeticException("OAEP-DECODE error; no 0x01 to seperate PS || 0x01 || M");
        }

        byte[] M = new byte[DB.length - j];

        System.arraycopy(DB, j , M, 0, DB.length - j);

        String mess = new String(M);
        System.out.println("And finally... :" + mess);
        System.out.println("\n");

        return M;
    }

    public byte[] getDecryptedMessage() { return this.DM; }
}
