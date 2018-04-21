package RSA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

public class RSAOAEPSign extends RSAOAEP
{
    private byte[] message;
    private byte[] M;

    private int hLen;
    private byte[] mHash;

    private byte[] salt;
    private int sLen;


    private int modBits;
    private byte[] EM;

    private BigInteger rsaMod;
    private BigInteger privateKey;

    private byte[] signature;

    public RSAOAEPSign(String message, BigInteger rsaMod, BigInteger privateKey) throws IOException
    {
        this.rsaMod = rsaMod;
        this.privateKey = privateKey;

        this.message = message.getBytes();
        this.mHash = sha256(this.message);
        this.hLen = mHash.length;

        this.sLen = 0;
        this.salt = genSalt();

        this.modBits = rsaMod.bitLength();
        this.M = genM();

        this.EM = encodeMessage(modBits-1);
        this.signature = RSASignature();
    }
    public RSAOAEPSign(String message, int sLength, BigInteger rsaMod, BigInteger privateKey) throws IOException
    {
        this.rsaMod = rsaMod;
        this.privateKey = privateKey;

        this.message = message.getBytes();
        this.mHash = sha256(this.message);
        this.hLen = mHash.length;

        this.sLen = sLength;
        this.salt = genSalt();

        this.modBits = rsaMod.bitLength();
        this.M = genM();

        this.EM = encodeMessage(modBits-1);
        this.signature = RSASignature();
    }
    private byte[] RSASignature()
    {
        formatByteToStringW(EM,"Before sign  ");
        int emLen = modBits/8;
        BigInteger m = OS2IP(EM);
        BigInteger s = m.modPow(privateKey, rsaMod);
        byte[] out = I2OSP(s, emLen);
        formatByteToStringW(out, "After sign   ");

        return out;
    }

    private byte[] encodeMessage(int emBits) throws IOException
    {
        byte[] DB;
        byte[] out;
        int emLen = ceil(emBits, 8);
        int PS = genPS(emLen);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (PS > 0)
        {
            for (int i = 0; i < PS; i++)
                stream.write( (byte) 0x0);

            stream.write( (byte) 0x01 );
            if (salt != null)
                stream.write( salt );
        }
        else
        {
            stream.write( (byte) 0x01 );
            if (salt != null)
                stream.write( salt );
        }
        DB = stream.toByteArray();
        stream.reset();

        byte[] hHash = sha256(M);

        byte[] dbMask = MGF(hHash, emLen - hLen - 1, hLen);
        System.out.println("dbMasklen: "+dbMask.length+"\nDBlen: "+ DB.length);

        formatByteToStringW(dbMask,"dbMask");
        formatByteToStringW(DB, "DB    ");

        byte[] maskedDB = xorByteArrays(DB, dbMask);
        formatByteToStringW(maskedDB,"masked");

        int temp = (8*emLen)-modBits;

        formatByteToStringW(maskedDB,"maskedDB before");
        BitSet maskedDBBitset = BitSet.valueOf(maskedDB);

        System.out.println("temp: "+temp);
        for (int i = 0; i < temp; i++)
        {
            maskedDBBitset.set(maskedDBBitset.length() - temp, false);
            temp--;
        }
        formatByteToStringW(maskedDBBitset.toByteArray(), "maskedDB after");

        stream.write( maskedDBBitset.toByteArray() );
        stream.write( hHash );
        stream.write( (byte)0xbc );

        out = stream.toByteArray();

        return out;
    }
    private byte[] genSalt()
    {
        byte[] out;

        if (sLen <= 0)
            out = null;
        else
        {
            out = new byte[sLen];
            new Random().nextBytes(out);
        }

        return out;
    }
    private byte[] genM() throws IOException
    {
        byte[] out = new byte[8+hLen+sLen];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int i = 0; i < 8; i++)
            stream.write( (byte)0x0 );

        stream.write( mHash );

        if (salt != null)
            stream.write( salt );

        return out;
    }

    private int genPS(int emLen)
    {

        int temp = emLen - sLen - hLen - 2;
        System.out.println("PS: "+ temp);
        return emLen - sLen - hLen - 2;
    }
    public byte[] getSignature() { return signature; }
    public void getSignaturew(byte[] input) { formatByteToString(input);}
}
