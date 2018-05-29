
package blockchain.message;

import rsa.RSAKey;
import rsa.RSAOAEPSign;
import rsa.Signature;
import blockchain.utils.StringUtil;

import java.io.IOException;
import java.util.Objects;

//Is message signed or unsigned, and is senderPublicKey/receiver public keys? is it the message encrypted before signing?
/* Signing message and public key encryption:
 * 1) hash message, fx sha256
 * 2) Encrypt digest with private key of senderPublicKey; "Thus, an encrypted hash value is obtained". Use fx
 * a mathematical cryptographic encryption algorithm for calculating digital signatures from a message digest.(rsa)
 * 3) Verify:
 * 3.1) hash the SIGNED message; Use same hashing algorithm as before. " The obtained new hash is known as:
 * CURRENT HASH_VALUE"
 * 3.2) Decrypt the CURRENT HASH-VALUE with senders public key; Use same encryption algorithm as before.
 * "The result is known as: ORIGINAL HASH-VALUE, which is the hash value of message before signing(digest)"
 * 3.3) Compare CURRENT HASH_VALUE and ORIGINAL HASH_VALUE - must be identical.
 */
public class Message {
    private String message;
    private RSAKey senderPublicKey;
    private RSAKey recipientPublicKey;
    private Signature signature;

    public Message(String message, RSAKey senderPublicKey, RSAKey recipientPublicKey) {
        this.message = message;
        this.senderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
    }

    public Message(String message, RSAKey senderPublicKey, RSAKey recipientPublicKey, Signature signature) {
        this.message = message;
        this.senderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
        this.signature = signature;
    }

    public String calculateHash() {
        return StringUtil.applySha256(message
                + recipientPublicKey.getBase64String()
                + senderPublicKey.getBase64String()
                + signature.getBase64String());
    }

    public RSAKey getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public RSAKey getSenderPublicKey() {
        return senderPublicKey;
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) &&
                Objects.equals(senderPublicKey, message1.senderPublicKey) &&
                Objects.equals(recipientPublicKey, message1.recipientPublicKey) &&
                Objects.equals(signature, message1.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, senderPublicKey, recipientPublicKey, signature);
    }
}
