package blockchain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static blockchain.StringUtil.applySha256;

public class BlockchainChain {

    LinkedList<BlockVers2> chain;


    public BlockchainChain() {
        chain = new LinkedList<BlockVers2>();
    }

    private BlockVers2 mineBlockGenisis() {
        List<Message> genesis = new ArrayList<>();
        genesis.add(new Message("GenesisBlock"));


        String prevHeadhash = "0000000000000000000000000000000000000000000000000000000000000000";

        BlockVers2 genesisBlock = new BlockVers2(prevHeadhash, Chain.getTarget().getCompactTarget(), chain.size(), genesis);
        genesisBlock.calcMerkleHash();

        while (new BigInteger(genesisBlock.calculateHash(), 16).compareTo(Chain.getTarget().getBigIntegerTarget()) > 0) {
            if (genesisBlock.nonce == Integer.MAX_VALUE) {
                genesisBlock.nonce = 0;
                genesisBlock.setTimestamp(new Date().getTime());
            } else {
                genesisBlock.nonce++;
            }
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
    public void generateGenesis() {
        if (chain.size() > 0) {
            return;
        }

        BlockVers2 g = mineBlockGenisis();
        chain.add(g);
    }

    //Get latest block in chain for further use
    private BlockVers2 getLatestBlock() {
        return chain.getLast();
    }

    private BlockVers2 mineBlock(List<Message> msg) {
        String prevHeadhash = getLatestBlock().calculateHash();
        BlockVers2 b = new BlockVers2(prevHeadhash, Chain.getTarget().getCompactTarget(), chain.size(), msg);
        b.calcMerkleHash();

        while (new BigInteger(b.calculateHash(), 16).compareTo(Chain.getTarget().getBigIntegerTarget()) > 0) {
            if (b.nonce == Integer.MAX_VALUE) {
                b.nonce = 0;
                b.setTimestamp(new Date().getTime());
            } else {
                b.nonce++;
            }
        }

        return b;
    }


    //Add new block to chain if mining produces required target (amount of leading zeros).
    public void addBlock(List<Message> messages) {
        if (chain.size() % 2016 == 0 && chain.size() > 0) {
            Chain.adjustDifficulty(chain.getLast(), chain.get(chain.size() - 1 - 2016));
        }

        BlockVers2 in = mineBlock(messages);
        chain.add(in);

    }

    //check if blocks are valid
        void isChainValid() {
        for (int i = 0; i < chain.size() - 1; i++) {

            final BlockVers2 currentBlock = chain.get(i);
            final BlockVers2 prevBlock = chain.get(i + 1);

            if (currentBlock.calculateHash().equals(currentBlock.calculateHash())) {
                System.out.println("True");
            }

            if (prevBlock.calculateHash().equals(prevBlock.calculateHash())) {
                System.out.println("True");
            }

            else
                System.out.println("false");

        }


        

    }

    @Override
    public String toString() {
        return "BlockchainChain{" +
                "chain=" + chain +
                '}';
    }

    public BlockVers2 getBlock(int i) {
        return chain.get(i);
    }
}
