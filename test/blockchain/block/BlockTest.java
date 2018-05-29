package blockchain.block;

import rsa.KeyPairGenerator;
import rsa.RSAKey;
import rsa.RSAKeyPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import blockchain.message.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BlockTest {
    private RSAKey key1Public;
    private RSAKey key1Private;
    private RSAKey key2Public;
    private RSAKey key2Private;

    private RSAKey key1FalsePrivate;

    @BeforeEach
    void generateKeys() throws IOException {
        KeyPairGenerator gen = new KeyPairGenerator(2048);

        RSAKeyPair pair1 = gen.generateKeyPair();
        key1Public = pair1.getPublicKey();
        key1Private = pair1.getPrivateKey();


        RSAKeyPair pair2 = gen.generateKeyPair();
        key2Public = pair2.getPublicKey();
        key2Private = pair2.getPrivateKey();


        RSAKeyPair pair1False = gen.generateKeyPair();
        key1FalsePrivate = pair1False.getPrivateKey();
    }

    @Test
    void getMerkleRootHashTest01() {

        Message m = new Message("Test Message", key1Public, key2Public);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", key2Public, key1Public);
        m2.signMessage(key2Private);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals("3720e16ad9712e01705e6bfb72d31cbc3fa2999e32384e54b0cb57e1822c93dd", blockTest.getMerkleRootHash());

    }

    @Test
    void getMerkleRootHashTest02() {

        Message m = new Message("Test Message", key1Public, key2Public);
        m.signMessage(key1Private);

        // "Test" changed to "Test2"
        Message m2 = new Message("Test2", key2Public, key1Public);
        m2.signMessage(key2Private);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertNotEquals("f5fc51789712f3fcb992ee2b5d7f7819948b8ef3714e7f012233ad021d6bb042", blockTest.getMerkleRootHash());

    }

    @Test
    void getMerkleRootHashTest03() {

        Message m = new Message("Test Message", key1Public, key2Public);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", key2Public, key1Public);
        m2.signMessage(key2Public);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        String merkleRootHash01 = blockTest.getMerkleRootHash();

        //Signature test. Changed a private key to a "False Private" key //
        m2.signMessage(key1FalsePrivate);

        String merkleRootHash02 = blockTest.getMerkleRootHash();
        assertNotEquals(merkleRootHash01, merkleRootHash02);
    }

    @Test
    void getMerkleRootHashTest04() {

        //Test "getMerkleRootHash" is 64.//

        Message m = new Message("Test Message", key1Public, key2Public);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", key2Public, key1Public);
        m2.signMessage(key2Public);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));


        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals(64, blockTest.getMerkleRootHash().length());


    }
}