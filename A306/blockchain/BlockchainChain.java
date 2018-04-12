package A306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BlockchainChain {

    LinkedList<BlockVers2> chain = new LinkedList<BlockVers2>();

    public BlockchainChain() {

        chain.add(createGenesisBlock());

    }

    //Create the genesis block; Note: Not finished
    private BlockVers2 createGenesisBlock() {
        List<Message> genesis = new ArrayList<>();
        genesis.add(new Message("GenesisBlock"));
        return new BlockVers2(0, new Date().getTime(), genesis, "0");
    }

    //Get latest block in chain for further use
    private BlockVers2 getLatestBlock(){
        return chain.getLast();
    }



    //Add new block to chain if mining is less than target. Not sure how mining will work yet!
    private void addBlock(BlockVers2 in) {


        in.timestamp = in.getTimestamp();
        in.prevHeadhash = getLatestBlock().calculateHash();
        in.hash = in.calculateHash();
        in.merkleRootHash = in.calcMerkleHash();
        in.compactDifficulty = in.getCompactDifficulty();
        chain.push(in);
        in.index = chain.indexOf(in);

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
