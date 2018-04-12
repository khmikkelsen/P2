package A306.rsa;

import java.math.BigInteger;
import java.util.Random;

public class KeyPair
{
    private BigInteger p; // Skal være primtal
    private BigInteger q; // Skal være primtal
    private BigInteger lambda;
    private BigInteger publicKey; // Public key eksponent
    private BigInteger privateKey; // Private key eksponent
    private BigInteger n;

    // Constructor
    public KeyPair(BigInteger p, BigInteger q) throws IllegalArgumentException
    {
        if (p.compareTo(BigInteger.ONE) < 0 || q.compareTo(BigInteger.ONE) < 0)
            throw new IllegalArgumentException("Negative primes are illegal.");

        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        this.lambda = lcm(p.subtract(BigInteger.ONE) , q.subtract(BigInteger.ONE));
        this.publicKey = genPublic();
        this.privateKey = genPrivate(publicKey, lambda);
    }

    // Public key generation, som finder et tal som er co-prime med lambda = (lcm(p-1,q-1).
    private BigInteger genPublic()
    {
        Random rand = new Random();
        boolean coPrimeCheck = false;

        BigInteger e = BigInteger.probablePrime(1024, rand);

        while (!coPrimeCheck) {

            if (e.gcd(lambda).equals(BigInteger.ONE))// co-prime fundet hvis 1: et primtal, 2: gcd er 1 med lambda.
                coPrimeCheck = true;
            else
            {
                rand = new Random();
                e = BigInteger.probablePrime(1024, rand);
            }
        }

        return e; // e er den tilhørende public key eksponent.
    }

    // lcm (least common multiple) ved reduktion af greatest common divisor (gcd).
    private BigInteger lcm(BigInteger a, BigInteger b)
    {
        BigInteger AGcdB = a.gcd(b);
        BigInteger ATimesB = a.multiply(b);

        return ATimesB.divide(AGcdB);
    }

    // Genererer private key.
    private BigInteger genPrivate(BigInteger x, BigInteger y)
    {
        BigInteger roots[] = new BigInteger[2];

        roots = egcd(x, y, roots);
        return (roots[0].multiply(x).add(roots[1].multiply(y))).equals(BigInteger.ONE) ? roots[1] : roots[0]; // Returnerer den af de to rødder, som opfylder ax+by = gcd(e,lambda) = 1.
    }

    // Modular multiplikativ inverse ved hjælp af Euclid's Extended Algoritme.
    private BigInteger[] egcd(BigInteger x, BigInteger y, BigInteger[] roots)
    {
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE, prevA = BigInteger.ONE,
                   prevB = BigInteger.ZERO, temp, quotient, remainder;

        while(y.compareTo(BigInteger.ZERO) != 0)
        {
            quotient = x.divide(y);
            remainder = x.mod(y);

            x = y;
            y = remainder;

            temp = a;
            a = prevA.subtract(quotient.multiply(a));
            prevA = temp;

            temp = b;
            b = prevB.subtract(quotient.multiply(b));
            prevB = temp;
        }

        roots[0] = prevA;
        roots[1] = prevB;

        return roots;
    }

    // Getters
    public BigInteger getPublicKey() { return this.n; }
    public BigInteger getPublicE() { return this.publicKey; }
    public BigInteger getPrivateKey() { return this.privateKey; }
}
