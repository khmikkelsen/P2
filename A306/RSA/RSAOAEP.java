package rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class RSAOAEP
{
    public byte[] MGF(byte[] seed, int maskLen, int hLen) throws IOException
    {
        byte mask[] = new byte[maskLen];
        int ceil = (maskLen / hLen);

        ByteArrayOutputStream outputT = new ByteArrayOutputStream( );
        ByteArrayOutputStream outputC = new ByteArrayOutputStream( );

        for (int i = 0; i < ceil; i++)
        {
            outputC.write( seed );
            outputC.write( I2OSP(BigInteger.valueOf(i), 4) );

            byte[] temp = sha256(outputC.toByteArray());

            outputT.write( outputT.toByteArray() );
            outputT.write( temp );

            outputC.reset();
        }

        byte[] maskTemp = outputT.toByteArray();
        System.arraycopy(maskTemp, 0, mask, 0, maskLen);

        return mask;
    }
    public byte[] xorByteArrays(byte[] arr1, byte[] arr2)
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
    public byte[] sha256(byte[] octet)
    {
        MessageDigest digest = null;

        try { digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(octet);
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        return digest.digest(octet);
    }
    public String formatByteToString (byte[] input)
    {
        Formatter formatter1 = new Formatter();
        for (byte b : input)
            formatter1.format("%02x", b);

        String hex1 = formatter1.toString();

        return hex1;
    }
    public void formatByteToStringW (byte[] input, String byteName)
    {
        Formatter formatter1 = new Formatter();
        for (byte b : input)
            formatter1.format("%02x", b);

        String hex1 = formatter1.toString();

        System.out.println(byteName+ ": "+hex1);
    }
}
