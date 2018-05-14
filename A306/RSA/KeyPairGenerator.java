package RSA;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class KeyPairGenerator extends RSAOAEP
{
    private BigInteger publicKey; // Public key eksponent
    private BigInteger privateKey; // Private key eksponent
    private BigInteger rsaMod;
    private Random rand;

    // Constructor
    public KeyPairGenerator (int keyBitSize, Random random)
    {
        this.publicKey = new BigInteger("65537");
        this.rand = random;
        genKeys(keyBitSize/2);
    }
    private void genKeys(int keyBitsSize)
    {
        boolean check = false;
        BigInteger p, q, lambda;

        while (!check)
        {
            rand.setSeed(rand.nextLong());
            p = BigInteger.probablePrime(keyBitsSize, rand);
            q = BigInteger.probablePrime(keyBitsSize, rand);
            this.rsaMod = p.multiply(q);

            if (rsaMod.bitLength() % 8 == 0) {
                lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
                this.privateKey = publicKey.modInverse(lambda);
                check = true;
            }
        }

    }


    // lcm (least common multiple) ved reduktion af greatest common divisor (gcd).
    private BigInteger lcm(BigInteger a, BigInteger b)
    {
        BigInteger AGcdB = a.gcd(b);
        BigInteger ATimesB = a.multiply(b);

        return ATimesB.divide(AGcdB);
    }

    // Getters
    public BigInteger getPublicKey() { return this.rsaMod; }
    public BigInteger getPublicE() { return this.publicKey; }
    public BigInteger getPrivateKey() { return this.privateKey; }

}
