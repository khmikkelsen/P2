package a306.blockchain;
/**
 *This class is used to create a message hash. It uses the message, sender and recipient for this.
 */
class Message {
    private String message;
    private String sender;
    private String recipient;

    public Message(String message) {
        this.message = message;
    }

    /**
     * The function calculateHash: calls another function that returns a message digest.
     * @return message digest(hash)
     */
    public String calculateHash() {
        return StringUtil.applySha256(message
                + sender
                + recipient);
    }
}
