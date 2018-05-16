package RSA;

import java.math.BigInteger;

public class RSAKey
{
    private BigInteger n;
    private BigInteger exponent;

    public RSAKey(BigInteger n, BigInteger exponent)
    {
        this.n = n;
        this.exponent = exponent;
    }

    public RSAKey(String key) {
        // TODO: Convert String to modulus and exponent.
    }


    public BigInteger getRSAMod() { return n; }
    public BigInteger getExponent() { return exponent; }

    @Override
    public String toString() {
        return n.toString() +"-\n"+exponent.toString();
    }
}
