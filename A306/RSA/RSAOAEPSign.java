package rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
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

    private RSAKey sender;

    private byte[] signature;

    public RSAOAEPSign(String message, RSAKey sender) throws IOException
    {
        this.sender = sender;

        this.message = message.getBytes();
        this.mHash = sha256(this.message);
        this.hLen = mHash.length;

        this.sLen = 0;
        this.salt = genSalt();

        this.modBits = sender.getModulus().bitLength();
        this.M = genM();

        this.EM = encodeMessage(modBits-1);
        this.signature = RSASignature();
    }
    public RSAOAEPSign(String message, int sLength, RSAKey sender) throws IOException
    {
        this.sender = sender;

        this.message = message.getBytes();
        this.mHash = sha256(this.message); // Step 2
        this.hLen = mHash.length;

        this.sLen = sLength;
        this.salt = genSalt(); // Step 4

        this.modBits = sender.getModulus().bitLength();
        this.M = genM(); // Step 5

        this.EM = encodeMessage(modBits-1);
        this.signature = RSASignature();
    }
    private byte[] RSASignature()
    {
        int emLen = modBits/8;
        if (modBits % 8 != 0)
            throw new ArithmeticException("modBits % 8 != 0");

        BigInteger m = OS2IP(EM);
        BigInteger s = m.modPow(sender.getExponent(), sender.getModulus());
        if (s.compareTo(sender.getModulus()) >= 0)
            throw new ArithmeticException("Signature out of range");

        return I2OSP(s, emLen);
    }

    private byte[] encodeMessage(int emBits) throws IOException
    {
        ByteArrayOutputStream ConcatenateStream = new ByteArrayOutputStream();
        byte[] DB;
        int emLen = ceil(emBits, 8);
        int PS = genPS(emLen);

        byte[] hHash = sha256(M);

        DB = genDB(salt, PS);
        byte[] dbMask = MGF(hHash, emLen - hLen - 1, hLen);
        byte[] maskedDB = xorByteArrays(DB, dbMask);

        maskedDB = paddZeros(maskedDB, (8*emLen) - emBits);

        ConcatenateStream.write( maskedDB );
        ConcatenateStream.write( hHash );
        ConcatenateStream.write( (byte)0xbc );

        byte[] out = ConcatenateStream.toByteArray();

        if (out.length != modBits/8)
          throw new ArithmeticException("length != modBits/8");

        return out;
    }
    private byte[] genDB (byte[] salt, int PS) throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (PS > 0) {
            for (int i = 0; i < PS; i++)
                stream.write( (byte) 0x0);

            stream.write( (byte) 0x01 );
            if (salt != null)
                stream.write( salt );
        }
        else {
            stream.write( (byte) 0x01 );
            if (salt != null)
                stream.write( salt );
        }
        return stream.toByteArray();
    }
    private byte[] genSalt()
    {
        byte[] out;

        if (sLen <= 0)
            out = null;
        else {
            out = new byte[sLen];
            new Random().nextBytes(out);
        }
        return out;
    }
    private byte[] genM() throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int i = 0; i < 8; i++)
            stream.write( (byte)0x0 );

        stream.write( mHash );

        if (salt != null)
            stream.write( salt );

        return stream.toByteArray();
    }

    private int genPS(int emLen)
    {
        return emLen - sLen - hLen - 2;
    }
    public byte[] getSignature() { return signature; }
    public String getSignatureBase64String()
    {
        return new String(Base64.getEncoder().encode(signature));
    }
}
