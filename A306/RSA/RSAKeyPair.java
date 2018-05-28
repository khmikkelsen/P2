package RSA;

public class RSAKeyPair {
    RSAKey privateKey;
    RSAKey publicKey;

    public RSAKeyPair(RSAKey privateKey, RSAKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public RSAKey getPrivateKey() {
        return privateKey;
    }

    public RSAKey getPublicKey() {
        return publicKey;
    }
}
