package a306.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Message> list = new ArrayList<>();


//
//        list.add(new Message("a"));
//        list.add(new Message("b"));
//        list.add(new Message("c"));
//        list.add(new Message("d"));
//        list.add(new Message("f"));
//
//        Block b = new Block("prevHash", list);
//
//        b.calcMerkleHash();

        Chain c = new Chain();

        c.adjustDifficulty();
    }
}
