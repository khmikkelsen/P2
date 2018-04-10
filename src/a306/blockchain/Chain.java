package a306.blockchain;

import java.math.BigInteger;

public class Chain {
    private static final long targetTimespan = 14 * 24 * 60 * 60; // Two weeks (in seconds).

    private static final BigInteger proofOfWorkLimit = new BigInteger("00000000FFFF0000000000000000000000000000000000000000000000000000", 16); // Highest target (difficulty 1)
    private static Target chainTarget = new Target(proofOfWorkLimit);

    void adjustDifficulty() {

        Block lastBlock = new Block("", null);
        Block firstBlock = new Block("", null); // Find block that is lastBlock - 2016.


        System.out.println("pow limit: " + proofOfWorkLimit.toString(16));


        long actualTimespan = lastBlock.getTimestamp() - firstBlock.getTimestamp();

        actualTimespan = targetTimespan / 2;

//        if (actualTimespan < targetTimespan / 4) {
//            actualTimespan = targetTimespan / 4;
//        } else if (actualTimespan > targetTimespan * 4) {
//            actualTimespan = targetTimespan * 4;
//        }



        BigInteger newBigIntegerTarget = new Target(lastBlock.getCompactDifficulty()).getBigIntegerTarget();

        newBigIntegerTarget = newBigIntegerTarget.multiply(BigInteger.valueOf(actualTimespan));
        newBigIntegerTarget = newBigIntegerTarget.divide(BigInteger.valueOf(targetTimespan));


        Target newTarget = new Target(newBigIntegerTarget);
//        System.out.println("New target in hex: " + newTarget.getBigIntegerTarget().toString(16));
//        System.out.println("New target in compact form: " + newTarget.getCompactTarget());


    }
}
