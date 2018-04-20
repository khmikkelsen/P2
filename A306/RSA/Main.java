package rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        Random rand = new Random();
        BigInteger p = BigInteger.probablePrime(2048, rand);
        BigInteger q = BigInteger.probablePrime(2048, rand);

        KeyPairGenerator Alice = new KeyPairGenerator(p, q);

        String copypasta = "Hej jeg hedder Kaj ";
        System.out.println("Message length: " + copypasta.length());

        byte[] label = new byte[]{0x0,0x1,0x02};

        try
        {
            RSAOAEPEncrypt mess = new RSAOAEPEncrypt(copypasta, label, Alice.getPublicKey(), Alice.getPublicE());
            RSAOAEPDecrypt demess = new RSAOAEPDecrypt(mess.getEncryptedMessage(), label, Alice.getPublicKey(), Alice.getPrivateKey());
        }
        catch (IOException e){e.printStackTrace();}


    }
}
