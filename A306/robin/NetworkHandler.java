package robin;

import RSA.BadVerificationException;
import RSA.RSAOAEPVerify;
import com.google.gson.JsonSyntaxException;
import robin.commands.*;
import robin.json.JsonUtil;
import robin.node.NodeClient;

import java.io.IOException;

public class NetworkHandler {

    NodeClient nodeClient;

    public NetworkHandler(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void sendRequest(String request) {
        // Find out which command was invoked by mapping to NetworkCommand which has an Enum field.
        NetworkCommand c = JsonUtil.getParser().fromJson(request, NetworkCommand.class);

        switch (c.getCommand()) {
            case SEND_MESSAGE:
                Message incomingMessage;

                try {
                    SendMessage messageCommand = JsonUtil.getParser().fromJson(request, SendMessage.class);
                    incomingMessage = messageCommand.getMessage();
                } catch (JsonSyntaxException e) {
                    break;
                }

                nodeClient.validateIncomingMessage(incomingMessage);

                break;

            case BLOCK_DATA:
                Block incomingBlock;

                try {
                    BlockData blockData = JsonUtil.getParser().fromJson(request, BlockData.class);
                    incomingBlock = blockData.getBlock();
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
        BlockData blockDataCommand = new BlockData(newBlock);
        String command = JsonUtil.getParser().toJson(blockDataCommand);
        simulator.broadcastBlock(nodeClient, command);
    }
}
