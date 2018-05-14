package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        //BigInteger p = BigInteger.probablePrime(1024, rand);
        //BigInteger q = BigInteger.probablePrime(1024, rand);

        Random rand = new SecureRandom();
        KeyPairGenerator Alice = new KeyPairGenerator(2048, rand);

        String copypasta = "ffff";

        byte[] label = new byte[]{0x0,0x1,0x02};
        System.out.println(Alice.toString());

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

        }
        catch (IOException | BadVerificationException e) {e.printStackTrace();}

    }

}
