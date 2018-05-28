package robin.commands;

import robin.Message;

public class SendMessage {
    private CommandType command = CommandType.SEND_MESSAGE;
    private Message message;

    public SendMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
