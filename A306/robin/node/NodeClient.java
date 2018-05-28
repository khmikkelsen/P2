package robin.node;

import RSA.BadVerificationException;
import RSA.RSAOAEPVerify;
import robin.*;
import robin.json.JsonUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NodeClient implements MiningCompleteListener {
    // TODO: Does it need to be synchronized?
    private List<Message> messagePool = Collections.synchronizedList(new ArrayList<Message>());

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future miningTask;
    private Miner miner;

    // 2 hours.
    private final int maxBlockTimeAhead = 2 * 60 * 60 * 1000;

    @Override
    public void miningComplete(Block newBlock) {
        try {
            if (newBlock != null) {
                System.out.println("Writing mined block to database");
                DatabaseConnection.addBlock(newBlock);
                System.out.println("Wrote mined block to database");


                System.out.println(JsonUtil.getPrettyParser().toJson(DatabaseConnection.getBlockTable()));

                messagePool.removeAll(newBlock.getMessages());

                // TODO: Broadcast mined block to network.
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        stopMining();
        startMining();
    }

    public void validateIncomingMessage(Message message) {
        if (isMessageSignatureValid(message)) {
            messagePool.add(message);

            // Start mining if not already mining.
            if (miningTask == null || miningTask.isDone()) {
                startMining();
            }
        }
    }

    private synchronized void stopMining() {
        if (miningTask != null && !miningTask.isDone()) {
            miner.removeListener(this);
            miningTask.cancel(true);
        }
    }

    private synchronized void startMining() {
        if (messagePool.size() > 0) {
            miner = new Miner(new ArrayList<>(messagePool));
            miner.addListener(this);
            miningTask = executorService.submit(miner);
        }
    }

    public void validateIncomingBlock(Block newBlock) {
        try {
            long blockCount = DatabaseConnection.getBlockCount();
            Block previousBlock = DatabaseConnection.getBlockByIndex(blockCount - 1);

            if (isBlockValid(newBlock, previousBlock)) {
                // Remove all messages, that are included
                messagePool.removeAll(newBlock.getMessages());


                // Stop the mining thread.
                stopMining();

                // Start the mining thread.
                startMining();

                // Add block to database.
                DatabaseConnection.addBlock(newBlock);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBlockValid(Block newBlock, Block previousBlock) {
        long blockCount = previousBlock.getIndex() + 1;

        // Doesn't deal with this node not being up to date with the longest chain.
        // This node should retrieve the chain on startup before accepting incoming connections.
        // 1. Must refer to the latest block.
        if (!isPreviousHashCorrect(previousBlock, newBlock.getPrevHeadHash())) {
            return false;
        }

        // 2. Must have messages.
        if (newBlock.getMessages() == null || newBlock.getMessages().size() < 1) {
            return false;
        }

        // 3. Must not be more than two hours in the future.
        if (newBlock.getTimestamp() - new Date().getTime() > maxBlockTimeAhead) {
            return false;
        }

        // 4. Must have valid proof of work (hash lower than claimed target)
        if (!isProofOfWorkValid(newBlock)) {
            return false;
        }

        // 5. Is claimed target correct according to rules?
        // blockCount == new block index
        if (!isTargetValid(newBlock, previousBlock, blockCount)) {
            return false;
        }

        // 6. Are all messages signed correctly?
        for (Message message : newBlock.getMessages()) {
            if (!isMessageSignatureValid(message)) {
                return false;
            }
        }

        // 7. Verify merkle root hash
        if (!isMerkleRootHashValid(newBlock)) {
            return false;
        }

        return true;
    }

    private boolean isPreviousHashCorrect(Block previousBlock, String previousHeadHash) {
        return previousBlock.getHash().equals(previousHeadHash);
    }

    private boolean isProofOfWorkValid(Block newBlock) {
        BigInteger blockHash = new BigInteger(newBlock.calculateHash(), 16);
        BigInteger target = new Target(newBlock.getCompactTarget()).getBigIntegerTarget();

        return blockHash.compareTo(target) <= 0;
    }

    private boolean isTargetValid(Block newBlock, Block previousBlock, long newBlockIndex) {
        String newBlockCompactTarget = newBlock.getCompactTarget();

        // If the block before this block is a multiple of the target adjust interval, first adjust local target.
        if ((newBlockIndex - 1) % Chain.getTargetAdjustInterval() == 0) {
            try {
                // Start period block == previous block - target adjust interval.
                Block startPeriodBlock = DatabaseConnection.getBlockByIndex(newBlockIndex - 1 - Chain.getTargetAdjustInterval());

                return newBlockCompactTarget.equals(Chain.adjustTarget(previousBlock, startPeriodBlock).getCompactTarget());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Otherwise the target should be the same as in the previous block.
        else {
            return newBlockCompactTarget.equals(previousBlock.getCompactTarget());
        }
    }

    private boolean isMessageSignatureValid(Message message) {
        try {
            new RSAOAEPVerify(message.getSignature().getBytes(), message.getMessage().getBytes(), message.getSender());
            return true;
        } catch (IOException | BadVerificationException e) {
            return false;
        }
    }

    private boolean isMerkleRootHashValid(Block newBlock) {
        String realMerkleRootHash = BlockUtil.calculateMerkleRootHash(newBlock.getMessages());

        return realMerkleRootHash.equals(newBlock.getMerkleRootHash());
    }
}
