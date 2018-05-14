package robin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/* Created by Melanie Selman

    AAU-mail: mselma17@student.aau.dk */

     class HexUtilTest {

         @Test
         void appendZeroes() {

             assertEquals("F000000000",  HexUtil.appendZeros("F",10));

         }


         @Test
         void zeroPadHex1() {

             assertEquals("0F", HexUtil.zeroPadHex("F"));

         }

         @Test
         void zeroPadHex2() {

             assertEquals("0F", HexUtil.zeroPadHex("0F"));

         }
     }