package a306.blockchain;

import java.math.BigInteger;

public class DifficultyMath {


    /*
     *
     */
    public static String appendZeros(String hexValue, int length) {
        while (hexValue.length() < length) {
            hexValue += "0";
        }

        return hexValue;
    }

    /*
     * The function appendZeros: is used for prepending(adding leading) zero to a hex number less
     * than a spesified length; in this case, if not a multiple of 2; for use when converting into base256
     */
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
