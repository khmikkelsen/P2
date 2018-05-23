package RSA;

import Communication.CommunicationSimulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class RSAOAEPEncryptTest
{
    // Tests length of encrypted message to be 64 characters long.
    @Test
    void testEncryptedLength() throws IOException
    {
        KeyPairGenerator key = new KeyPairGenerator(2048);
        assertEquals(new RSAOAEPEncrypt("Hello, world", new byte[]{1, 2}, key.getPublicKey()).getEncryptedMessage().length, 256);
    }

    // Tests encryption and decryption.
    @Test
    void testEncryptionDecryption() throws IOException
    {
        String message = "Hello, world";
        KeyPairGenerator keyPair = new KeyPairGenerator(2048);
        RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, keyPair.getPublicKey());
        assertEquals(CommunicationSimulator.toString(new RSAOAEPDecrypt(encrypt.getEncryptedMessage(), keyPair.getPrivateKey()).getDecrypt()), message);
    }

    // Tests the length of decrypted byte array.
    @Test
    void testDecryptedLength() throws IOException
    {
        String message = "Hello, world";
        KeyPairGenerator keyPair = new KeyPairGenerator(2048);
        RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, keyPair.getPublicKey());
        assertEquals(new RSAOAEPDecrypt(encrypt.getEncryptedMessage(), keyPair.getPrivateKey()).getDecrypt().length, 256);
    }
}