package robin;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/* Created by Melanie Selman

    AAU-mail: mselma17@student.aau.dk */

class TargetUtilTest {


    // Create lower difficulty

    @Test
    void adjustDifficultyTest01() {

        Block b1 = new Block("hash", "prevHash", "1f00ffff", 38100, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1f00ffff", 52400, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 2419200000L, 2, null);


        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();

        BigInteger oldTarget = new BigInteger("0000ffffffff0000000000000000000000000000000000000000000000000000", 16);

        BigDecimal factor = new BigDecimal(newTarget).divide(
                new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP
        );

        BigDecimal expectedFactor = new BigDecimal("2");

        assertTrue(expectedFactor.compareTo(factor) == 0);

    }


    // Create higher difficulty

    @Test
    void adjustDifficultyTest02() {

        Block b1 = new Block("hash", "prevHash", "1f00ffff", 38100, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1f00ffff", 52400, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 604800000L, 2, null);

        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();

        BigInteger oldTarget = new BigInteger("0000ffffffff0000000000000000000000000000000000000000000000000000", 16);

        BigDecimal factor = new BigDecimal(newTarget).divide(
                new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP
        );

        BigDecimal expectedFactor = new BigDecimal("0.5");

        assertTrue(expectedFactor.compareTo(factor) == 0);

    }


    // Hit the exact target

    @Test
    void adjustDifficultyTest03() {

        Block b1 = new Block("hash", "prevHash", "1f00ffff", 38100, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 0L, 2, null);
        Block b2 = new Block("hash", "prevHash", "1f00ffff", 52400, "484402e866a9e0ed173b84ab975374df612e0f6b74afd00da945a0b9faab25d3", 1209600000L, 2, null);

        BigInteger newTarget = TargetUtil.adjustTarget(b2, b1).getBigIntegerTarget();

        BigInteger oldTarget = new BigInteger("0000ffffffff0000000000000000000000000000000000000000000000000000", 16);

        BigDecimal factor = new BigDecimal(newTarget).divide(
                new BigDecimal(oldTarget), 4, RoundingMode.HALF_UP
        );

        BigDecimal expectedFactor = new BigDecimal("1");

        assertTrue(expectedFactor.compareTo(factor) == 0);


    }
}

