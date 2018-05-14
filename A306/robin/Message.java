
package robin;

//Is message signed or unsigned, and is sender/receiver public keys? is it the message encrypted before signing?
/* Signing message and public key encryption:
 * 1) hash message, fx sha256
 * 2) Encrypt digest with private key of sender; "Thus, an encrypted hash value is obtained". Use fx
 * a mathematical cryptographic encryption algorithm for calculating digital signatures from a message digest.(RSA)
 * 3) Verify:
 * 3.1) hash the SIGNED message; Use same hashing algorithm as before. " The obtained new hash is known as:
 * CURRENT HASH_VALUE"
 * 3.2) Decrypt the CURRENT HASH-VALUE with senders public key; Use same encryption algorithm as before.
 * "The result is known as: ORIGINAL HASH-VALUE, which is the hash value of message before signing(digest)"
 * 3.3) Compare CURRENT HASH_VALUE and ORIGINAL HASH_VALUE - must be identical.
 */
class Message {
    private String message;
    private String sender;
    private String recipient;
    private String signature;

    public Message(String message, String sender, String recipient) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Message(String message, String sender, String recipient, String signature) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.signature = signature;
    }

    public String calculateHash() {
        return StringUtil.applySha256(message
                + recipient
                + sender
                + signature);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSignature() {
        return signature;
    }

    public void signMessage(String privateKey) {
        // TODO: Sign message
        this.signature = privateKey;
    }
}
