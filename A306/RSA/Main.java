package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {

        KeyPairGenerator generator = new KeyPairGenerator(2048);
        RSAKey Alicepub = generator.getPublicKey();
        RSAKey Alicepriv = generator.getPrivateKey();

        String copypasta = "ffff";

        byte[] label = new byte[]{0x0,0x1,0x02};
        System.out.println(Alicepriv.toString());
        System.out.println(Alicepub.toString());

        try
        {
            RSAOAEPEncrypt mess = new RSAOAEPEncrypt(copypasta, Alicepub);
            RSAOAEPDecrypt demess = new RSAOAEPDecrypt(mess.getEncryptedMessage(), Alicepriv);
        }
        catch (IOException e){e.printStackTrace();}

        try
        {
            RSAOAEPSign sign = new RSAOAEPSign(copypasta,32,Alicepriv);
            byte[] signature = sign.getSignature();
            RSAOAEPVerify veri = new RSAOAEPVerify(signature, copypasta.getBytes(),32, Alicepub);
        }
        catch (IOException | BadVerificationException e) {e.printStackTrace();}

    }

}
