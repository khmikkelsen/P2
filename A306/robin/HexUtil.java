package robin;

public class HexUtil {


    /**
     * The function appendZeros is used to append zeros to a hex value less than a
     * specified length
     * @param hexValue String for hex value needing padding
     * @param length int for specifying intended length
     * @return a padded hex value
     */
    public static String appendZeros(String hexValue, int length) {
        while (hexValue.length() < length) {
            hexValue += "0";
        }

        return hexValue;
    }

    public static String prependZeros(String hexValue, int length) {
        while (hexValue.length() < length) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }

    /**
     * The function ZeroPadHex is used to prepend a zero for numbers with digits that are not a multiple of 2.
     * @param hexValue String to add padding for a hex value
     * @return padded hex value
     */
    public static String zeroPadHex(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }
}
