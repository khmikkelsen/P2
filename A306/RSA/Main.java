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

        KeyPairGenerator Alice = new KeyPairGenerator(2048);

        String copypasta = "ffff";

        byte[] label = new byte[]{0x0,0x1,0x02};
        System.out.println(Alice.toString());

        try
        {
            RSAOAEPEncrypt mess = new RSAOAEPEncrypt(copypasta, Alice.getRsaMod(), Alice.getPublicE());
            RSAOAEPDecrypt demess = new RSAOAEPDecrypt(mess.getEncryptedMessage(), Alice.getRsaMod(), Alice.getPrivateKey());
        }
        catch (IOException e){e.printStackTrace();}

        try
        {
            RSAOAEPSign sign = new RSAOAEPSign(copypasta,32,Alice.getRsaMod(), Alice.getPrivateKey());
            byte[] signature = sign.getSignature();
            RSAOAEPVerify veri = new RSAOAEPVerify(signature, copypasta.getBytes(),32, Alice.getRsaMod(), Alice.getPublicE());

        }
        catch (IOException | BadVerificationException e) {e.printStackTrace();}

    }

}
