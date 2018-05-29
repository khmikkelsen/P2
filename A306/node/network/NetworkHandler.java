package node.network;

import com.google.gson.JsonSyntaxException;
import blockchain.block.Block;
import blockchain.message.Message;
import node.NodeClient;
import simulator.Simulator;
import node.network.commands.*;
import json.JsonUtil;

public class NetworkHandler {

    NodeClient nodeClient;

    public NetworkHandler(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void sendRequest(String request) {
        // Find out which command was invoked by mapping to Command which has an Enum field.
        Command c = JsonUtil.getParser().fromJson(request, Command.class);

        switch (c.getCommand()) {
            case SEND_MESSAGE:
                Message incomingMessage;

                try {
                    SendMessageCommand messageCommand = JsonUtil.getParser().fromJson(request, SendMessageCommand.class);
                    incomingMessage = messageCommand.getMessage();
                } catch (JsonSyntaxException e) {
                    break;
                }

                nodeClient.validateIncomingMessage(incomingMessage);

                break;

            case BLOCK_DATA:
                Block incomingBlock;

                try {
                    BlockDataCommand blockDataCommand = JsonUtil.getParser().fromJson(request, BlockDataCommand.class);
                    incomingBlock = blockDataCommand.getBlock();
                } catch (JsonSyntaxException e) {
                    break;
                }

                nodeClient.validateIncomingBlock(incomingBlock);

                break;
            case GET_BLOCK_COUNT:

                break;
            case GET_BLOCK:
                break;

            default:
                // Unsupported command.
                break;
        }
    }

    public void broadcastBlock(Simulator simulator, Block newBlock) {
        BlockDataCommand blockDataCommandCommand = new BlockDataCommand(newBlock);
        String command = JsonUtil.getParser().toJson(blockDataCommandCommand);
        simulator.broadcastBlock(nodeClient, command);
    }
}
