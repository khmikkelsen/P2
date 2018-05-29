package robin.node;

import robin.Block;
import robin.TargetUtil;
import robin.DatabaseConnection;
import robin.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Miner implements Runnable {

    private List<Message> messages;
    private DatabaseConnection databaseConnection;

    private List<MiningCompleteListener> listeners = new ArrayList<>();

    public Miner(List<Message> messages, DatabaseConnection databaseConnection) {
        this.messages = messages;
        this.databaseConnection = databaseConnection;
    }

    public final void addListener(MiningCompleteListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(MiningCompleteListener listener) {
        listeners.remove(listener);
    }

    private final void notifyListeners(Block block) {
        for (MiningCompleteListener listener : listeners) {
            listener.onMiningComplete(block);
        }

        listeners = null;
    }

    @Override
    public void run() {
        try {
            Block latestBlock = databaseConnection.getLatestBlock();

            if (latestBlock == null) {
                notifyListeners(null);
                return;
            }

            long newBlockIndex = latestBlock.getIndex() + 1;
            String compactTarget = latestBlock.getCompactTarget();

            // If the block before this block is a multiple of the target adjust interval, first adjust local target.
            if ((newBlockIndex - 1) % TargetUtil.getTargetAdjustInterval() == 0 && (newBlockIndex - 1) > 0) {
                System.out.println(newBlockIndex);
                // Start period block == previous block - target adjust interval.
                Block startPeriodBlock = databaseConnection.getBlockByIndex(newBlockIndex - 1 - TargetUtil.getTargetAdjustInterval());

                compactTarget = TargetUtil.adjustTarget(latestBlock, startPeriodBlock).getCompactTarget();
            }

            // Otherwise the target should be the same as in the previous block.
            Block newBlock = new Block(latestBlock.getHash(), compactTarget, messages);

            // Mine the block. This method blocks until it is done.
            newBlock.mineBlock();

            notifyListeners(newBlock);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
