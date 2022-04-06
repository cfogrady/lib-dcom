package com.github.cfogrady.dcom.digimon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckSumCalculatorTest {
    @Test
    void testThatChecksumMatchesDM20Sample() {
        ChecksumCalculator checksumCalculator = new ChecksumCalculator();
        checksumCalculator.addToChecksum(0b0010000100011100); // 1
        checksumCalculator.addToChecksum(0b0000110100101011); // 2
        checksumCalculator.addToChecksum(0b0000001000011110); // 3
        checksumCalculator.addToChecksum(0b0000000100001110); // 4
        checksumCalculator.addToChecksum(0b0000110110011110); // 5
        checksumCalculator.addToChecksum(0b0000010010111110); // 6
        checksumCalculator.addToChecksum(0b0000000101011110); // 7
        checksumCalculator.addToChecksum(0b0000110110101110); // 8
        checksumCalculator.addToChecksum(0b0010010001101110); // 9
        checksumCalculator.addNibbleToChecksum(0); // A Dodges
        checksumCalculator.addNibbleToChecksum(0b1111); // A Hits
        checksumCalculator.addNibbleToChecksum(0b1110 ); // A EOL
        Assertions.assertEquals(5, checksumCalculator.valueToAchieveRemainder(0));
    }
}

