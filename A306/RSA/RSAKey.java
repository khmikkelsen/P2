package RSA;

import org.bouncycastle.asn1.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

public class RSAKey {
    private BigInteger n;
    private BigInteger exponent;

    public RSAKey(BigInteger n, BigInteger exponent) {
        this.n = n;
        this.exponent = exponent;
    }

    public RSAKey(String base64Key) throws InvalidRSAKeyException, IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Key);

        try (ASN1InputStream input = new ASN1InputStream(decodedBytes)) {
            ASN1Sequence sequence = (ASN1Sequence) input.readObject();
            ASN1Integer modulus = ASN1Integer.getInstance(sequence.getObjectAt(0));
            ASN1Integer exponent = ASN1Integer.getInstance(sequence.getObjectAt(1));

            this.n = modulus.getValue();
            this.exponent = exponent.getValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidRSAKeyException("The Base64 string does not contain a modulus and an exponent");
        }
    }


    public BigInteger getModulus() {
        return n;
    }

    public BigInteger getExponent() {
        return exponent;
    }

    @Override
    public String toString() {
        ASN1Integer modulus = new ASN1Integer(n);
        ASN1Integer exponent = new ASN1Integer(this.exponent);

        ASN1Encodable[] encodables = new ASN1Encodable[]{modulus, exponent};

        DERSequence sequence = new DERSequence(encodables);
        byte[] sequenceBytes;

        try {
            sequenceBytes = sequence.getEncoded();
            String base64 = Base64.getEncoder().encodeToString(sequenceBytes);

            return base64;
        } catch (IOException e) {
            return null;
        }
    }
//
//    @Override
//    public String toString() {
//        return n.toString(16) + "-\n" + exponent.toString(16);
//    }
}
