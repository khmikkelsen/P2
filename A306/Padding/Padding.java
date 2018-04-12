package A306.Padding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;

import A306.rsa.KeyPair;

public class Padding
{
    private String message;
    private byte[] M;
    private byte[] EM;

    private byte[] L = {(byte) 0x0};
    private byte[] lHash;
    private int PS;
    private byte[] DB;

    private KeyPair KeyPair;
    private int k;

    private byte[] encryptedMessage;

    public Padding(String message, KeyPair KeyPair) throws IOException
    {
        this.KeyPair = KeyPair;
        this.message = message;
        this.M = message.getBytes();
        this.k = KeyPair.getPublicKey().bitLength() / 8;

        this.lHash = sha256(L);
        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = encryptRSA();
    }

    public Padding(String message, byte[] l, KeyPair KeyPair) throws IOException
    {
        this.KeyPair = KeyPair;
        this.message = message;
        this.M = message.getBytes();
        this.k = KeyPair.getPublicKey().bitLength();

        this.L = l;
        this.lHash = sha256(L);
        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = encryptRSA();

    }
    private byte[] encryptRSA()
    {
        BigInteger m = OS2IP(EM);
        BigInteger c = m.modPow(KeyPair.getPublicE(), KeyPair.getPublicKey());
        byte[] C = I2OSP(c, k);

        return C;
    }
    /* OAEP encode primitive

     */
    private byte[] encodeOAEP() throws IOException
    {
        byte[] seed = new byte[lHash.length];
        new Random().nextBytes(seed);

        byte[] dbMask = MGF(seed, k - lHash.length - 1);
        byte[] maskedDB = new byte[DB.length];

        for (int i = 0; i < DB.length; i++)
        {
            maskedDB[i] = (byte) (DB[i] ^ dbMask[i]);
        }


        Formatter formatter3 = new Formatter();
        for (byte b : maskedDB) {
            formatter3.format("%02x", b);
        }
        String hex3 = formatter3.toString();
        System.out.println("maskedDB: " + hex3);

        byte[] seedMask = MGF(maskedDB, lHash.length);
        byte[] maskedSeed = new byte[seed.length];

        for (int j = 0; j < seed.length; j++)
            maskedSeed[j] = (byte) (seed[j] ^ seedMask[j]);

        Formatter formatter = new Formatter();
        for (byte b : maskedSeed) {
            formatter.format("%02x", b);
        }

        String hex = formatter.toString();
        System.out.println("maskedSeed: " + hex);


        byte[] EM = new byte[maskedSeed.length + maskedDB.length + 1];
        EM[0] = 0x0;

        System.arraycopy(maskedSeed, 0, EM, 1, maskedSeed.length);
        System.arraycopy(maskedDB, 0, EM, maskedSeed.length+1, maskedDB.length);

        Formatter formatter2 = new Formatter();
        for (byte b : EM) {
            formatter2.format("%02x", b);
        }
        String hex2 = formatter2.toString();
        System.out.println("EM: " + hex2);

        Formatter formatter4 = new Formatter();
        for (byte b : KeyPair.getPublicKey().toByteArray()) {
            formatter4.format("%02x", b);
        }
        String hex4 = formatter4.toString();
        System.out.println("K: " + hex4);



        return EM;
    }
    // Length of PS, amount of 0 bytes to write, may be 0
    private int genPS()
    {
        return k - M.length - 2*lHash.length - 2;
    }
    // Generate DB(data block) consisting of DB = lHash || PS || 0x00 || M or
    // DB = lHash || 0x00 || M incase PS is empty
    private byte[] genDB() throws IOException
    {
        ByteArrayOutputStream DB = new ByteArrayOutputStream();

        DB.write( lHash );

        if (PS <= 0)
        {
            DB.write(0x0);
            DB.write( M );
        }
        else
        {
            for (int i = 0; i < PS; i++)
                DB.write( 0x00 );

            DB.write( 0x0);
            DB.write( M );
        }

        return DB.toByteArray();
    }
    // Mask generation function, outputs a mask of a byte array with a determined length
    private byte[] MGF(byte[] seed, int maskLen) throws IOException
    {
        ByteArrayOutputStream outputT = new ByteArrayOutputStream( );
        ByteArrayOutputStream outputC = new ByteArrayOutputStream( );

        int ceil = (maskLen / lHash.length);

        System.out.println("Ceil is: " + maskLen + " / " + lHash.length + " = " + ceil);

        for (int i = 0; i < ceil; i++)
        {
            outputC.write( seed );
            outputC.write ( I2OSP(BigInteger.valueOf(i), 4) );

            outputT.write( outputT.toByteArray() );
            outputT.write( sha256(outputC.toByteArray()) );

            outputC.reset();
        }

        byte[] maskTemp = outputT.toByteArray();
        byte[] mask = new byte[maskLen];

        System.arraycopy(maskTemp, 0, mask, 0, maskLen);

        return mask;
    }

    // source: https://github.com/chetan51/ABBC/blob/master/src/main/java/RSAEngine/Crypter.java
    // I2OSP (Integer-To-Octet-String-Primitive) Converts and integer(int)
    // to an octet string representative as per RSA PKCS#1
    private byte[] I2OSP (BigInteger x, int xLen)
    {
        BigInteger twofiftysix = new BigInteger("256");
        byte[] out = new byte[xLen];
        BigInteger[] cur;

        if(x.compareTo(twofiftysix.pow(xLen)) >= 0){
            throw new ArithmeticException("Integer too large");
        }
        for(int i = 1; i <= xLen; i++)
        {
            cur = x.divideAndRemainder(twofiftysix.pow(xLen-i));
            out[i - 1] = cur[0].byteValue();
        }

        return out;
    }
    // source: https://github.com/chetan51/ABBC/blob/master/src/main/java/RSAEngine/Crypter.java
    // OS2IP (Octet-String-To-Integer-Primitive) Converts an octet string (byte array)
    // to integer representative as per RSA PKCS#1
    private BigInteger OS2IP (byte[] octet)
    {
        BigInteger out = new BigInteger("0");
        BigInteger twofiftysix = new BigInteger("256");

        for(int i = 1; i <= octet.length; i++){
            out = out.add((BigInteger.valueOf(0xFF & octet[i - 1])).multiply(twofiftysix.pow(octet.length-i)));
        }

        return out;
    }
    // Returns input byte array as hash of said byte array
    private byte[] sha256(byte[] octet)
    {
        MessageDigest digest = null;

        try { digest = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        return digest.digest(octet);
    }
    // #OMITTED# Integer(int) to byte array conversion
    private byte[] intToByteArray(int value)
    {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }


}
