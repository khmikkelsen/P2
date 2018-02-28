package A306.rsa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RSATest
{
    // Test the result of public key.
    @Test
    private void testPublicKey()
    {
        KeyPairClean key = new KeyPairClean(13, 7);
        assertEquals(key.getPublicKey(), 1);
    }
}
