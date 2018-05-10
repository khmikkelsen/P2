package robin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/* Created by Melanie Selman

    AAU-mail: mselma17@student.aau.dk */

     class DifficultyMathTest {

         @Test
         void appendZeroes() {

             assertEquals("F000000000",  DifficultyMath.appendZeros("F",10));

         }


         @Test
         void zeroPadHex1() {

             assertEquals("0F", DifficultyMath.zeroPadHex("F"));

         }

         @Test
         void zeroPadHex2() {

             assertEquals("0F", DifficultyMath.zeroPadHex("0F"));

         }
     }