package rsa;

import org.bouncycastle.asn1.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Objects;

/**
 * RSAKey
 * Class to be used for a key, containing rsa modulus n and the exponent.
 * Has base64 string from ASN.1 standard.
 * Constructors allow from either BigInteger n and exponent or from a base64 string in ASN.1 standard.
 */
public class RSAKey {
    private BigInteger n;
    private BigInteger exponent;
    private String base64Key;

    /**
     * Constructor for
     * @param n rsa modulus
     * @param exponent rsa exponent
     * @throws IOException caused by IOException in calculateBase64String
     */
    public RSAKey(BigInteger n, BigInteger exponent) throws IOException {
        this.n = n;
        this.exponent = exponent;
        this.base64Key = calculateBase64String();
    }

    /**
     *
     * @param base64Key Takes a base64 in ASN.1 standard string,
     *                 and decodes it to an RSAKey with BigIntegers rsa modulus and exponent
     * @throws InvalidRSAKeyException If base64 string cannot be converted, throws an InvalidRSAKeyException.
     * @throws IOException IOException caused by ASN.1 conversion.
     */
    public RSAKey(String base64Key) throws InvalidRSAKeyException, IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Key);

        try (ASN1InputStream input = new ASN1InputStream(decodedBytes)) {
            ASN1Sequence sequence = (ASN1Sequence) input.readObject();
            ASN1Integer modulus = ASN1Integer.getInstance(sequence.getObjectAt(0));
            ASN1Integer exponent = ASN1Integer.getInstance(sequence.getObjectAt(1));

            this.n = modulus.getValue();
            this.exponent = exponent.getValue();
            this.base64Key = base64Key;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidRSAKeyException("The Base64 string does not contain a modulus and an exponent");
        }
    }

    private String calculateBase64String() throws IOException {
        ASN1Integer modulus = new ASN1Integer(n);
        ASN1Integer exponent = new ASN1Integer(this.exponent);

        ASN1Encodable[] encodables = new ASN1Encodable[]{modulus, exponent};

        DERSequence sequence = new DERSequence(encodables);
        byte[] sequenceBytes;

        sequenceBytes = sequence.getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(sequenceBytes);

        return base64Key;
    }
    /*
     * Getters
     */
    public BigInteger getModulus() {
        return n;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    public String getBase64String() {
        return base64Key;
    }

    @Override
    public String toString() {
        return n.toString(16) + "-\n" + exponent.toString(16);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSAKey rsaKey = (RSAKey) o;
        return Objects.equals(n, rsaKey.n) &&
                Objects.equals(exponent, rsaKey.exponent) &&
                Objects.equals(base64Key, rsaKey.base64Key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(n, exponent, base64Key);
    }
}
