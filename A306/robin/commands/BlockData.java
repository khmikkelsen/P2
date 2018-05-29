package robin.commands;

import robin.Block;
import robin.Message;

public class BlockData {

    private CommandType command = CommandType.BLOCK_DATA;
    private Block block;

    public BlockData(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
