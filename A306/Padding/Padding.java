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
    private byte[] decryptedMessage;
    private byte[] DM;

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
        this.decryptedMessage = decryptRSA();
        this.DM = decodeOAEP();
    }

    public Padding(String message, byte[] l, KeyPair KeyPair) throws IOException
    {
        this.KeyPair = KeyPair;
        this.message = message;
        this.M = message.getBytes();
        this.k = KeyPair.getPublicKey().bitLength() / 8;

        this.L = l;
        this.lHash = sha256(L);
        this.PS = genPS();
        this.DB = genDB();
        this.EM = encodeOAEP();
        this.encryptedMessage = encryptRSA();
        this.decryptedMessage = decryptRSA();
        this.DM = decodeOAEP();

    }
    private byte[] decodeOAEP() throws IOException
    {
        byte[] Y = new byte[1];
        Y[0] = decryptedMessage[0];

        byte[] maskedSeed = new byte[lHash.length];
        System.arraycopy(decryptedMessage, 1, maskedSeed, 0, maskedSeed.length);
        byte[] maskedDB = new byte[k - lHash.length - 1];
        System.out.println("Length of maskedDB: " + maskedDB.length + "\nLength of decryptedMessage: " + decryptedMessage.length);
        System.arraycopy(decryptedMessage, maskedSeed.length + 1, maskedDB, 0, maskedDB.length - 1);

        byte[] seedMask = MGF(maskedDB, lHash.length);
        byte[] seed = xorByteArrays(maskedSeed, seedMask);

        formatByte(maskedSeed, "maskedSeed: ");
        formatByte(seedMask, "seedMask: ");

        formatByte(seed, "After XOR: ");

        byte[] dbMask = MGF(seed, k - lHash.length - 1);
        byte[] DB = xorByteArrays(maskedDB, dbMask);

        int j = lHash.length;

        System.out.println("DBLen: " + DB.length);
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

        int temp = DB.length - j;

        System.out.println("amount to copy to DB: " + DB.length + " - " + j + " = " + temp);
        formatByte(DB, "DBDecode: ");

        System.arraycopy(DB, j   , M, 0, DB.length - j);

        String mess = new String(M);
        System.out.println("And finally... :" + mess);

        return M;
    }

    private byte[] decryptRSA()
    {
        BigInteger c = OS2IP(encryptedMessage);

        if(c.compareTo(BigInteger.ZERO) <= 0 )
            throw new ArithmeticException();

        BigInteger m = c.modPow(KeyPair.getPrivateKey(), KeyPair.getPublicKey());


        formatByte(m.toByteArray(), "C after decryption: ");

        return I2OSP(m, k);
    }
    private byte[] encryptRSA()
    {
        formatByte(EM, "C before encryption: ");
        BigInteger m = OS2IP(EM);
        BigInteger c = m.modPow(KeyPair.getPublicE(), KeyPair.getPublicKey());
        System.out.println("C: " + c.intValue());
        byte[] C = I2OSP(c, k);
        System.out.println("C length: " + C.length);

        formatByte(C, "C After encryption: ");
        return C;
    }
    /* OAEP encode primitive

     */
    private byte[] encodeOAEP() throws IOException
    {
        byte[] seed = new byte[lHash.length];
        new Random().nextBytes(seed);

        byte[] dbMask = MGF(seed, k - lHash.length - 1);
        byte[] maskedDB = xorByteArrays(DB, dbMask);

        formatByte(dbMask, "dbMask:        ");
        formatByte(DB, "DB:            ");
        formatByte(maskedDB, "DB xor dbMask: ");

        byte[] seedMask = MGF(maskedDB, lHash.length);
        byte[] maskedSeed = xorByteArrays(seed, seedMask);

        byte[] EM = new byte[maskedSeed.length + maskedDB.length + 1];
        EM[0] = 0x0;

        System.arraycopy(maskedSeed, 0, EM, 1, maskedSeed.length);
        System.arraycopy(maskedDB, 0, EM, maskedSeed.length+1, maskedDB.length);

        formatByte(EM, "EM: ");
        return EM;
    }
    // Length of PS, amount of 0 bytes to write, may be 0
    private int genPS()
    {
        int ps = k - M.length - 2*lHash.length - 2;
        formatByte(lHash, "Hash(l) = lHash:     ");
        System.out.println("psLen: "+ps);
        System.out.println("kLen: " + k);
        return k - M.length - 2*lHash.length - 2;
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
        formatByte(DB.toByteArray(), "DB: ");

        return DB.toByteArray();
    }
    // Mask generation function, outputs a mask of a byte array with a determined length
    private byte[] MGF(byte[] seed, int maskLen) throws IOException
    {
        byte mask[] = new byte[maskLen];
        byte[] C = new byte[4];
        int counter = 0;

        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            while (counter < (maskLen / lHash.length))
            {

                C = I2OSP(BigInteger.valueOf(counter), 4);
                digest.update(seed);
                digest.update(C);

                System.arraycopy(digest.digest(), 0, mask, counter * lHash.length, lHash.length);

                counter++;
            }
            if ((counter * lHash.length) < maskLen)
            {
                C = I2OSP(BigInteger.valueOf(counter), 4);

                digest.update(seed);
                digest.update(C);

                System.arraycopy(digest.digest(), 0,
                        mask, counter * lHash.length, mask.length - (counter* lHash.length));

            }
        }
        catch ( NoSuchAlgorithmException e) {e.printStackTrace();}

     /*   ByteArrayOutputStream outputT = new ByteArrayOutputStream( );
        ByteArrayOutputStream outputC = new ByteArrayOutputStream( );

        int ceil = (maskLen / lHash.length);

        System.out.println("Ceil is: " + maskLen + " / " + lHash.length + " = " + ceil);

        for (int i = 0; i < ceil; i++)
        {
            outputC.write( seed );
            outputC.write( I2OSP(BigInteger.valueOf(i), 4) );

            byte[] temp = outputC.toByteArray();

            outputT.write( outputT.toByteArray() );
            outputT.write( temp );

            formatByte(temp, "Hash of outputC: ");

            outputC.reset();
        }

        byte[] maskTemp = outputT.toByteArray();
        byte[] mask = new byte[maskLen];

        System.arraycopy(maskTemp, 0, mask, 0, maskLen);
*/
        return mask;
    }

    // source: https://github.com/chetan51/ABBC/blob/master/src/main/java/RSAEngine/Crypter.java
    // I2OSP (Integer-To-Octet-String-Primitive) Converts an integer
    // to an octet string representative as per RSA PKCS#1
    public byte[] I2OSP (BigInteger x, int xLen)
    {
        BigInteger twofiftysix = new BigInteger("256");
        byte[] out = new byte[xLen];
        BigInteger[] cur;

        for(int i = 1; i <= xLen; i++)
        {
            cur = x.divideAndRemainder(twofiftysix.pow(xLen-i));
            out[i - 1] = cur[0].byteValue();
        }

        return out;
    }
    // source: https://github.com/chetan51/ABBC/blob/master/src/main/java/RSAEngine/Crypter.java
    // OS2IP (Octet-String-To-Integer-Primitive) Converts an octet string (byte array)
    // to an integer representative as per RSA PKCS#1
    public BigInteger OS2IP (byte[] octet)
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
                (byte) value};
    }
    private void formatByte (byte[] input, String byteName)
    {
        Formatter formatter1 = new Formatter();
        for (byte b : input) {
            formatter1.format("%02x", b);
        }
        String hex1 = formatter1.toString();
        System.out.println(byteName + ": " + hex1);
    }
    private byte[] xorByteArrays(byte[] arr1, byte[] arr2)
    {
        byte[] out;

        if (arr1.length > arr2.length)
            out = new byte[arr1.length];
        else
            out = new byte[arr2.length];

        for (int i = arr1.length - 1; i >= 0; i--)
        {
            out[i] = (byte) (arr1[i] ^ arr2[i]);
        }
        return out;
    }


}
