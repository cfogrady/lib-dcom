package com.github.cfogrady.dcom.digimon.dm20;

public class DM20PacketMasks {
    static final int ORDER_MASK =        0b1000000000000000;
    static final int ATTACK_MASK =       0b0111110000000000;
    static final int ATTACK_SHIFT = 10;
    static final int OPERATION_MASK =    0b0000001100000000;
    static final int OPERATION_SHIFT = 8;
    static final int VERSION_MASK =      0b0000000011110000;
    static final int VERSION_SHIFT = 4;

    static final int EOL_MASK =          0b0000000000001110;

    static final int INDEX_MASK =        0b0011111111000000;
    static final int INDEX_SHIFT = 6;
    static final int ATTRIBUTE_MASK =   0b0000000000110000;
    static final int ATTRIBUTE_SHIFT = 4;

    static final int STRONG_SHOT_MASK = 0b1111110000000000;
    static final int STRONG_SHOT_SHIFT = 10;
    static final int WEAK_SHOT_MASK = 0b0000001111110000;
    static final int WEAK_SHOT_SHIFT = 4;

    static final int TAG_MASK =       0b1111000000000000;
    static final int TAG_SHIFT = 12;
    static final int POWER_MASK =     0b0000111111110000;
    static final int POWER_SHIFT = 4;

    static final int CHECK_MASK =     0b1111000000000000;
    static final int CHECK_SHIFT = 12;
    static final int DODGES_MASK =     0b0000111100000000;
    static final int DODGES_SHIFT = 8;
    static final int HITS_MASK =     0b0000000011110000;
    static final int HITS_SHIFT = 4;
}
