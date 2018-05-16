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

    public BigInteger getRSAMod() { return n; }
    public BigInteger getExponent() { return exponent; }

    public String toString() {
        return n.toString() +"-\n"+exponent.toString();
    }
}
