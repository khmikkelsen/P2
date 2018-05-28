package RSA;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyPairGenerator extends RSAOAEP {

    private RSAKey privateKey;
    private RSAKey publicKey;


    // Constructor
    public KeyPairGenerator(int keyBitSize) throws IOException {
        genKeys(keyBitSize / 2, new BigInteger("65537"));
    }

    private void genKeys(int keyBitsSize, BigInteger publicExponent) throws IOException
    {
        BigInteger p, q, lambda;
        SecureRandom rand = new SecureRandom();

        while (true)
        {
            p = BigInteger.probablePrime(keyBitsSize, rand);
            q = BigInteger.probablePrime(keyBitsSize, rand);
            BigInteger rsaMod = p.multiply(q);

            if (rsaMod.bitLength() % 8 == 0) {
                lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
                BigInteger privateExponent = publicExponent.modInverse(lambda);

                this.publicKey = new RSAKey(rsaMod, publicExponent);
                this.privateKey = new RSAKey(rsaMod, privateExponent);

                break;
            }
        }
    }

    // lcm (least common multiple) ved reduktion af greatest common divisor (gcd).
    private BigInteger lcm(BigInteger a, BigInteger b) {
        BigInteger AGcdB = a.gcd(b);
        BigInteger ATimesB = a.multiply(b);

        return ATimesB.divide(AGcdB);
    }

    // Getters
    public RSAKey getPublicKey() {
        return publicKey;
    }

    public RSAKey getPrivateKey() {
        return privateKey;
    }


}
