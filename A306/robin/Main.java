package robin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // write your code here

        DatabaseConnection db = new DatabaseConnection();
        try {
            db.setup();
            db.createBlockTable();
            db.createMessageTable();
        } catch (SQLException exception) {
            return;
        }

        Message m = new Message("Test Mesage", "Robin Public Key", "Melanie Public Key");
        m.signMessage("RSA private key");
        Message m2 = new Message("Du er dum", "Martin Public Key", "Kasper Public Key");
        m2.signMessage("RSA private key");

        List<Message> msgs = new ArrayList<>(Arrays.asList(m, m2));

        Block block = new Block("prevHeadHash", Chain.getTarget().getCompactTarget(), msgs);
        block.mineBlock();

        try {
            long newBlockId = db.addBlock(block);

            db.addMessagesToBlockId(block.getMessages(), newBlockId);
        } catch ( SQLException exception) {
            // The block was added, but there was an error while trying to add the messages.

        }
        try {
            Block b2 = db.getBlockById(1);
            System.out.println("Retrieved block 1 from db:");
            System.out.println(b2 + "\n");

            Block b3 = db.getBlockById(10);
            System.out.println("Retrieved block 10 from db:");
            System.out.println(b3 + "\n");
        } catch (SQLException exception) {
            // Could not retrieve block(s).
        }
    }
}
