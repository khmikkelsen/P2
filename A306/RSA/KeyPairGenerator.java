package RSA;

import java.math.BigInteger;
import java.util.Random;

public class KeyPairGenerator extends RSAOAEP
{
    private BigInteger p; // Skal være primtal
    private BigInteger q; // Skal være primtal
    private BigInteger lambda;
    private BigInteger publicKey; // Public key eksponent
    private BigInteger privateKey; // Private key eksponent
    private BigInteger n;

    // Constructor
    public KeyPairGenerator(BigInteger p, BigInteger q) throws IllegalArgumentException
    {
        if (p.compareTo(BigInteger.ONE) < 0 || q.compareTo(BigInteger.ONE) < 0)
            throw new IllegalArgumentException("Negative primes are illegal.");

        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        if (n.bitLength() % 8 != 0)
            throw new IllegalArgumentException("Product of given primes is not divisable by 8.");

        this.publicKey = new BigInteger("65537");
        this.lambda = lcm(p.subtract(BigInteger.ONE) , q.subtract(BigInteger.ONE));
        this.privateKey = publicKey.modInverse(lambda);
    }
    public KeyPairGenerator (int keyBitSize)
    {
        this.publicKey = new BigInteger("65537");
        genKeys(keyBitSize);
    }
    private void genKeys(int keyBitsSize)
    {
        boolean check = false;

        while (!check)
        {
            Random rand = new Random();
            this.p = BigInteger.probablePrime(keyBitsSize, rand);
            this.q = BigInteger.probablePrime(keyBitsSize, rand);
            this.n = p.multiply(q);

            if (n.bitLength() % 8 == 0) {
                this.lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
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
    public BigInteger getPublicKey() { return this.n; }
    public BigInteger getPublicE() { return this.publicKey; }
    public BigInteger getPrivateKey() { return this.privateKey; }

}
