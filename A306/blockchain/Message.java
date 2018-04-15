
package A306.blockchain;

import A306.blockchain.StringUtil;

//Is message signed or unsigned and is sender/receiver public keys?
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

    public Message(String message) {
        this.message = message;
    }

    public String calculateHash() {
        return StringUtil.applySha256(message
                + sender
                + recipient);
    }
}
