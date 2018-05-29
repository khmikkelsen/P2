package node.mining;

import blockchain.block.Block;

public interface MiningCompleteListener {
    void onMiningComplete(final Block block);
}
