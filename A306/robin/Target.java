package robin;

import java.math.BigInteger;

/**
 * This class is for Target conversion. From compact to base256 and vice versa.
 *
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
     *The function calculateCompactTarget: First) uses toString to make the BigInt base256; is then
     * represented as a base16 string.
     *. Also, zeroPadHex is used to check if hex string length is a multiple of two, else a leading zero is added;
     *
     *Second) extracts the first two hex digits and puts in firstDigit. If the two digits have a value greater than
     *127, then add(prepend) a base256  zero digit
     *
     * Third) check length of the base256 number;the length of number is then added as first digit of the compact number.
     * Fourth) Check length of compact number, bust be 4 base256 digits or 8 hex digits
     */
    public static String calculateCompactTarget(BigInteger bigIntegerTarget) {
        // This is a hex string, which is also a base 256 string if you add a space after
        // each set of 2 hex digits.
        String base256NewTarget = HexUtil.zeroPadHex(bigIntegerTarget.toString(16));

        // One base 256 digit is two base 16 (hex) digits.
        String firstBase256Digit = base256NewTarget.substring(0, 2); //range is exclusive

        if (Integer.valueOf(firstBase256Digit, 16) > 127) {
            base256NewTarget = "00" + base256NewTarget;
        }

        int numberOfBase256Digits = base256NewTarget.length() / 2;

        // Take first 6 hex digits
        String compactTarget = HexUtil.zeroPadHex(Integer.toString(numberOfBase256Digits, 16))
                + base256NewTarget.substring(0, Math.min(base256NewTarget.length(), 6));

        compactTarget = HexUtil.appendZeros(compactTarget, 8); // compact target must be at least 4 base256 digits

        return compactTarget;
    }

    /**
     * The function calculateBigIntegerTarget: uses compactTargets first digit value to find length of the BigInt
     * representation. Then, gets the value of rest of digits. This data is used in a formula to convert compact form
     * into a BigInteger target - which is needed to compare zeros.
     *
     */
    public static BigInteger calculateBigIntergerTarget(String compactTarget) {
        int factor = Integer.valueOf(compactTarget.substring(0, 2), 16);
        BigInteger value = new BigInteger(compactTarget.substring(2, compactTarget.length()), 16);

        BigInteger two = new BigInteger("2", 10);
        BigInteger bigIntegerTarget = value.multiply(two.pow(8 * (factor - 3)));

        return bigIntegerTarget;
    }

    public BigInteger getBigIntegerTarget() {
        return bigIntegerTarget;
    }

    public  String getCompactTarget() {
        return compactTarget;
    }
}
