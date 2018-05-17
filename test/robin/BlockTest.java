package robin;

import RSA.InvalidRSAKeyException;
import RSA.RSAKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.INVALID_ACTIVITY;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class BlockTest {

    private final String validBase64RSAKey = "MIIBCgKCAQEA61BjmfXGEvWmegnBGSuS+rU9soUg2FnODva32D1AqhwdziwHINFaD1MVlcrYG6XRKfkcxnaXGfFDWHLEvNBSEVCgJjtHAGZIm5GL/KA86KDp/CwDFMSwluowcXwDwoyinmeOY9eKyh6aY72xJh7noLBBq1N0bWi1e2i+83txOCg4yV2oVXhBo8pYEJ8LT3el6Smxol3C1oFMVdwPgc0vTl25XucMcG/ALE/KNY6pqC2AQ6R2ERlVgPiUWOPatVkt7+Bs3h5Ramxh7XjBOXeulmCpGSynXNcpZ/06+vofGi/2MlpQZNhHAo8eayMp6FcvNucIpUndo1X8dKMv3Y26ZQIDAQAB";
    private RSAKey validRSAKey;

    @BeforeEach
    void generateRSAKey() throws IOException, InvalidRSAKeyException {
        validRSAKey = new RSAKey(validBase64RSAKey);
    }

    @Test
    void getMerkleRootHashTest01() throws IOException, InvalidRSAKeyException {

        Message m = new Message("Test Message", validRSAKey, validRSAKey);
        m.signMessage("RSA private key");
        Message m2 = new Message("Test", validRSAKey, validRSAKey);
        m2.signMessage("RSA private key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals("f705d73203d26809942140c76e14412ec03042add36534e68c980ba4c05c44f4", blockTest.getMerkleRootHash());

    }

    @Test
    void getMerkleRootHashTest02() throws IOException, InvalidRSAKeyException {

        Message m = new Message("Test Message", validRSAKey, validRSAKey);
        m.signMessage("RSA private key");
        // "Test" changed to "Test2"
        Message m2 = new Message("Test2", validRSAKey, validRSAKey);
        m2.signMessage("RSA private key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertNotEquals("50866b3a574df909a36594876d2281df9a188352f67ff3976bbf3a52ceb87a02", blockTest.getMerkleRootHash());
    }

    @Test
    void getMerkleRootHashTest03() throws IOException, InvalidRSAKeyException {
        Message m = new Message("Test Message", validRSAKey, validRSAKey);
        m.signMessage("RSA private key");
        Message m2 = new Message("Test", validRSAKey, validRSAKey);
        //Signature test. Changed "private to falseprivate" //
        m2.signMessage("RSA falseprivate key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertNotEquals("50866b3a574df909a36594876d2281df9a188352f67ff3976bbf3a52ceb87a02", blockTest.getMerkleRootHash());
    }

    @Test
    void mineBlockTest01() {

        //TODO: Spørg Mads
    }
}