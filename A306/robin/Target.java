package robin;

import java.math.BigInteger;

/**
 * This class is for Target conversion. From compact to base256 and vice versa.
 */

public class Target {

    private BigInteger bigIntegerTarget;
    private String compactTarget;

    //Constructor: for big to compact
    public Target(BigInteger bigIntegerTarget) {
        this.bigIntegerTarget = bigIntegerTarget;
        this.compactTarget = calculateCompactTarget(bigIntegerTarget);
    }

    //constructor for compact to big
    public Target(String compactTarget) {
        this.compactTarget = compactTarget;
        this.bigIntegerTarget = calculateBigIntergerTarget(compactTarget);
    }

    /**
     * The function calculateCompactTarget: First) uses toString to make the BigInt base256; is then
     * represented as a base16 string.
     * . Also, zeroPadHex is used to check if hex string length is a multiple of two, else a leading zero is added;
     * <p>
     * Second) extracts the first two hex digits and puts in firstDigit. If the two digits have a value greater than
     * 127, then add(prepend) a base256  zero digit
     * <p>
     * Third) check length of the base256 number;the length of number is then added as first digit of the compact number.
     * Fourth) Check length of compact number, bust be 4 base256 digits or 8 hex digits
     */
    public static String calculateCompactTarget(BigInteger bigIntegerTarget) {
        // This is a hex string, which is also a base 256 string if you add a space after
        // each set of 2 hex digits.
        String base256 = bigIntegerTarget.toString(16);

        int exponent = (base256.length() + 2 - 1) / 2;

        // Take first 3 base 256 hex digits
        String compactTarget = HexUtil.zeroPadHex(Integer.toString(exponent, 16))
                + HexUtil.zeroPadHex(base256.substring(0, Math.min(base256.length(), 6)));

        return HexUtil.appendZeros(compactTarget, 8); // compact target must be 4 base 256 digits.
    }

    /**
     * The function calculateBigIntegerTarget: uses compactTargets first digit value to find length of the BigInt
     * representation. Then, gets the value of rest of digits. This data is used in a formula to convert compact form
     * into a BigInteger target - which is needed to compare zeros.
     */
    public static BigInteger calculateBigIntergerTarget(String compactTarget) {
        if (compactTarget.length() != 8) {
            throw new IllegalArgumentException("A compact target is 8 hex digits long");
        }

        int exponent = Integer.valueOf(compactTarget.substring(0, 2), 16);

        // Take the fewest number of digits (minimum of "exponent bytes" and all remaining digits), e.g. 01003456 -> 00
        String significandHex = compactTarget.substring(2, Math.min(2 + exponent * 2, compactTarget.length()));
        BigInteger significand = new BigInteger(significandHex, 16);

        BigInteger base = new BigInteger("256", 10);

        // Convert to bytes (2 digits = 1 byte).
        int significandByteLength = significandHex.length() / 2;
        BigInteger bigIntegerTarget = significand.multiply(base.pow(exponent - significandByteLength));

        return bigIntegerTarget;
    }

    public BigInteger getBigIntegerTarget() {
        return bigIntegerTarget;
    }

    public String getCompactTarget() {
        return compactTarget;
    }
}
