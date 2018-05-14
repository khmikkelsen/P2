package RSA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

abstract class RSAOAEP
{
    /*
     * Mask-generation function
     * Returns a masked byte string of a desired length maskLen. hLen defines the length of output from hash function.
     */
    protected byte[] MGF(byte[] seed, int maskLen, int hLen) throws IOException
    {
        byte mask[] = new byte[maskLen];
        int ceil = ceil(maskLen, hLen);

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
    /*
     * Performs XOR operation on 2 byte arrays
     */
    protected byte[] xorByteArrays(byte[] arr1, byte[] arr2)
    {
        if (arr1.length != arr2.length)
            throw new ArrayIndexOutOfBoundsException("Incompatible byte arrays");

        byte[] out = new byte[arr1.length];

        for (int i = arr1.length - 1; i >= 0; i--)
            out[i] = (byte) (arr1[i] ^ arr2[i]);

        return out;
    }
    /*
     * Integer-To-Octet-String-Primitive
     * Method returns an octet string representative of an integer of a desired length xLen.
     */
    protected byte[] I2OSP (BigInteger x, int xLen)
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
    /*
     * Octet-String-To-Integer-Primitive
     * Method returns an integer representative of input byte array.
     */
    protected BigInteger OS2IP (byte[] octet)
    {
        BigInteger out = new BigInteger("0");
        BigInteger twofiftysix = new BigInteger("256");

        for(int i = 1; i <= octet.length; i++){
            out = out.add((BigInteger.valueOf(0xFF & octet[i - 1])).multiply(twofiftysix.pow(octet.length-i)));
        }

        return out;
    }
    // Method returns hash of input byte array
    protected byte[] sha256(byte[] octet)
    {
        MessageDigest digest;

        try { digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(octet);
        }
        catch (NoSuchAlgorithmException | NullPointerException e) { e.printStackTrace(); }

        return null;
    }
    /*
     * \ceil method, returns ceiling of two integers.
     */
    protected int ceil(int x, int y)
    {
        return (int) Math.ceil((double) x / (double) y);
    }
}
