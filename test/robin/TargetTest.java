package robin;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;


class TargetTest {


    // Calculate compact target from the biginteger 1000
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

        assertNotEquals("0203e800h", Target.calculateCompactTarget(num));

    }



    // Calculate the compact target from a hex value
    @Test
    void calculateCompactTargetTest03() {

        BigInteger num = new BigInteger("130E0000000000000000000000000000000000000000000", 16);

        String compactTarget = Target.calculateCompactTarget(num);

        assertEquals("180130e0", Target.calculateCompactTarget(num));


    }


    // Calculate compact target from the biginteger 65535
    @Test
    void calculateCompactTargetTest04() {

        BigInteger num = new BigInteger("65535", 10);

        String compactTarget = Target.calculateCompactTarget(num);

        assertEquals("0300ffff", Target.calculateCompactTarget(num));

    }


    // Calculate biginteger target from a compact target
    @Test
    void calculateBigIntegerTarget01() {

        String compactTarget = "180130E0";

        BigInteger num1 = new BigInteger("130E0000000000000000000000000000000000000000000", 16);

        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);

        assertEquals(num1, num2);

    }


    /* Use the same values as in calculateCompactTargetTest04, and see if
    * the same results will appear when we calculate the biginteger target
    * from the compact target.*/
    @Test
    void calculateBigIntegerTarget02() {

        String compactTarget = "0300ffff";

        BigInteger num1 = new BigInteger("65535", 10);

        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);

        assertEquals(num1, num2);

    }


    /*
    * Use the same values as in calculateCompactTargetTest01. In this case
    * an ArithmeticException will be thrown, because the exponent will
    * become negative.*/
    @Test
    void calculateBigIntegerTarget03() {

        assertThrows(ArithmeticException.class, () -> {

            String compactTarget = "0203e800";

            Target.calculateBigIntergerTarget(compactTarget);

        });

    }

}