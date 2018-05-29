package blockchain.utils;

import org.junit.jupiter.api.Test;
import blockchain.utils.StringUtil;

import static org.junit.jupiter.api.Assertions.*;

/* Created by Melanie Selman

    AAU-mail: mselma17@student.aau.dk */

class StringUtilTest {


    @Test
    void applySha265Test01() {
        String str = "a b c d e f g h i j k l m n o p q r s t u v w x y z æ ø å !\"#¤%&/()=?`";

        String hash = "7a36eab637f03c29aa3b0896049534ee949f4a7ac81fdeb99e9a440972139db2";

        assertEquals(hash, StringUtil.applySha256(str));

    }

    @Test
    void applySha265Test02() {
        String str = "b c d e f g h i j k l m n o p q r s t u v w x y z æ ø å !\"#¤%&/()=?`";

        String hash = "7a36eab637f03c29aa3b0896049534ee949f4a7ac81fdeb99e9a440972139db2";

        assertNotEquals(hash, StringUtil.applySha256(str));
    }
}