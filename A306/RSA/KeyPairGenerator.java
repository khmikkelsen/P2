package rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyPairGenerator extends RSAOAEP {

    private int keyBitSize;
    BigInteger publicExponent;

    // Constructor
    public KeyPairGenerator(int keyBitSize) throws IOException {
        this.keyBitSize = keyBitSize / 2;
        this.publicExponent = new BigInteger("65537");
    }

    public RSAKeyPair generateKeyPair() throws IOException {

        BigInteger p, q, lambda;
        SecureRandom rand = new SecureRandom();

        while (true) {
            p = BigInteger.probablePrime(keyBitSize, rand);
            q = BigInteger.probablePrime(keyBitSize, rand);
            BigInteger rsaMod = p.multiply(q);

            if (rsaMod.bitLength() % 8 == 0) {
                lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
                BigInteger privateExponent = publicExponent.modInverse(lambda);

                RSAKey privateKey = new RSAKey(rsaMod, privateExponent);
                RSAKey publicKey = new RSAKey(rsaMod, publicExponent);

                return new RSAKeyPair(privateKey, publicKey);
            }
        }
    }

    // lcm (least common multiple) ved reduktion af greatest common divisor (gcd).
    private BigInteger lcm(BigInteger a, BigInteger b) {
        BigInteger AGcdB = a.gcd(b);
        BigInteger ATimesB = a.multiply(b);

        return ATimesB.divide(AGcdB);
    }
}
