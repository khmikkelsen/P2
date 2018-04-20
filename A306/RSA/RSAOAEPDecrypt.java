package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Formatter;


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

    private byte[] decryptRSA()
    {
        BigInteger c = OS2IP(encryptedMessage);

        if(c.compareTo(BigInteger.ZERO) <= 0 )
            throw new ArithmeticException();

        BigInteger m = c.modPow(kPriv, nPub);

        return I2OSP(m, k);
    }
    private byte[] decodeOAEP() throws IOException
    {
        System.out.println("DECODE-OAEP");
        System.out.println("k: " + k);
        formatByteToStringW(decryptedMessage, "decryptedMessage");
        byte[] Y = new byte[1];
        Y[0] = decryptedMessage[0];

        byte[] maskedSeed = new byte[lHash.length];
        System.arraycopy(decryptedMessage, 1, maskedSeed, 0, maskedSeed.length);
        byte[] maskedDB = new byte[k - lHash.length - 1];
        System.arraycopy(decryptedMessage, maskedSeed.length + 1, maskedDB, 0, maskedDB.length);

        System.out.println("maskedSeedLen: " + maskedSeed.length);
        System.out.println("maskedDBLen:   " + maskedDB.length + "\n");

        byte[] seedMask = MGF(maskedDB, lHash.length, lHash.length);
        byte[] seed = xorByteArrays(maskedSeed, seedMask);

        formatByteToStringW(maskedSeed, "maskedSeed: ");
        formatByteToStringW(seedMask, "seedMask: ");
        formatByteToStringW(seed, "seed = seedMask xor maskedSeed: ");

        byte[] dbMask = MGF(seed, k - lHash.length - 1, lHash.length);
        byte[] DB = xorByteArrays(maskedDB, dbMask);

        int j = lHash.length;

        System.out.println("DBLen: " + DB.length);
        formatByteToStringW(DB, "DB after decode");
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
        System.arraycopy(DB, j , M, 0, DB.length - j);

        formatByteToStringW(M, "Final message: ");

        String mess = new String(M);
        System.out.println("And finally... :" + mess);
        System.out.println("\n\n");

        return M;
    }

    public String getDecryptedMessage() {
        return formatByteToString(DM);
    }
}
