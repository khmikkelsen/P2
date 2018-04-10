package a306.blockchain;

import java.math.BigInteger;

public class DifficultyMath {



    public static String prependZeros(String hexValue, int length) {
        while (hexValue.length() < length) {
            hexValue += "0";
        }

        return hexValue;
    }

    public static String zeroPadHex(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }

    public static String zeroPadHex(int number) {
        String res = Integer.toString(number, 16);

        if (res.length() % 2 != 0) {
            res = "0" + res;
        }

        return res;
    }
}
