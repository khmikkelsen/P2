package RSA;

import java.io.IOException;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        Random rand = new Random();
        //BigInteger p = BigInteger.probablePrime(1024, rand);
        //BigInteger q = BigInteger.probablePrime(1024, rand);

        KeyPairGenerator Alice = new KeyPairGenerator(1024);

        String copypasta = "Hej jeg hedder Kaj.";

        byte[] label = new byte[]{0x0,0x1,0x02};

        try
        {
            RSAOAEPEncrypt mess = new RSAOAEPEncrypt(copypasta, label, Alice.getPublicKey(), Alice.getPublicE());
            RSAOAEPDecrypt demess = new RSAOAEPDecrypt(mess.getEncryptedMessage(), label, Alice.getPublicKey(), Alice.getPrivateKey());
        }
        catch (IOException e){e.printStackTrace();}

        try
        {
            RSAOAEPSign sign = new RSAOAEPSign(copypasta,32,Alice.getPublicKey(), Alice.getPrivateKey());
            byte[] signature = sign.getSignature();
            RSAOAEPVerify veri = new RSAOAEPVerify(signature, copypasta.getBytes(),32, Alice.getPublicKey(), Alice.getPublicE());

            System.out.println("Signature was: " +veri.getResult());
        }
        catch (IOException e) {e.printStackTrace();}


    }
}
