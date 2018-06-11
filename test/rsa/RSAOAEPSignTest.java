package rsa;

import blockchain.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RSAOAEPSignTest
{
    // Tests length of signature to be 2048 / 8.
    @Test
    void signatureTest01() throws IOException
    {
        RSAKeyPair sender = new KeyPairGenerator(2048).generateKeyPair();
        RSAKeyPair receiver = new KeyPairGenerator(2048).generateKeyPair();
        Message m = new Message("Test", sender.getPublicKey(), receiver.getPublicKey());
        m.signMessage(sender.getPrivateKey());
        Assertions.assertEquals(m.getSignature().getBytes().length, 2048/8);
    }

    // Tests result of signed message.
    @Test
    void testSignedResult() throws IOException
    {
        RSAKeyPair sender = new KeyPairGenerator(2048).generateKeyPair();
        RSAKeyPair receiver = new KeyPairGenerator(2048).generateKeyPair();
        Message m = new Message("Test", sender.getPublicKey(), receiver.getPublicKey());
        m.signMessage(sender.getPrivateKey());

        // Verifying message.
        try
        {
            new RSAOAEPVerify(m.getSignature().getBytes(), m.getMessage().getBytes(), sender.getPublicKey());
        }

        catch (BadVerificationException e)
        {
            Assertions.fail("Message not verified.");
        }
    }
}
