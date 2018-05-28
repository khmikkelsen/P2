package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        KeyPairGenerator generator = null;
        RSAKey Alicepub = null;
        RSAKey Alicepriv = null;

        try{
            generator = new KeyPairGenerator(2048);
            RSAKeyPair pair = generator.generateKeyPair();
            Alicepub = pair.getPublicKey();
            Alicepriv = pair.getPrivateKey();
        }

        catch (IOException e){
            //Empty.
        }

        String copypasta = "Hello, wold!dijf";

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
