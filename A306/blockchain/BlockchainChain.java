package A306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static A306.blockchain.StringUtil.applySha256;

public class BlockchainChain {

    LinkedList<BlockVers2> chain;
    List<Message> msg;

    public BlockchainChain() {

        chain = new LinkedList<BlockVers2>();
    }

    private BlockVers2 mineBlockGenisis(){
        List<Message> genesis = new ArrayList<>();
        genesis.add(new Message("GenesisBlock"));
        BlockVers2 genesisBlock = new BlockVers2();
        genesisBlock.timestamp = new Date().getTime();
        genesisBlock.prevHeadhash = "0";
        genesisBlock.merkleRootHash = genesisBlock.calcMerkleHash(genesis);
        genesisBlock.nonce = genesisBlock.getNonce();
        genesisBlock.compactDifficulty = genesisBlock.getCompactDifficulty(); //difficulty 1 is at start
        String minedHash = genesisBlock.calculateHash();
        String mineHash2 = applySha256(minedHash);


        while(!mineHash2.nuller <= tearget.nuller){
            nonce++;
            genesisBlock.calculateHash();
        }

        return genesisBlock;

        /* 1)Collect transactions from the transaction pool and build a complete block such that its size does
         * not exceed 1 MB.
         * 2)Calculate the hash by applying SHA-256 twice to the Block header:
         * (Version + Previous Block Hash + Merkle Root + Timestamp + Difficulty Bits + Nonce )
         * Compare the result of Step # 2 with the expected number of zeros. If not matched then increment the nonce by
         * 1 and go back to Step # 1. Technically speaking the hash value is compared with a target.
         */

    }

    //Create the genesis block; Note: Not finished
     private void generateGenesis() {
        BlockVers2 g = mineBlockGenisis();
         chain.add(g);
         g.index = chain.indexOf(chain.getLast());
    }

    //Get latest block in chain for further use
    private BlockVers2 getLatestBlock(){
        return chain.getLast();
    }

    private BlockVers2 mineBlock(){
        BlockVers2 b = new BlockVers2();
        b.timestamp = b.getTimestamp();
        b.prevHeadhash = getLatestBlock().calculateHash();
        b.hash = b.calculateHash();
        b.merkleRootHash = b.calcMerkleHash(msg);
        b.compactDifficulty = b.getCompactDifficulty();
        b.nonce = b.getNonce();
        String minedHash = b.calculateHash();
        String mineHash2 = applySha256(minedHash);

        while(!mineHash2.nuller <= target.nuller){
            nonce++;
            b.calculateHash();
        }

        return b;

    }




    //Add new block to chain if mining produces required target (amount of leading zeros).
    private void addBlock() {
        BlockVers2 in = mineBlock();
        chain.add(in);
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
