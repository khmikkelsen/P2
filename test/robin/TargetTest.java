package robin;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;


//TODO: Spørg Mads

class TargetTest {



    @Test
    void calculateCompactTargetTest01() {

        BigInteger num = new BigInteger("1000", 10);

        String compactTarget = Target.calculateCompactTarget(num);

        assertEquals("0203e800", compactTarget);


    }

    @Test
    void calculateCompactTargetTest02() {

        BigInteger num = new BigInteger("1000", 10);

        String compactTarget = Target.calculateCompactTarget(num);


        assertNotEquals("0203e800h", Target.calculateCompactTarget(num));

    }


    @Test
    void calculateCompactTargetTest03() {

        BigInteger num = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffff",16);

        String compactTarget = Target.calculateCompactTarget(num);

        assertEquals("1d00ffff", Target.calculateCompactTarget(num));


    }

    @Test
    void calculateCompactTargetTest04() {

        BigInteger num = new BigInteger("65535",10);

        String compactTarget = Target.calculateCompactTarget(num);

        assertEquals("0300ffff", Target.calculateCompactTarget(num));


    }




    @Test
    void calculateBigIntegerTarget01() {

        String compactTarget = "180130E0";

        BigInteger num1 = new BigInteger("130E0000000000000000000000000000000000000000000", 16);

        BigInteger num2 = Target.calculateBigIntergerTarget(compactTarget);

        assertEquals(num1, num2);



    }
}