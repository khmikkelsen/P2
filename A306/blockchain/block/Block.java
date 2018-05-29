package blockchain.block;

import blockchain.message.Message;
import blockchain.target.Target;
import blockchain.utils.StringUtil;
import json.JsonUtil;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Block {
    private String prevHeadhash;
    private String compactTarget;
    private int nonce = new Random().nextInt(Integer.MAX_VALUE); // nonce starts at random number and is incremented at each hash
    private String merkleRootHash;
    private long timestamp;

    private Long index; // Can be null.

    private String hash;
    private List<Message> messages;

    public Block(String prevHeadhash, String compactTarget, List<Message> messages) {
        this.timestamp = new Date().getTime();
        this.prevHeadhash = prevHeadhash;
        this.compactTarget = compactTarget;
        this.merkleRootHash = BlockUtil.calculateMerkleRootHash(messages);
        this.messages = messages;
    }

    public Block(String hash, String prevHeadhash, String compactTarget, int nonce, String merkleRootHash, long timestamp, long index, List<Message> messages) {
        this.hash = hash;
        this.prevHeadhash = prevHeadhash;
        this.compactTarget = compactTarget;
        this.index = index;
        this.timestamp = timestamp;
        this.merkleRootHash = merkleRootHash;
        this.nonce = nonce;
        this.messages = messages;
    }


    /**
     * Return the hash of all the fields in the header.
     */
    public final String calculateHash() {
        return StringUtil.applySha256(
                prevHeadhash
                        + Long.toString(timestamp)
                        + merkleRootHash
                        + Integer.toString(nonce)
                        + compactTarget

        );
    }

    public void mineBlock() {
        this.merkleRootHash = BlockUtil.calculateMerkleRootHash(messages);

        BigInteger target = new Target(compactTarget).getBigIntegerTarget();

        while (new BigInteger(this.calculateHash(), 16).compareTo(target) >= 0) {
            if (nonce == Integer.MAX_VALUE) {
                nonce = 0;
                this.timestamp = new Date().getTime();
            } else {
                nonce++;
            }
        }
    }

    public String getMerkleRootHash() {
        return merkleRootHash;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCompactTarget() {
        return compactTarget;
    }

    public String getPrevHeadHash() {
        return prevHeadhash;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Long getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHeadhash='" + prevHeadhash + '\'' +
                ", compactTarget='" + compactTarget + '\'' +
                ", nonce=" + nonce +
                ", merkleRootHash='" + merkleRootHash + '\'' +
                ", timestamp=" + timestamp +
                ", index=" + index +
                ", hash='" + hash + '\'' +
                ", messages=" + messages +
                '}';
    }
    public String getJSon ()
    {
        return JsonUtil.getParser().toJson(this);
    }
}



