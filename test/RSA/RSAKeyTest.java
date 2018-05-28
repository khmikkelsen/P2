package RSA;

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
        assertEquals(keyPairGenerator.getPublicKey().getBase64String().length(), 360);
    }
}