package blockchain.target;

import org.junit.jupiter.api.Test;
import blockchain.block.Block;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/* Created by Melanie Selman

    AAU-mail: mselma17@student.aau.dk */

class TargetUtilTest {

    // Create lower difficulty
    @Test
    void adjustDifficultyTestExam01() {
        long targetTimespan = 20 * 60 * 1000;

        Target oldTarget = new Target("1e100000");
        Target newTarget = new Target("1e02f79f");

        long oldTimestamp = 1528893491657L;
        long newTimestamp = 1528893714203L;


        BigDecimal factor = new BigDecimal(newTarget.getBigIntegerTarget())
                .divide(new BigDecimal(oldTarget.getBigIntegerTarget()), 4, RoundingMode.HALF_UP);

        BigDecimal expectedFactor = new BigDecimal((double) (newTimestamp - oldTimestamp) / (double) targetTimespan)
                .divide(new BigDecimal(1), 4, RoundingMode.HALF_UP);

        assertEquals(expectedFactor, factor);
    }

    // Create lower difficulty
    @Test
    void adjustDifficultyTest01() {

        Block b1 = new Block("hash", "prevHash", "1a00ffff", 38100,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1a00ffff", 52400,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 20 * 60 * 1000 * 2, 2, null);

        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();
        BigInteger oldTarget = new Target(b1.getCompactTarget()).getBigIntegerTarget();

        BigDecimal factor = new BigDecimal(newTarget).divide(new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP);
        BigDecimal expectedFactor = new BigDecimal("2").divide(new BigDecimal(1), 4, RoundingMode.HALF_UP);

        assertEquals(expectedFactor, factor);
    }

    // Create higher difficulty
    @Test
    void adjustDifficultyTest02() {

        Block b1 = new Block("hash", "prevHash", "1a00ffff", 38100,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1a00ffff", 52400,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", (20 * 60 * 1000) / 2, 2, null);

        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();
        BigInteger oldTarget = new Target(b1.getCompactTarget()).getBigIntegerTarget();

        BigDecimal factor = new BigDecimal(newTarget).divide(new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP);
        BigDecimal expectedFactor = new BigDecimal("0.5").divide(new BigDecimal(1), 4, RoundingMode.HALF_UP);

        assertEquals(expectedFactor, factor);
    }

    // Hit the exact target
    @Test
    void adjustDifficultyTest03() {

        Block b1 = new Block("hash", "prevHash", "1a00ffff", 38100,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1a00ffff", 52400,
                "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 20 * 60 * 1000, 2, null);

        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();
        BigInteger oldTarget = new Target(b1.getCompactTarget()).getBigIntegerTarget();

        BigDecimal factor = new BigDecimal(newTarget).divide(new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP);
        BigDecimal expectedFactor = new BigDecimal("1").divide(new BigDecimal(1), 4, RoundingMode.HALF_UP);

        assertEquals(expectedFactor, factor);
    }
}

