package node.network.commands;

import blockchain.message.Message;

public class SendMessageCommand {
    private CommandType command = CommandType.SEND_MESSAGE;
    private Message message;

    public SendMessageCommand(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
