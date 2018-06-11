package rsa;

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
        KeyPairGenerator gen = new KeyPairGenerator(2048);
        assertEquals(new RSAOAEPEncrypt("Hello, world", new byte[]{1, 2}, gen.generateKeyPair().getPublicKey()).getEncryptedMessage().length, 256);
    }

    // Tests encryption and decryption.
    @Test
    void testEncryptionDecryption() throws IOException
    {
        String message = "Hello, world";
        KeyPairGenerator keyPair = new KeyPairGenerator(2048);
        RSAKeyPair key = keyPair.generateKeyPair();
        RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, key.getPublicKey());
        assertEquals(new String(new RSAOAEPDecrypt(encrypt.getEncryptedMessage(), key.getPrivateKey()).getDecryptedMessage()), message);
    }

    // Tests the length of decrypted byte array.
    @Test
    void testDecryptedLength() throws IOException
    {
        String message = "Hello, world";
        KeyPairGenerator keyPair = new KeyPairGenerator(2048);
        RSAKeyPair key = keyPair.generateKeyPair();
        RSAOAEPEncrypt encrypt = new RSAOAEPEncrypt(message, key.getPublicKey());
        assertEquals(new RSAOAEPDecrypt(encrypt.getEncryptedMessage(), key.getPrivateKey()).getDecryptedMessage().length, message.getBytes().length);
    }
}