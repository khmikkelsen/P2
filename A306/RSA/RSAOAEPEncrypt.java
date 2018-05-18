package RSA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import static java.lang.Math.pow;

public class RSAOAEPEncrypt extends RSAOAEP
{
    private byte[] M;
    private byte[] EM;

    private byte[] L;
    private byte[] lHash;
    private byte[] DB;
    private int PS;

    private RSAKey recipient;
    private int k;

    private byte[] encryptedMessage;

    public RSAOAEPEncrypt(String message, RSAKey recipient) throws IOException
    {
        this.recipient = recipient;
        this.M = message.getBytes();
        this.k = recipient.getModulus().bitLength() / 8;

        this.L = new byte[]{(byte) 0x0};
        if (L.length > pow(2, 61)-1)
            throw new IllegalArgumentException("Label L too long");

        this.lHash = sha256(L);

        if(k < lHash.length*2 + 2)
            throw new ArithmeticException("Decryption error; k less than lHash*2+2");
        if(M.length > k - lHash.length*2 -2 )
            throw new IllegalArgumentException("Message too long, at most k - lHash*2 - 2 bytes");

        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = RSAEncrypt();
    }
    public RSAOAEPEncrypt(String message, byte[] label, RSAKey recipient) throws IOException
    {
        this.recipient = recipient;
        this.M = message.getBytes();
        this.k = recipient.getModulus().bitLength() / 8;
        this.L = label;
        if (L.length > pow(2, 61)-1)
            throw new ArithmeticException("Label L too long");

        this.lHash = sha256(L);

        if(k < lHash.length*2 + 2)
            throw new ArithmeticException("Decryption error; k less than lHash*2+2");
        if(M.length > k - lHash.length*2 -2 )
            throw new IllegalArgumentException("Message too long, at most k - lHash*2 - 2 bytes");

        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = RSAEncrypt();
    }

    /* RSA Encryption primitive
     * Compute for byte string EM an integer representative m. Compute c = m^e (mod n), where e is the recipient's
     * public key and n is the recipients RSA modulus. Convert the integer c to be a k (RSA modulus length) byte string, C.
     * Output C.
     */
    private byte[] RSAEncrypt()
    {
        BigInteger m = OS2IP(EM);
        BigInteger c = m.modPow(recipient.getExponent(), recipient.getModulus());
        byte[] C = I2OSP(c, k);
        if (C.length != k)
            throw new ArithmeticException("Ciphertext length not equal to RSA Modulus length");

        return C;
    }
    /* OAEP encode primitive
     * Generates a random seed of lHash length. Maskes seed to be dbMask, a DB length (k - lHash - 1 bytes),
     * and performs XOR on DB and dbMask, result is maskedDB. Generates seedMask from maskedDB, a byte string og lHash length.
     * Performs XOR on original seed and seedMask to make maskedSeed. Outputs EM, a maskedSeed + maskedDB + 1 length
     * byte string, consisting of EM = 0x0 | maskedSeed | maskedDB.
     */
    private byte[] encodeOAEP() throws IOException
    {
        byte[] seed = new byte[lHash.length];
        new Random().nextBytes(seed);

        byte[] dbMask = MGF(seed, k - lHash.length - 1, lHash.length);
        byte[] maskedDB = xorByteArrays(DB, dbMask);

        byte[] seedMask = MGF(maskedDB, lHash.length, lHash.length);
        byte[] maskedSeed = xorByteArrays(seed, seedMask);

        byte[] EM = new byte[maskedSeed.length + maskedDB.length + 1];
        EM[0] = (byte) 0x0;

        System.arraycopy(maskedSeed, 0, EM, 1, maskedSeed.length);
        System.arraycopy(maskedDB, 0, EM, maskedSeed.length+1, maskedDB.length);

        return EM;
    }

    // Generate DB(data block) consisting of DB = lHash || PS || 0x01 || M or
    // DB = lHash || 0x01 || M incase PS is empty
    private byte[] genDB() throws IOException
    {
        ByteArrayOutputStream DB = new ByteArrayOutputStream();

        DB.write( lHash );

        if (PS <= 0)
        {
            DB.write(0x01);
            DB.write( M );
        }
        else
        {
            for (int i = 0; i < PS; i++)
                DB.write( 0x00 );

            DB.write( 0x01);
            DB.write( M );
        }

        return DB.toByteArray();
    }
    // Returns amount of 0 bytes to pad in DB, k - M.length - 2*hLen - 2 bytes
    private int genPS()
    {
        return k - M.length - 2*lHash.length - 2;
    }

    // Getter
    public byte[] getEncryptedMessage()
    {
        return this.encryptedMessage;
    }

}
