package a306.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Block {

    private String prevHeadhash;
    private String compactDifficulty;
    private int nonce;
    private String merkleRootHash;
    private long timestamp;

    private String hash;
    private List<Message> msg;


    Block(String prevHeadHash, List<Message> msg) {
        this.msg = msg;
        this.prevHeadhash = prevHeadHash;
        this.timestamp = new Date().getTime();
    }


    public final String calculateHash() {
        return StringUtil.applySha256(
                prevHeadhash
                        + Long.toString(timestamp)
                        + merkleRootHash
                        + Integer.toString(nonce)
                        + compactDifficulty
        );
    }

    public String calcMerkleHash() {
        List<String> hashedMessages = new ArrayList<>();

        for (Message m : msg) {
            hashedMessages.add(m.calculateHash());
        }

        String merkleRootHash = calculateMerkleRootHash(hashedMessages);

        System.out.println(merkleRootHash);
        return merkleRootHash;
    }

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


    public void mineBlock(int difficulty) {


    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCompactDifficulty() {
        return compactDifficulty;
    }
}




