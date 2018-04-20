package rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class RSAOAEPSign extends RSAOAEP
{
    private byte[] message;
    private byte[] M;

    private int hLen;
    private byte[] mHash;

    private byte[] salt;
    private int sLen;
    private int PS;

    private int emBitLength;
    private int emLen;
    private byte[] EM;

    public RSAOAEPSign(String message) throws IOException
    {
        this.message = message.getBytes();
        this.mHash = sha256(this.message);
        this.hLen = mHash.length;

        this.sLen = 0;
        this.salt = genSalt();

        this.emBitLength = 8*hLen+8*sLen+9;
        this.emLen = emBitLength/8;
        this.EM = new byte[emBitLength/8];
        this.M = genM();

        this.PS = genPS();
        this.EM = encodeMessage();
    }
    public RSAOAEPSign(String message, int emBits) throws IOException
    {
        this.message = message.getBytes();
        this.mHash = sha256(this.message);
        this.hLen = mHash.length;

        this.sLen = 0;
        this.salt = genSalt();

        this.emBitLength = emBits;
        this.emLen = emBitLength/8;
        this.EM = new byte[emBitLength/8];
        this.M = genM();

        this.PS = genPS();
        this.EM = encodeMessage();
    }
    private byte[] encodeMessage() throws IOException
    {
        byte[] DB;
        byte[] out;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (PS > 0)
        {
            for (int i = 0; i < PS; i++)
                stream.write( (byte) 0x0);

            stream.write( (byte) 0x01 );
            stream.write( salt );
        }
        else
        {
            stream.write( (byte) 0x01 );
            stream.write( salt );
        }
        DB = stream.toByteArray();

        byte[] hHash = sha256(M);

        byte[] dbMask = MGF(hHash, emLen - hLen - 1, hLen);
        byte[] maskedDB = xorByteArrays(DB, dbMask);



        return maskedDB;
    }
    private byte[] genSalt()
    {
        byte[] out;

        if (sLen <= 0)
            out = new byte[]{0x0};
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
        stream.write( salt );

        out = stream.toByteArray();

        return out;
    }

    private int genPS()
    {
        return emLen - sLen - hLen - 2;
    }
}
