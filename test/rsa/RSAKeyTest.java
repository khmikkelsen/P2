package rsa;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class RSAKeyTest
{
    // Tests length of hashing method.
    @Test
    void testHashLength() throws IOException
    {
        KeyPairGenerator keyPairGenerator = new KeyPairGenerator(2048);
        RSAKeyPair pair = keyPairGenerator.generateKeyPair();
        assertEquals(pair.getPublicKey().getBase64String().length(), 360);
    }
}