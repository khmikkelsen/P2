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

    private BigInteger nPub;
    private BigInteger ePub;
    private int k;

    private byte[] encryptedMessage;

    public RSAOAEPEncrypt(String message, BigInteger publicN, BigInteger publicE) throws IOException
    {
        this.nPub = publicN;
        this.ePub = publicE;
        this.M = message.getBytes();
        this.k = nPub.bitLength() / 8;

        this.L = new byte[]{(byte) 0x0};
        if (L.length > pow(2, 61)-1)
            throw new ArithmeticException("Label L too long");

        this.lHash = sha256(L);

        if(k < lHash.length*2 + 2)
            throw new ArithmeticException("Decryption error; k less than lHash*2+2");

        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = RSAEncrypt();
    }
    public RSAOAEPEncrypt(String message, byte[] label, BigInteger nPub, BigInteger ePub) throws IOException
    {
        this.nPub = nPub;
        this.ePub = ePub;
        this.M = message.getBytes();
        this.k = nPub.bitLength() / 8;
        this.L = label;
        if (L.length > pow(2, 61)-1)
            throw new ArithmeticException("Label L too long");

        this.lHash = sha256(L);

        if(k < lHash.length*2 + 2)
            throw new ArithmeticException("Decryption error; k less than lHash*2+2");

        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = RSAEncrypt();
    }


    private byte[] RSAEncrypt()
    {
        BigInteger m = OS2IP(EM);
        BigInteger c = m.modPow(ePub, nPub);
        byte[] C = I2OSP(c, k);
        if (C.length != k)
            throw new ArithmeticException("Ciphertext length not equal to RSA Modulus length");

        return C;
    }
    /* OAEP encode primitive
     *
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
    private int genPS()
    {
        return k - M.length - 2*lHash.length - 2;
    }
    public byte[] getEncryptedMessage()
    {
        return this.encryptedMessage;
    }

}
