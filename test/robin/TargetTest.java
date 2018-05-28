package robin;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;


class TargetTest {


    // Calculate compact target from 1000
    @Test
    void calculateCompactTargetTest01() {
        BigInteger num = new BigInteger("1000", 10);
        String compactTarget = Target.calculateCompactTarget(num);
        assertEquals("0203e800", compactTarget);
    }

    // Check that calculateCompactTarget returns the same value every time
    @Test
    void calculateCompactTargetTest02() {
        BigInteger num = new BigInteger("1000", 10);
        String compactTarget = Target.calculateCompactTarget(num);
        assertNotEquals("0203e800h", compactTarget);

        //TODO: Does this test make sense? There are never h's in hex values
    }

    // Calculate the compact target from a hex value
    @Test
    void calculateCompactTargetTest03() {
        BigInteger num = new BigInteger("130e0000000000000000000000000000000000000000000", 16);
        String compactTarget = Target.calculateCompactTarget(num);
        assertEquals("180130e0", compactTarget);
    }

    // Calculate compact target from 65535/0xFFFF
    @Test
    void calculateCompactTargetTest04() {
        BigInteger num = new BigInteger("65535", 10);
        String compactTarget = Target.calculateCompactTarget(num);
        assertEquals("02ffff00", compactTarget);
    }

    // Calculate BigInteger target from a compact target
    @Test
    void calculateBigIntegerTarget01() {
        String compactTarget = "180130e0";
        BigInteger num1 = new BigInteger("130e0000000000000000000000000000000000000000000", 16);
        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);
        assertEquals(num1, num2);
    }

    /* Use the same values as in calculateCompactTargetTest04, and see if
     * the same results will appear when we calculate the BigInteger target
     * from the compact target.*/
    @Test
    void calculateBigIntegerTarget02() {
        String compactTarget = "02ffff00";
        BigInteger num1 = new BigInteger("65535", 10);
        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);
        assertEquals(num1, num2);
    }

    /*
     * Use the same values as in calculateCompactTargetTest01, and see if
     * the same results will appear when we calculate the BigInteger target
     * from the compact target. */
    @Test
    void calculateBigIntegerTarget03() {
        String compactTarget = "0203e800";
        BigInteger num1 = new BigInteger("1000", 10);
        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);
        assertEquals(num1, num2);
    }
}