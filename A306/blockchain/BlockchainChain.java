package A306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BlockchainChain {

    LinkedList<BlockVers2> chain;
    List<Message> msg;

    public BlockchainChain() {

        chain = new LinkedList<BlockVers2>();
    }

    //Create the genesis block; Note: Not finished
     static void generateGenesis(BlockVers2 g) {

        new BlockchainChain().chain.add(g);
    }

    //Get latest block in chain for further use
    private BlockVers2 getLatestBlock(){
        return chain.getLast();
    }



    //Add new block to chain if mining produces required target (amount of leading zeros).
    private void addBlock(BlockVers2 in) {


        in.timestamp = in.getTimestamp();
        in.prevHeadhash = getLatestBlock().calculateHash();
        in.hash = in.calculateHash();
        in.merkleRootHash = in.calcMerkleHash(msg);
        in.compactDifficulty = in.getCompactDifficulty();
        in.nonce = in.getNonce();
        chain.push(in);
        in.index = chain.indexOf(chain.getLast());

    }

    //check if blocks are valid
    private boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {

            final BlockVers2 currentBlock = chain.get(i);
            final BlockVers2 prevBlock = chain.get(i - 1);

            if (currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            if (prevBlock.hash.equals(prevBlock.calculateHash())){
                return false;
            }

        }
        return true;
    }
}
