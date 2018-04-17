package blockchain;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Message> list = new ArrayList<>();


//
        list.add(new Message("a"));
        list.add(new Message("b"));
        list.add(new Message("c"));
//        list.add(new Message("d"));
//        list.add(new Message("f"));
//

        BlockchainChain chain = new BlockchainChain();
        chain.generateGenesis();

        chain.addBlock(list);
        chain.isChainValid();

        System.out.println(chain);
        System.out.println("Done");

        List<Message> testList = new ArrayList<>();


//
        testList.add(new Message("a"));
        testList.add(new Message("b"));
        testList.add(new Message("c"));
        BlockVers2 b = new BlockVers2("", Chain.getTarget().getCompactTarget(), 1, testList);
        b.calcMerkleHash();

        String testMerkleHash = b.getMerkleRootHash();

        if (testMerkleHash.equals(chain.getBlock(1).getMerkleRootHash())) {
            System.out.println("The merkle roots are equal");
        }
    }
}
