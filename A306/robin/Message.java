
package robin;

import RSA.RSAKey;
import RSA.RSAOAEPSign;
import RSA.Signature;

import java.io.IOException;
import java.util.Objects;

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
public class Message {
    private String message;
    private RSAKey sender;
    private RSAKey recipient;
    private Signature signature;

    public Message(String message, RSAKey sender, RSAKey recipient) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Message(String message, RSAKey sender, RSAKey recipient, Signature signature) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.signature = signature;
    }

    public String calculateHash() {
        return StringUtil.applySha256(message
                + recipient.getBase64String()
                + sender.getBase64String()
                + signature.getBase64String());
    }

    public RSAKey getRecipient() {
        return recipient;
    }

    public RSAKey getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Signature getSignature() {
        return signature;
    }

    public void signMessage(RSAKey privateKey) {
        try {
            RSAOAEPSign sign = new RSAOAEPSign(message, privateKey);
            this.signature = new Signature(sign.getSignature());
        }

        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) &&
                Objects.equals(sender, message1.sender) &&
                Objects.equals(recipient, message1.recipient) &&
                Objects.equals(signature, message1.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, sender, recipient, signature);
    }
}
