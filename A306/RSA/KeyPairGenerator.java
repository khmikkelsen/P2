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
        System.out.println("N byte length: "+n.bitLength());
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
            this.lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
            this.privateKey = publicKey.modInverse(lambda);

            check = testKeys();
        }

    }
    private boolean testKeys()
    {
        boolean check = false;
        boolean check2 = false;

        BigInteger testInt = new BigInteger("5000");
        BigInteger testC = testInt.modPow(publicKey,n);
        BigInteger testM = testC.modPow(privateKey,n);

        if (testInt.equals(testM))
            check = true;

        testInt = new BigInteger("5000");
        testC = testInt.modPow(privateKey,n);
        testM = testC.modPow(publicKey,n);

        if (testInt.equals(testM))
            check2 = true;

        boolean temp = check & check2;

        return check & check2;
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
