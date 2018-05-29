package robin.node;

import RSA.BadVerificationException;
import RSA.InvalidRSAKeyException;
import RSA.RSAKey;
import RSA.RSAOAEPVerify;
import robin.*;

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

    private NetworkHandler networkHandler;

    private Simulator simulator;
    private String simulatorName;
    private DatabaseConnection databaseConnection;

    public NodeClient(String simulatorName, Simulator simulator, DatabaseConnection databaseConnection) {
        this.simulatorName = simulatorName;
        this.simulator = simulator;
        this.databaseConnection = databaseConnection;

        mineGenesisBlock();
        networkHandler = new NetworkHandler(this);
    }

    // 2 hours.
    private final int maxBlockTimeAhead = 2 * 60 * 60 * 1000;

    @Override
    public void onMiningComplete(Block newBlock) {
        try {
            if (newBlock != null) {
                databaseConnection.addBlock(newBlock);

                System.out.println(simulatorName + " successfully mined a block, message count: " + messagePool.size());
//                System.out.println(JsonUtil.getPrettyParser().toJson(databaseConnection.getBlockTable()));

                messagePool.removeAll(newBlock.getMessages());

                // TODO: Broadcast mined block to network.
                networkHandler.broadcastBlock(simulator, newBlock);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        stopMining();
        startMining();
    }

    // TODO: Does it need to be synchronized? Probably, because onMiningComplete is called from another thread.
    private synchronized void stopMining() {
        if (miningTask != null && !miningTask.isDone()) {
            miner.removeListener(this);
            miningTask.cancel(true);
        }
    }

    // TODO: Does it need to be synchronized? Probably, because onMiningComplete is called from another thread.
    private synchronized void startMining() {
        if (messagePool.size() > 0) {
            miner = new Miner(new ArrayList<>(messagePool), databaseConnection);
            miner.addListener(this);
            miningTask = executorService.submit(miner);
        }
    }

    public void validateIncomingMessage(Message message) {

        System.out.println(simulatorName + " recieved incoming message");
        if (isMessageSignatureValid(message)) {
            messagePool.add(message);

//            System.out.println(simulatorName + " validated message");
            // Start mining if not already mining.
            if (miningTask == null || miningTask.isDone()) {
//                System.out.println(simulatorName + " started mining, message count: " + messagePool.size());
                startMining();
            }
        }
    }

    public void validateIncomingBlock(Block newBlock) {
        try {
            long blockCount = databaseConnection.getBlockCount();
            Block previousBlock = databaseConnection.getBlockByIndex(blockCount - 1);

            if (isBlockValid(newBlock, previousBlock)) {

                // Stop the mining thread.
                stopMining();

                // Remove all messages, that are included
                messagePool.removeAll(newBlock.getMessages());

                // Start the mining thread.
                startMining();

                // Add block to database.
                databaseConnection.addBlock(newBlock);

                System.out.println(simulatorName + " added incoming block to database");
//                System.out.println(JsonUtil.getPrettyParser().toJson(databaseConnection.getBlockTable()));

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
        if ((newBlockIndex - 1) % TargetUtil.getTargetAdjustInterval() == 0 && (newBlockIndex - 1) > 0) {
            try {
                // Start period block == previous block - target adjust interval.
                Block startPeriodBlock = databaseConnection.getBlockByIndex(newBlockIndex - 1 - TargetUtil.getTargetAdjustInterval());

                return newBlockCompactTarget.equals(TargetUtil.adjustTarget(previousBlock, startPeriodBlock).getCompactTarget());
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
            new RSAOAEPVerify(message.getSignature().getBytes(), message.getMessage().getBytes(), message.getSenderPublicKey());
            return true;
        } catch (IOException | BadVerificationException e) {
            return false;
        }
    }

    private boolean isMerkleRootHashValid(Block newBlock) {
        String realMerkleRootHash = BlockUtil.calculateMerkleRootHash(newBlock.getMessages());

        return realMerkleRootHash.equals(newBlock.getMerkleRootHash());
    }

    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public String getSimulatorName() {
        return simulatorName;
    }

    private void mineGenesisBlock() {
        try {
            if (databaseConnection.getBlockCount() != 0) {
                return;
            }

            RSAKey genesisPrivateKey = new RSAKey("MIICCQKCAQEAgu1qZL08/NV5d423pAi1IA8IxzSROVl6t4zWRstR9Olrpyb4QNRfg5kzN7XpcmmXcIDAEu32u1JAKlRNENpERH21odU99g7Rocxz6gCtVK76ucti4cn4wgIgQbzA03Jn01rGo4WDUCgPQUZpdQJQBi4+uGp9tDPQCswVpSAbWXxvQwLlpZ0IohfdjHo0axpcsDt9spPIIHceoLPLkGildS0ye+IO50wfYfsM3lZSlVSiWUIITOqJQtWcSo7ebGIDyeC1r5Th0dCpKSPjAtAACwoonoRVJZaVVFpAq0AS67TKQOHStopKmoBbsO84L+LQ4+47DveuDxcWac05z00jtQKCAQACeiCYGSl2eh54cn6zNIlNZY4WYL+k91+rlIH4UxVGSNqaeLxb06Nz+gRz98sxI4oeh4e6w/RLUnt4IYystohx8vPqWikNhvW/NdRKl3/aro3RUTsEx71fBEsdG0ewUxrfmY57s7AKkUYf8h0Bfk1KdKUHTa0k/pGffAF71IntQoxqfsPRVgHgQeQ0BN7OuZc+dDLxbA5MEGRSyWzxmc01P6VP2pmndG3WE9l4q5z84m4AunBMA/5tr7bB4I9I49X2J5D/TEDWTCaU1YWia7t2ljdl9c/K5rdbDQ1ZHPrAZc4BZWpvnlr72dNbAu2sU/AXDzGSJn4w+nZUgB03wcNJ");
            RSAKey genesisPublicKey = new RSAKey("MIIBCgKCAQEAgu1qZL08/NV5d423pAi1IA8IxzSROVl6t4zWRstR9Olrpyb4QNRfg5kzN7XpcmmXcIDAEu32u1JAKlRNENpERH21odU99g7Rocxz6gCtVK76ucti4cn4wgIgQbzA03Jn01rGo4WDUCgPQUZpdQJQBi4+uGp9tDPQCswVpSAbWXxvQwLlpZ0IohfdjHo0axpcsDt9spPIIHceoLPLkGildS0ye+IO50wfYfsM3lZSlVSiWUIITOqJQtWcSo7ebGIDyeC1r5Th0dCpKSPjAtAACwoonoRVJZaVVFpAq0AS67TKQOHStopKmoBbsO84L+LQ4+47DveuDxcWac05z00jtQIDAQAB");

            Message genesisMessage = new Message("Genesis block", genesisPublicKey, genesisPublicKey);
            genesisMessage.signMessage(genesisPrivateKey);

            Block genesisBlock = new Block("00007836244f37d24bf9b4f59fbe51ee85d54f12cc2349fde04199b5dd969013",
                    "0000000000000000000000000000000000000000000000000000000000000000",
                    "1e100000",
                    144267,
                    "62e9c20979b19211cd82d33f2ece5bc441c02714907dc3bb969f06bc1901ae7b",
                    1527525138304L,
                    0,
                    Collections.singletonList(genesisMessage));

            databaseConnection.addBlock(genesisBlock);
        } catch (SQLException | InvalidRSAKeyException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}