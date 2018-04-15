package A306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is for creating blocks within a blockchain. See: Blockchain Technology.
 */

class Block {

    private String prevHeadhash;
    private String compactDifficulty;
    private int nonce;
    private String merkleRootHash;
    private long timestamp;

    private String hash;
    private List<Message> msg;



    //The constructor for a block: creates block header at instance creation.

    Block(String prevHeadHash, List<Message> msg) {
        this.msg = msg;
        this.prevHeadhash = prevHeadHash;
        this.timestamp = new Date().getTime();
    }


    /**
     * The method calculateHash: creates a hashed header of the block.
     * @return hash of block
     */
    public final String calculateHash() {
        return StringUtil.applySha256(
                prevHeadhash
                        + Long.toString(timestamp)
                        + merkleRootHash
                        + Integer.toString(nonce)
                        + compactDifficulty
        );
    }

    /**
     * The method calcMerkleHash: takes each message(Blockchain transaction) and creates a hash(digest) of it, and then
     * uses them to create the Merkle root hash.
     * @return  Merkle root, consisting of a blocks transaction hashes
     */
    public String calcMerkleHash() {
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
     * The function calculateMerkleRootHash: takes a list of hashes; If there is more than 1 node;
     * For as long as i is less than amount of hashes, then hash i and i+1 get combined into a new hash and added to the
     * newNodes list. hashedCount iterates after a combination is made.  i is iterated by 2 because 2 hashes are combined
     * each time.
     * When out of for loop, nodeSize is checked against hashedCount to explore if any hashes remains uncombined. If yes,
     * it is added to the newNodes list.
     * Is recursive. The whole process starts again, with the now updated hash list : newNodes.
     * When the newNode list has a size of 1, then the function returns the first hash in list, which is now
     * a Merkle root.
     * @param nodes  to generate a Merkle root from
     * @return nodes, or rather the newly generated merkle root , which is the first and only element in nodes list.
     */
    private String calculateMerkleRootHash(List<String> nodes) {
        if (nodes.size() > 1) {
            List<String> newNodes = new ArrayList<>();

            int hashedCount = 0;

            for (int i = 0; i < nodes.size() - 1; i += 2) {
                String combinedHash = StringUtil.applySha256(nodes.get(i) + nodes.get(i + 1));
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


    public void mineBlock() {



    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCompactDifficulty() {
        return compactDifficulty;
    }
}




