package a306.Padding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import A306.rsa.KeyPair;

public class Padding
{
    private String message;
    private byte[] M;

    private byte[] L;
    private byte[] lHash;
    private byte[] PS;
    private byte[] DB;

    private KeyPair pairKey;
    private int k;

    public Padding(String message, KeyPair pubKey) throws IOException
    {
        this.pairKey = pubKey;
        this.message = message;
        this.M = message.getBytes();

        this.L[0] = 0;
        this.lHash = sha256(L);
        this.PS = genPS();
        this.DB = genDB();

    }

    public Padding(String message, byte[] l) throws IOException
    {
        this.message = message;
        this.M = message.getBytes();

        this.L = l;
        this.lHash = sha256(L);
        this.PS = genPS();
        this.DB = genDB();

    }

    void padd()
    {
        byte[] seed = new byte[lHash.length];

    }

    private byte[] sha256(byte[] octet)
    {
        MessageDigest digest = null;

        try { digest = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        byte[] byteHash = digest.digest(octet);

        return byteHash;
    }

    private byte[] genPS()
    {
        int mLen = M.length;

        int psLen = k - mLen - 2*lHash.length - 2;

        if (psLen < 0)
            psLen = 0;

        for (int i = 0; i <= psLen; i++)
            PS[i] = 0;

        return PS;
    }

    private byte[] genDB() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        outputStream.write( lHash );
        outputStream.write( PS );
        outputStream.write(0x01 );
        outputStream.write( M );

        return outputStream.toByteArray( );
    }

    private byte[] MGF(byte[] seed, int maskLen)
    {
        byte[] mask = new byte[maskLen];
        byte[] T = {(byte) 0x0};

        int ceil = (maskLen / lHash.length) - 1;

        for (int i = 0; i < ceil; i++)
        {

        }

        return mask;
    }
    /*
    private byte[] I20SP (int x, int xLen)
    {

    }
*/

}
