import java.util.Random;

public class KeyPairClean
{
    private int p; // Skal være primtal
    private int q; // Skal være primtal
    private int lambda;
    private int publicKey; // Public key eksponent
    private int privateKey; // Private key eksponent
    private int n = p*q;

    public KeyPairClean(int p, int q)
    {
        this.p = p;
        this.q = q;
        this.lambda = lcm(p - 1, q - 1);
        this.publicKey = genPublic(p,q);
        this.privateKey = genPrivate(publicKey, lambda);
    }

    /* Public key generation, finder et tal som er co-prime med lambda = (lcm(p-1,q-1) */
    public int genPublic (int p, int q)
    {
        Random rand = new Random();

        int e = rand.nextInt(lambda-1) + 2; // rand e = 1 < e < lambda

        while (1 < e & e < lambda) {

            if (isPrime(e) & gcd(e, lambda) == 1) // co-prime fundet hvis 1: et primtal, 2: gcd er 1 med lambda
                break;
            else
                e = rand.nextInt(lambda-1) + 2;
        }

        return e; // e er den tilhørende public key eksponent
    }
    /* lcm (least common multiple) ved reduktion af greatest common divisor (gcd) */
    private int lcm(int a, int b)
    {
        return (a / gcd(a,b)) * b;
    }
    /* Rekursiv gcd funktion */
    private int gcd(int x, int y)
    {
        System.out.print("gcd("+x+","+y+")\n");

        if ( y == 0 ) {
            return x;
        }
        else if ( x >= y && y > 0)
            return gcd(y, x % y); // Hvis parametre er korrekt, kald funktion igen, med y og rest af x og y
        else
            return gcd(y, x); // Hvis y er større end x, kald funktion igen bare omvendt
    }
    /* >>kan effektiviseres<< Tjekker om et tal er primtal */
    private boolean isPrime (int n)
    {
        for (int i = 2; i < n; i++)
        {
            if (n % i == 0)
                return false;
        }
        return true;
    }
    private int genPrivate(int x, int y)
    {
        int roots[] = new int[2];

        roots = egcd(x, y, roots);

        return (roots[0] * x) + (roots[1] * y) == 1 ? roots[1] : roots[0]; // returnerer den af de to rødder, som opfylder
                                                                           // ax+by = gcd(e,lambda) = 1
    }

    /* Modular multiplikativ inverse ved hjælp af Euclid's Extended Algoritme */
    public int[] egcd (int x, int y, int[] roots)
    {
        int a = 0, b = 1, prevA = 1, prevB = 0, temp, quotient, remainder;

        while(y != 0)
        {
            quotient = x / y;
            remainder = x % y;

            x = y;
            y = remainder;

            temp = a;
            a = prevA - quotient * a;
            prevA = temp;

            temp = b;
            b = prevB - quotient * b;
            prevB = temp;
        }

        roots[0] = prevA;
        roots[1] = prevB;

        return roots;
    }
    /* one legal boi 8)) */
    public int getPublicKey() {
        return publicKey;
    }
    /* one forbidden boi 8)) */
    public int getPrivateKey() {
        return privateKey;
    }

    /* one forbidden boi 8)) */
    public int getLambda() {
        return lambda;
    }
}
