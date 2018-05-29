package RSA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

abstract class RSAOAEP
{
    /**
     * Mask generation function, MGF1, from RSA PKCS#1.
     * Takes an input byte array and masks of a desired length maskLen.
     * hLen defines the length of the output from desired hash function. In this implementation, SHA-256 is used.
     * @param seed Byte array to be masked.
     * @param maskLen Desired length of mask.
     * @param hLen Length of output from hash function. In this implementation SHA-256.
     * @return Returns the masked byte array of byte length maskLen.
     * @throws IOException IOException thrown by ByteArrayOutputStream in case of I/O error.
     */
    protected byte[] MGF(byte[] seed, int maskLen, int hLen) throws IOException
    {
        byte mask[] = new byte[maskLen];
        int ceil = ceil(maskLen, hLen);

        ByteArrayOutputStream outputT = new ByteArrayOutputStream( );
        ByteArrayOutputStream outputC = new ByteArrayOutputStream( );

        for (int i = 0; i < ceil; i++) {
            outputC.write( seed );
            outputC.write( I2OSP(BigInteger.valueOf(i), 4) );

            byte[] seedAndCounter = sha256(outputC.toByteArray());

            outputT.write( outputT.toByteArray() );
            outputT.write( seedAndCounter );
            outputC.reset();
        }

        byte[] maskTemp = outputT.toByteArray();
        System.arraycopy(maskTemp, 0, mask, 0, maskLen);

        return mask;
    }

    /**
     * XOR operation on two byte arrays. Throws exception if they are not of equal length.
     * @param arr1 First array to be XOR'd.
     * @param arr2 Second array to be XOR'd.
     * @return Returns the XOR'd byte array.
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

    /**
     * Integer-To-Octet-String-Primitive.
     * Method returns an octet string representative of an integer of a desired length xLen.
     * @param x BigInteger to convert to byte array.
     * @param xLen Desired length of byte array.
     * @return Returns byte array representative of input BigInteger of the desired length.
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

    /**
     * Converts a byte array to a BigInteger representative.
     * @param octet Byte array to convert.
     * @return BigInteger representative of input byte array.
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

    /**
     * Hash function to be used in this implementation. SHA-256. Output of this is of 32 byte length.
     * @param octet Byte array to hash.
     * @return Returns hashed byte array.
     */
    protected byte[] sha256(byte[] octet)
    {
        MessageDigest digest;

        try { digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(octet);
        }
        catch (NoSuchAlgorithmException | NullPointerException e) { e.printStackTrace(); }

        return null;
    }

    /**
     * For usage in RSAOAEPSign and RSAOAEPVerify. Padds the left most amountToPadd bits in the leftmost byte.
     * @param arrToPadd Byte array to padd.
     * @param amountToPadd Amount of bits to padd in leftmost byte.
     * @return Returns the padded byte array.
     */
    protected byte[] paddZeros(byte[] arrToPadd, int amountToPadd)
    {
        BitSet arrToPaddBitset = BitSet.valueOf(arrToPadd);
        for (int i = 7; i > 7 - amountToPadd; i--)
            arrToPaddBitset.set(i, false);

        return arrToPaddBitset.toByteArray();
    }

    /**
     * Ceiling method. Returns the ceiling of two integers.
     * @param x The dividend.
     * @param y The divisor.
     * @return Ceiling integer of x divided by y.
     */
    protected int ceil(int x, int y)
    {
        return (int) Math.ceil((double) x / (double) y);
    }
}
