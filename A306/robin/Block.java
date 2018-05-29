package robin;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import robin.json.JsonUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Block {
    private String prevHeadhash;
    private String compactTarget;
    private int nonce = 0; //nonce starts at zero and is incremented at each hash
    private String merkleRootHash;
    private long timestamp;

    private Long index; // Can be null.

    private String hash;
    private List<Message> messages;

    public Block(String prevHeadhash, String compactTarget, List<Message> messages) {
        this.timestamp = new Date().getTime();
        this.prevHeadhash = prevHeadhash;
        this.compactTarget = compactTarget;
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
                        + compactTarget

        );
    }

    public void mineBlock() {

        this.merkleRootHash = BlockUtil.calculateMerkleRootHash(messages);

        BigInteger target = new Target(compactTarget).getBigIntegerTarget();

        while (new BigInteger(this.calculateHash(), 16).compareTo(target) > 0) {
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



