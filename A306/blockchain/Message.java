
package A306.blockchain;

import A306.blockchain.StringUtil;

class Message {
    private String message;
    private String sender;
    private String recipient;

    public Message(String message) {
        this.message = message;
    }

    public String calculateHash() {
        return StringUtil.applySha256(message
                + sender
                + recipient);
    }
}
