package robin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Prøver bare en anden måde at lave blok

public class Block {
    String prevHeadhash;
    String compactDifficulty;
    int nonce = 0; //nonce starts at zero and is incremented at each hash
    String merkleRootHash;
    long timestamp;
    int index;

    String hash;
    private List<Message> messages;

    public Block(String prevHeadhash, String compactDifficulty, List<Message> messages) {
        timestamp = new Date().getTime();
        this.prevHeadhash = prevHeadhash;
        this.compactDifficulty = compactDifficulty;
        this.messages = messages;
    }

    public Block(String hash, String prevHeadhash, String compactDifficulty, int index, long timestamp, String merkleRootHash, int nonce) {
        this.hash = hash;
        this.prevHeadhash = prevHeadhash;
        this.compactDifficulty = compactDifficulty;
        this.index = index;
        this.timestamp = timestamp;
        this.merkleRootHash = merkleRootHash;
        this.nonce = nonce;
    }

    /**
     * The method calculateHash: creates a hashed header of the block.
     *
     * @return hash of block
     */
    public final String calculateHash() {
        return StringUtil.applySha256(
                prevHeadhash
                        + Long.toString(timestamp)
                        + merkleRootHash
                        + Integer.toString(nonce)
                        + compactDifficulty
                        + index

        );
    }

    public void mineBlock() {

        calcMerkleHash();

        while (new BigInteger(this.calculateHash(), 16).compareTo(Chain.getTarget().getBigIntegerTarget()) > 0) {
            if (nonce == Integer.MAX_VALUE) {
                nonce = 0;
                setTimestamp(new Date().getTime());
            } else {
                nonce++;
            }
        }
    }

    /**
     * The method calcMerkleHash: takes each message(Blockchain transaction) and creates a hash of it, and then
     * uses them to create the Merkle root hash.
     *
     * @return Merkle root, consisting of a blocks transaction hashes
     */
    private void calcMerkleHash() {
        List<String> hashedMessages = new ArrayList<>();

        for (Message m : messages) {
            hashedMessages.add(m.calculateHash());
        }
        // The list of hashes are given to calculateMerklerootHash function; a Merkle root is returned.
        String merkleRootHash = calculateMerkleRootHash(hashedMessages);

        this.merkleRootHash = merkleRootHash;
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
     *
     * @param nodes to generate a Merkle root from
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

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCompactDifficulty() {
        return compactDifficulty;
    }

    public String getMerkleRootHash() {
        return merkleRootHash;
    }

    public String getPrevHeadHash() {
        return prevHeadhash;
    }

    public List<Message> getMessages() {
        return messages;
    }

    private void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHeadhash='" + prevHeadhash + '\'' +
                ", compactDifficulty='" + compactDifficulty + '\'' +
                ", nonce=" + nonce +
                ", merkleRootHash='" + merkleRootHash + '\'' +
                ", timestamp=" + timestamp +
                ", index=" + index +
                ", hash='" + hash + '\'' +
                ", messages=" + messages +
                '}';
    }
}



