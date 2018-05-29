package robin.node;

import robin.Block;
import robin.Chain;
import robin.DatabaseConnection;
import robin.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Miner implements Runnable {

    private List<Message> messages;
    private List<MiningCompleteListener> listeners = new ArrayList<>();


    public Miner(List<Message> messages) {
        this.messages = messages;
    }

    public final void addListener(MiningCompleteListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(MiningCompleteListener listener) {
        listeners.remove(listener);
    }

    private final void notifyListeners(Block block) {
        if (block != null) {
            System.out.println("Successfully mined block");
        }

        for (MiningCompleteListener listener : listeners) {
            listener.miningComplete(block);
        }

        listeners = null;
    }

    @Override
    public void run() {
        try {
            System.out.println("Started mining");
            Block latestBlock = DatabaseConnection.getLatestBlock();

            if (latestBlock == null) {
                notifyListeners(null);
                return;
            }

            long newBlockIndex = latestBlock.getIndex() + 1;
            String compactTarget = latestBlock.getCompactTarget();

            // If the block before this block is a multiple of the target adjust interval, first adjust local target.
            if ((newBlockIndex - 1) % Chain.getTargetAdjustInterval() == 0 && (newBlockIndex - 1) > 0) {
                System.out.println(newBlockIndex);
                // Start period block == previous block - target adjust interval.
                Block startPeriodBlock = DatabaseConnection.getBlockByIndex(newBlockIndex - 1 - Chain.getTargetAdjustInterval());

                compactTarget = Chain.adjustTarget(latestBlock, startPeriodBlock).getCompactTarget();
            }

            // Otherwise the target should be the same as in the previous block.
            Block newBlock = new Block(latestBlock.getHash(), compactTarget, messages);

            // Mine the block. This method blocks until it is done.
            System.out.println("Started finding nonce...");
            newBlock.mineBlock();

            notifyListeners(newBlock);
        } catch (NullPointerException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
