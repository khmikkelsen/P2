package rsa;

/**
 * Class for a pair of RSAKeys.
 * Constructor allow from initialization from two RSAKeys.
 */

public class RSAKeyPair {
    RSAKey privateKey;
    RSAKey publicKey;

    /**
     * Constructor from two RSAKeys
     * @param privateKey Private RSAKey
     * @param publicKey Public RSAKey
     */
    public RSAKeyPair(RSAKey privateKey, RSAKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /*
     * Getters
     */
    public RSAKey getPrivateKey() {
        return privateKey;
    }

    public RSAKey getPublicKey() {
        return publicKey;
    }
}
