package robin;

import java.math.BigInteger;


/**
 * The class chain: is used to adjust target difficulty and generate new target so that the miners can create
 * a block for the blockchain.
 */
public class TargetUtil {

    private static int targetAdjustInterval = 20;

    // Two weeks (in seconds);wanted time for 20 blocks = 20 minutes.
    private static final long targetTimespan = 20 * 60 * 1000;

    /*BigInteger is used for large Integers, i.e greater than 64-bit
     * The BigInteger constructor: takes the string representation of a big int and the base(radix) to make a BigInteger
     * The proofOfWorkLimit : is maximum target, and is what difficulty 1 sets the hash block
     * target as. (the bits field  is compact difficulty)
     */
//    private static final String limit = new Target(new BigInteger("00000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)).getCompactTarget();
    private static final Target proofOfWorkLimit = new Target(new BigInteger("0000100000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)); // Very high target (difficulty less than 1).
    //private static final Target proofOfWorkLimit = new Target("00000000FFFF0000000000000000000000000000000000000000000000000000"); // Highest target (difficulty 1)

    public static long getTargetAdjustInterval() {
        return targetAdjustInterval;
    }


    public static Target getProofOfWorkLimit() {
        return proofOfWorkLimit;
    }

    /**
     * The function adjustTarget: adjusts the difficulty based on 'if the previous 2016 blocks took more than two
     * weeks to find, the difficulty is reduced. If they took less than two weeks, the difficulty is increased. '
     */
    public static Target adjustTarget(Block endPeriodBlock, Block startPeriodBlock) {
        //Find the actual time it took for the 20 blocks to be generated??
        long actualTimespan = endPeriodBlock.getTimestamp() - startPeriodBlock.getTimestamp();

//        if (actualTimespan < targetTimespan / 4) {
//            actualTimespan = targetTimespan / 4;
//        } else if (actualTimespan > targetTimespan * 4) {
//            actualTimespan = targetTimespan * 4;
//        }

        double factor = (double) actualTimespan / (double) targetTimespan;

        BigInteger newBigIntegerTarget = new Target(endPeriodBlock.getCompactTarget()).getBigIntegerTarget();

        //Calculate new target
        newBigIntegerTarget = newBigIntegerTarget.multiply(BigInteger.valueOf(actualTimespan));
        newBigIntegerTarget = newBigIntegerTarget.divide(BigInteger.valueOf(targetTimespan));

        // Don't allow target to exceed limit.
        if (newBigIntegerTarget.compareTo(proofOfWorkLimit.getBigIntegerTarget()) > 0) {
            newBigIntegerTarget = proofOfWorkLimit.getBigIntegerTarget();
        }

        //New target for nodes; hash must be less than target
        return new Target(newBigIntegerTarget);
    }
}
