package robin;

import RSA.BadVerificationException;
import RSA.RSAOAEPVerify;
import com.google.gson.JsonSyntaxException;
import robin.commands.*;
import robin.json.JsonUtil;
import robin.node.NodeClient;

import java.io.IOException;

public class NetworkRequestHandler {

    NodeClient nodeClient;

    public NetworkRequestHandler(NodeClient nodeClient) {
        this.nodeClient = nodeClient;
    }

    public void handleIncomingRequest(String request) {
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
            case GET_BLOCK_COUNT:

                break;
            case GET_BLOCK:
                break;

            default:
                // Unsupported command.
                break;
        }
    }

    private static void sendMessage(Message message) {
        try {
            RSAOAEPVerify verify = new RSAOAEPVerify(message.getSignature().getBytes(), message.getMessage().getBytes(), message.getSender());
        } catch (IOException | BadVerificationException e) {
            // Invalid signature.
            return;
        }

        // TODO: Add to queue of messages and begin mining at some point.
    }

    private void sendBlockCount(long blockCount) {
        // Create socket and respond.
    }
}
