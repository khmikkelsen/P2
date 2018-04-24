package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.Random;
import RSA.RSAOAEP;

public class Main
{
    public static void main(String[] args)
    {
        Random rand = new Random();
        BigInteger p = BigInteger.probablePrime(1024, rand);
        BigInteger q = BigInteger.probablePrime(1024, rand);

        KeyPairGenerator Alice = new KeyPairGenerator(p, q);
        System.out.println("Alice has e: "+Alice.getPublicE()+"\nAnd n: "+Alice.getPublicKey()+"\nAnd d: "+Alice.getPrivateKey());

        String copypasta = "Hej jeg hedder Kaj hej";
        System.out.println("Message length: " + copypasta.length());

        byte[] label = new byte[]{0x0,0x1,0x02};
/*
        try
        {
            RSAOAEPEncrypt mess = new RSAOAEPEncrypt(copypasta, label, Alice.getPublicKey(), Alice.getPublicE());
            RSAOAEPDecrypt demess = new RSAOAEPDecrypt(mess.getEncryptedMessage(), label, Alice.getPublicKey(), Alice.getPrivateKey());
        }
        catch (IOException e){e.printStackTrace();}
        */
        try
        {
            RSAOAEPSign sign = new RSAOAEPSign(copypasta,32,Alice.getPublicKey(), Alice.getPrivateKey());
            byte[] signature = sign.getSignature();
            RSAOAEPVerify veri = new RSAOAEPVerify(signature, copypasta.getBytes(),32, Alice.getPublicKey(), Alice.getPublicE());

            sign.getSignaturew(signature);
        }
        catch (IOException e) {e.printStackTrace();}


    }
}
