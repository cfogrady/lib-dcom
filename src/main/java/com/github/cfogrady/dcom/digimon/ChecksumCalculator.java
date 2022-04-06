package com.github.cfogrady.dcom.digimon;

import lombok.Getter;

public class ChecksumCalculator {
    @Getter
    private int currentChecksum = 0;
    public static final int NIBBLE1_MASK = 0b1111000000000000;
    public static final int NIBBLE1_SHIFT = 12;
    public static final int NIBBLE2_MASK = 0b0000111100000000;
    public static final int NIBBLE2_SHIFT = 8;
    public static final int NIBBLE3_MASK = 0b0000000011110000;
    public static final int NIBBLE3_SHIFT = 4;
    public static final int NIBBLE4_MASK = 0b0000000000001111;

    public void addToChecksum(int packet) {
        addNibbleToChecksum((packet & NIBBLE1_MASK) >> NIBBLE1_SHIFT);
        addNibbleToChecksum((packet & NIBBLE2_MASK) >> NIBBLE2_SHIFT);
        addNibbleToChecksum((packet & NIBBLE3_MASK) >> NIBBLE3_SHIFT);
        addNibbleToChecksum(packet & NIBBLE4_MASK);
    }

    public void addNibbleToChecksum(int nibble) {
        currentChecksum += nibble;
    }

    public void clearCurrentChecksum() {
        currentChecksum = 0;
    }

    public int valueToAchieveRemainder(int remainder) {
        int currentRemainder = currentChecksum % 16;
        if(currentRemainder > remainder) {
            return (16 - currentRemainder) + remainder;
        } else if(currentRemainder < remainder) {
            return remainder - currentRemainder;
        }
        return 0;
    }
}
