package robin;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class BlockTest{


    @Test
    void calcMerkleHashTest01() {

        Message m = new Message("Test Message", "Robin Public Key", "Melanie Public Key");
        m.signMessage("RSA private key");
        Message m2 = new Message("Test", "Martin Public Key", "Kasper Public Key");
        m2.signMessage("RSA private key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block hola = new Block("prevHeadhash","1f00ffff", messages);

        assertEquals("f5fc51789712f3fcb992ee2b5d7f7819948b8ef3714e7f012233ad021d6bb042", hola.calculateMerkleRootHash(messages));

    }

    @Test
    void calcMerkleHashTest02() {

        Message m = new Message("Test Message", "Robin Public Key", "Melanie Public Key");
        m.signMessage("RSA private key");

        // "Test" changed to "Test2"
        Message m2 = new Message("Test2", "Martin Public Key", "Kasper Public Key");
        m2.signMessage("RSA private key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block hola = new Block("prevHeadhash","1f00ffff", messages);

        assertNotEquals("f5fc51789712f3fcb992ee2b5d7f7819948b8ef3714e7f012233ad021d6bb042", hola.calculateMerkleRootHash(messages));

    }

    @Test
    void calcMerkleHashTest03() {

        Message m = new Message("Test Message", "Robin Public Key", "Melanie Public Key");
        m.signMessage("RSA private key");
        Message m2 = new Message("Test", "Martin Public Key", "Kasper Public Key");

        //Signature test. Changed "private to falseprivate" //
        m2.signMessage("RSA falseprivate key");

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block hola = new Block("prevHeadhash","1f00ffff", messages);

        assertNotEquals("f5fc51789712f3fcb992ee2b5d7f7819948b8ef3714e7f012233ad021d6bb042", hola.calculateMerkleRootHash(messages));

    }

}