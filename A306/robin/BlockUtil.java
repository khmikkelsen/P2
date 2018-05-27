package robin;

import java.util.ArrayList;
import java.util.List;

public class BlockUtil {

    /**
     * The method calculateMerkleRootHash: takes each message(Blockchain transaction) and creates a hash of it, and then
     * uses them to create the Merkle root hash.
     *
     * @return Merkle root, consisting of a blocks transaction hashes
     */

    public static String calculateMerkleRootHash(List<Message> messages) {
        List<String> hashedMessages = new ArrayList<>();

        for (Message m : messages) {
            hashedMessages.add(m.calculateHash());
        }
        // The list of hashes are given to getMerkleRootHash function; a Merkle root is returned.
        String merkleRootHash = calculateMerkleRoot(hashedMessages);

        return merkleRootHash;
    }

    /**
     * The function getMerkleRootHash: takes a list of hashes; If there is more than 1 node.
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
    private static String calculateMerkleRoot(List<String> nodes) {
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

            return calculateMerkleRoot(newNodes);
        }

        return nodes.get(0);
    }
}
