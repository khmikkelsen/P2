package robin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // write your code here

        DatabaseConnection db = new DatabaseConnection();
        db.setup();
        db.createBlockTable();
        db.createMessageTable();

        Message m = new Message("Test Mesage", "Robin Public Key", "Melanie Public Key");
        m.signMessage("RSA private key");
        Message m2 = new Message("Du er dum", "Martin Public Key", "Kasper Public Key");
        m2.signMessage("RSA private key");

        List<Message> msgs = new ArrayList<>(Arrays.asList(m, m2));

        Block b = new Block("prevHeadHash", Chain.getTarget().getCompactTarget(), msgs);
        b.mineBlock();

        db.addBlock(b);

        Block b2 = db.getBlockByIndex(1);
        System.out.println("Retrieved block 1 from db:");
        System.out.println(b2 + "\n");

        Block b3 = db.getBlockByIndex(10);
        System.out.println("Retrieved block 10 from db:");
        System.out.println(b3 + "\n");
    }
}
