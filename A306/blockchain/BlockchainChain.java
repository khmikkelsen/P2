package A306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BlockchainChain {

    LinkedList<BlockVers2> chain = new LinkedList<BlockVers2>();
    List<Message> msg;

    public BlockchainChain() {

        chain.add(createGenesisBlock());

    }

    //Create the genesis block; Note: Not finished
    private BlockVers2 createGenesisBlock() {
        List<Message> genesis = new ArrayList<>();
        genesis.add(new Message("GenesisBlock"));
        BlockVers2 genesisBlock = new BlockVers2(0);//Note: change til 0 og remove constructor param. index
        genesisBlock.timestamp = new Date().getTime();
        genesisBlock.prevHeadhash = "0";
        genesisBlock.merkleRootHash = genesisBlock.calcMerkleHash(genesis);
        genesisBlock.nonce = 0;//?
        genesisBlock.compactDifficulty = genesisBlock.getCompactDifficulty(); //difficulty 1 is at start
        genesisBlock.calculateHash();
        return genesisBlock;
                //BlockVers2(0, new Date().getTime(), genesis, "0");
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
