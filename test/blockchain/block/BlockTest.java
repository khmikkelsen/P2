package blockchain.block;

import rsa.KeyPairGenerator;
import rsa.RSAKey;
import rsa.RSAKeyPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import blockchain.message.Message;

import java.io.IOException;
import java.math.BigInteger;
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
    private RSAKey senderKey1;
    private RSAKey receiverKey1;
    private RSAKey senderKey2;
    private RSAKey receiverKey2;

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

        this.senderKey1 = new RSAKey(new BigInteger("18681911242180496364036540852749608026086010177556995141127402546734951724982822366166663321626870408564979271418725095580710089245646171149084442268530923710802923841757102017320427032609819215067019272466767339629746716335576486375941273757421711351720927733537905949677731117551888160610969750595513026631818771925624665924783010106567841660325426679258988561706703577029549994780690704100564013904130095261102575531549233369743010448506007079246477208619860377453994877859050716416527615602770700648891053283938169558891575482297594359029433772801788509129744326861760756124769103494547343860555359369025176252651"), new BigInteger("65537"));
        this.receiverKey1 = new RSAKey(new BigInteger("26091512787228588811906883636338748327362013228114887585662811236829782045263498109801648963001115323905074418352924177449741062346376954707770669168519938636218301124167816833004568260724733607448997163791841926520968509326866087520017534858227471420929896593751613206649029532497017813906363979060658842828775767092484334687842028157012304193480409911054169089573471514731353367839137090746021669340197552880798065174493482570408644350989893851505754765383246814109119731647937151241635617932813943750488221409700184809632616349831377452338591620288070605591162521376309556428706016580075707166258943789863747010627"), new BigInteger("65537"));
        this.senderKey2 = new RSAKey(new BigInteger("27089068014800800653753200503280896497513248565198129438279566256538238844389363012152881458178378417198971803930281513948583120361836298971200085244375251411596847332743872248028593723368101135390601310260608044067397057114505387634841276547950109527395425955921057654271812521667956149742931508171940515515971100887944873146238392089317001524691317238386314169218528156087284518527023631585884135433517246540959915847436031301430861700752516188581513539223241958381557397818755739291543180949965218556130484277805905462926489585960766248464860561151284309267631060283704169582018716564585806158320807331814215063081"), new BigInteger("65537"));
        this.receiverKey2 = new RSAKey(new BigInteger("21575309834807845314928587913613599574644526206821973863398297381780785019704370030959458752485265019518002249264995483964882981930926550050784285705841175482908649570127479504158567108139679549024029350993357299269189397124838784669787836703256160093762692841604892899611881789934839872172075245029620097916672398219848364861913416506443292893713488467125168109681063191534334405583525504010475845894904875779401560010751777468560614303914772841904394503485871929874317077155829127638167076836641996417020250279063773367149353562815029549679516288892962055130124216322878809993240672742116177565570371696539138438151"), new BigInteger("65537"));
    }

    @Test
    void testMerkleroots() throws IOException
    {
        RSAKeyPair sender = new KeyPairGenerator(2048).generateKeyPair();
        RSAKeyPair receiver = new KeyPairGenerator(2048).generateKeyPair();

        Message m1 = new Message("Test message", sender.getPublicKey(), receiver.getPublicKey());
        m1.signMessage(sender.getPrivateKey());
        Message m2 = new Message("Test message", receiver.getPublicKey(), sender.getPublicKey());
        m2.signMessage(receiver.getPrivateKey());

        Block block = new Block("prevHeadhash", "1f00ffff", List.of(m1, m2));
        String merkle1 = block.getMerkleRootHash();

        m2 = new Message("New test message", receiver.getPublicKey(), sender.getPublicKey());
        m2.signMessage(receiver.getPrivateKey());
        block = new Block("prevHeadhash", "1f00ffff", List.of(m1, m2));
        String merkle2 = block.getMerkleRootHash();

        assertNotEquals(merkle1, merkle2);
    }

    @Test
    void getMerkleRootHashTest01() {

        Message m = new Message("Test Message", senderKey1, receiverKey1);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", receiverKey1, senderKey1);
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

        Message m = new Message("Test Message", senderKey1, receiverKey1);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", receiverKey1, senderKey1);
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

        Message m = new Message("Test Message", senderKey1, receiverKey1);
        m.signMessage(key1Private);
        Message m2 = new Message("Test", receiverKey1, senderKey1);
        m2.signMessage(key2Public);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));


        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals(64, blockTest.getMerkleRootHash().length());


    }
}