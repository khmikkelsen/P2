package A306.blockchain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static A306.blockchain.StringUtil.applySha256;

//Prøver bare en anden måde at lave blok

public class BlockVers2 {
    String prevHeadhash;
    String compactDifficulty = new A306.blockchain.Target(new Chain().getProofOfWorkLimit()).getCompactTarget();
    int nonce = 0; //'nonce starts at zero and is incremented at each hash'
    String merkleRootHash;
    long timestamp;
    int index;

    String hash;
    private List<Message> msg;

    public BlockVers2() {
        this.index = index;

    }

    /**
     * The method calculateHash: creates a hashed header of the block.
     * @return hash of block
     */
    public final String calculateHash() {
        return applySha256(
                prevHeadhash
                        + Long.toString(timestamp)
                        + merkleRootHash
                        + Integer.toString(nonce)
                        + compactDifficulty
                        + index

        );
    }

    /**
     * The method calcMerkleHash: takes each message(Blockchain transaction) and creates a hash of it, and then
     * uses them to create the Merkle root hash.
     * @return  Merkle root, consisting of a blocks transaction hashes
     */
    public String calcMerkleHash(List<Message> msg) {
        List<String> hashedMessages = new ArrayList<>();

        for (Message m : msg) {
            hashedMessages.add(m.calculateHash());
        }
        // The list of hashes are given to calculateMerklerootHash function; a Merkle root is returned.
        String merkleRootHash = calculateMerkleRootHash(hashedMessages);

        System.out.println(merkleRootHash);
        return merkleRootHash;
    }

    /**
     * The function calculateMerkleRootHash: takes a list of hashes; If there is more than 1 node.
     * For as long as i is less than amount of hashes, then hash i and i+1 get combined into a new hash and added to the
     * newNodes list. hashedCount iterates after a combination is made.  i is iterated by 2 beacuse 2 hashes are combined
     * each time.
     * When out of for loop, nodeSize is checked against hashedCount to explore if any hashes remains uncombined. If yes,
     * it is added to the newNodes list.
     * Is recursive. The whole process starts again, with the now updated hash list : newNodes.
     * When the newNode list has a size of 1, then the function returns the first hash in list, which is now
     * a Merkle root.
     * @param nodes to generate a Merkle root from
     * @return nodes, or rather the newly generated merkle root , which is the first and only element in nodes list.
     */
    private String calculateMerkleRootHash(List<String> nodes) {
        if (nodes.size() > 1) {
            List<String> newNodes = new ArrayList<>();

            int hashedCount = 0;

            for (int i = 0; i < nodes.size() -   1; i += 2) {
                String combinedHash = applySha256(nodes.get(i) + nodes.get(i + 1));
                hashedCount += 2;
                newNodes.add(combinedHash);
            }

            if (nodes.size() > hashedCount) {
                newNodes.add(nodes.get(nodes.size() - 1));
            }

            return calculateMerkleRootHash(newNodes);
        }

        return nodes.get(0);
    }

    public int getNonce(){
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCompactDifficulty() {
        return compactDifficulty;
    }

    public boolean mineBlockGenisis(){
        List<Message> genesis = new ArrayList<>();
        genesis.add(new Message("GenesisBlock"));
        BlockVers2 genesisBlock = new BlockVers2();
        genesisBlock.timestamp = new Date().getTime();
        genesisBlock.prevHeadhash = "0";
        genesisBlock.merkleRootHash = genesisBlock.calcMerkleHash(genesis);
        genesisBlock.nonce = 0;//?
        genesisBlock.compactDifficulty = genesisBlock.getCompactDifficulty(); //difficulty 1 is at start
        String minedHash = genesisBlock.calculateHash();
        String mineHash2 = applySha256(minedHash);


        while(!mineHash2.nuller <= tearget.nuller){
            nonce++;
            genesisBlock.calculateHash();
            BlockchainChain.generateGenesis(genesisBlock);
            }

            BlockchainChain.generateGenesis(genesisBlock);



        /* 1)Collect transactions from the transaction pool and build a complete block such that its size does
         * not exceed 1 MB.
         * 2)Calculate the hash by applying SHA-256 twice to the Block header:
         * (Version + Previous Block Hash + Merkle Root + Timestamp + Difficulty Bits + Nonce )
         * Compare the result of Step # 2 with the expected number of zeros. If not matched then increment the nonce by
         * 1 and go back to Step # 1. Technically speaking the hash value is compared with a target.
         */
        return true;
    }
}



