package node.network.commands;

import blockchain.block.Block;

public class BlockDataCommand {

    private CommandType command = CommandType.BLOCK_DATA;
    private Block block;

    public BlockDataCommand(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
