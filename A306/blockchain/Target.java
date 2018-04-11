package blockchain;

import java.math.BigInteger;

public class Target {

    private BigInteger bigIntegerTarget;
    private String compactTarget;

    //Constructor: for big to compact
    public Target(BigInteger bigIntegerTarget) {
        this.bigIntegerTarget = bigIntegerTarget;
        this.compactTarget = calculateCompactTarget();
    }

    //constructor for compact to big
    public Target(String compactTarget) {
        this.compactTarget = compactTarget;
        this.bigIntegerTarget = calculateBigIntergerTarget();
    }

    /*
     *The function calculateCompactTarget: First) makes the target into base256;uses toString to make the BigInt
     *a base16 string. Also, zeroPadHex is used to check if hex string length is a multiple of two, else a leading zero is added;
     *this is for conversion into base256.
     *
     *Second) extracts the first two hex digits and puts in firstDigit. If the two digits have a value greater than
     *127, then add a base256  zero digit.
     *
     * Third) check length of the base256 number;the length of number is then added as first digit of number.
     */
    private String calculateCompactTarget() {
        String base256NewTarget = DifficultyMath.zeroPadHex(bigIntegerTarget.toString(16));

        String firstDigit = base256NewTarget.substring(0, 2);

        if (Integer.valueOf(firstDigit, 16) > 127) {
            base256NewTarget = "00" + base256NewTarget;
        }

        int numberOfDigits = base256NewTarget.length() / 2;

        // Take first 6 digits in base256 representation.
        String compactTarget = DifficultyMath.zeroPadHex(Integer.toString(numberOfDigits, 16))
                + base256NewTarget.substring(0, Math.min(base256NewTarget.length(), 6));

        compactTarget = DifficultyMath.appendZeros(compactTarget, 8);

        return compactTarget;
    }

    /*
     *The function calculateBigIntegerTarget: uses compactTargets firt digit value to find length of the BigInt
     *representation. Then, gets the value of rest of digits. This data is used in a formula to convert compact form
     *into a BigInteger target.
     *
     */
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
