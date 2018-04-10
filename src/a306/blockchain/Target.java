package a306.blockchain;

import java.math.BigInteger;

import static a306.blockchain.DifficultyMath.prependZeros;
import static a306.blockchain.DifficultyMath.zeroPadHex;

public class Target {

    private BigInteger bigIntegerTarget;
    private String compactTarget;

    public Target(BigInteger bigIntegerTarget) {
        this.bigIntegerTarget = bigIntegerTarget;
        this.compactTarget = calculateCompactTarget();
    }

    public Target(String compactTarget) {
        this.compactTarget = compactTarget;
        this.bigIntegerTarget = calculateBigIntergerTarget();
    }

    private String calculateCompactTarget() {
        String base256NewTarget = zeroPadHex(bigIntegerTarget.toString(16));

        String firstDigit = base256NewTarget.substring(0, 2);

        if (Integer.valueOf(firstDigit, 16) > 127) {
            base256NewTarget = "00" + base256NewTarget;
        }

        int numberOfDigits = base256NewTarget.length() / 2;

        String compactTarget = zeroPadHex(Integer.toString(numberOfDigits, 16))
                + base256NewTarget.substring(0, Math.min(base256NewTarget.length(), 6)); // Take first 6 digits in base 256 representation.

        compactTarget = prependZeros(compactTarget, 8);

        return compactTarget;
    }

    private BigInteger calculateBigIntergerTarget() {
        int factor = Integer.valueOf(compactTarget.substring(0, 2), 16);
        BigInteger value = new BigInteger(compactTarget.substring(2, compactTarget.length()), 16);

        BigInteger two = new BigInteger("2", 10);
        BigInteger result = value.multiply(two.pow(8 * (factor - 3)));

        return result;
    }

    public BigInteger getBigIntegerTarget() {
        return bigIntegerTarget;
    }

    public String getCompactTarget() {
        return compactTarget;
    }
}
