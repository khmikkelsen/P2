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
    private RSAKey senderKey;
    private RSAKey senderKeyPrivate;
    private RSAKey receiverKey;
    private RSAKey receiverKeyPrivate;

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

        this.senderKey = new RSAKey(new BigInteger("18681911242180496364036540852749608026086010177556995141127402546734951724982822366166663321626870408564979271418725095580710089245646171149084442268530923710802923841757102017320427032609819215067019272466767339629746716335576486375941273757421711351720927733537905949677731117551888160610969750595513026631818771925624665924783010106567841660325426679258988561706703577029549994780690704100564013904130095261102575531549233369743010448506007079246477208619860377453994877859050716416527615602770700648891053283938169558891575482297594359029433772801788509129744326861760756124769103494547343860555359369025176252651"), new BigInteger("65537"));
        this.senderKeyPrivate = new RSAKey(new BigInteger("17152449407067743341493071522442055199408496038278532399497145179905510864445183046072459886550748406851430649916144486970469710932228994069682422668748882071870209006958304723291656155150760217139539987876728474334150104908701272689487110522826955918523389563114547473062017843706440182742590247252463959880474501945012573208195114881061055472755347073140231858982217755557863225833553053912742272678604664723495123806021350211070653079179418738932789592822800943792526474035429546694062795872590483792570932488917410662439420985671713864804232240009727035268916290219739321856087975341453590817508086757236483952783"), new BigInteger("3570275395372302985992305646785682835691761885746625566134249446445229816093641218288866770562919181409948444555458519904903528719379920084861570850599476246752482661220710650209547697338741405650381231130817576065875896914903770563096242095261362576293342977634421767151006552251726562901613513860788365483710182392791720266379821931925973702017465493955870336287042566508328533165472000749571350636020444830855880082514156221335236911292262593766913587477664704995310691165235331478798650893546557411748819979480610528656439928589069931120531632815333305689995273356456367471877061525336396726338911411309616216193"));
        this.receiverKey = new RSAKey(new BigInteger("26091512787228588811906883636338748327362013228114887585662811236829782045263498109801648963001115323905074418352924177449741062346376954707770669168519938636218301124167816833004568260724733607448997163791841926520968509326866087520017534858227471420929896593751613206649029532497017813906363979060658842828775767092484334687842028157012304193480409911054169089573471514731353367839137090746021669340197552880798065174493482570408644350989893851505754765383246814109119731647937151241635617932813943750488221409700184809632616349831377452338591620288070605591162521376309556428706016580075707166258943789863747010627"), new BigInteger("65537"));
        this.receiverKeyPrivate = new RSAKey(new BigInteger("16728210948752115712998054985881895133659259257711756402907573270357380059124855561533322243484020250400109909350542713646643741013842529435543369627349538354421402628881186817326606252858953379595166907522534670198317079050555433192923578857867977242701415727940766842248322708153158934351077112360230623074905072631563410767150732975542842268529948568312403558125039807692111057453673737465303646651243342068002909634716145088950505750917584896917499702583527991218041765600611379109872925657618188324463366352103966597612002830727185633647712496160075486842491221983156695048807205000944564322688252788257572295307"), new BigInteger("7242544235551931081655603875626071279887920591542826678888275901548064154411068772307662114496198065138439639026305357711929029851797837122065784846593977082221189387570215837591494714767938304611487608793477882715750613539221892047738072971032774162275223470290908780770786466919325086329014717253846892815227047800807404138251317913192260487028577097683774410704350806118785436875145438980568191727044341056647122024968572320513562610794498993862997660022472161360983519536725528941630316611185950534171093523915641216811705021686932202465470536309073160343237494570784278288092103458582506251744093242036241869349"));
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

        Message m = new Message("Test Message", senderKey, receiverKey);
        m.signMessage(senderKeyPrivate);
        Message m2 = new Message("Test", receiverKey, senderKey);
        m2.signMessage(receiverKeyPrivate);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals("3720e16ad9712e01705e6bfb72d31cbc3fa2999e32384e54b0cb57e1822c93dd", blockTest.getMerkleRootHash());
    }

    @Test
    void getMerkleRootHashTest02() {

        Message m = new Message("Test Message", senderKey, receiverKey);
        m.signMessage(senderKeyPrivate);

        // "Test" changed to "Test2"
        Message m2 = new Message("Test2", receiverKey, senderKey);
        m2.signMessage(receiverKeyPrivate);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));

        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertNotEquals("f5fc51789712f3fcb992ee2b5d7f7819948b8ef3714e7f012233ad021d6bb042", blockTest.getMerkleRootHash());

    }

    @Test
    void getMerkleRootHashTest03() {

        Message m = new Message("Test Message", senderKey, receiverKey);
        m.signMessage(senderKeyPrivate);
        Message m2 = new Message("Test", receiverKey, senderKey);
        m2.signMessage(receiverKeyPrivate);

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
        Message m = new Message("Test Message", senderKey, receiverKey);
        m.signMessage(senderKeyPrivate);
        Message m2 = new Message("Test", receiverKey, senderKey);
        m2.signMessage(receiverKeyPrivate);

        List<Message> messages = new ArrayList<>(Arrays.asList(m, m2));


        Block blockTest = new Block("prevHeadhash", "1f00ffff", messages);

        assertEquals(64, blockTest.getMerkleRootHash().length());
    }
}