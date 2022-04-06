package com.github.cfogrady.dcom.digimon.dm20;

public enum DM20Attack {
    SMILE, // 0
    DART, //1 (orginal DM battle sprite)
    ROCKET, //2
    COMET, //3
    LIGHTNING, //4
    NEEDLE, //5
    PUNCH, //6
    BOMB, //7
    HEART, //8
    FLUSH, //9
    BLACK_ROCKET, //10
    BONE, //11
    BOW, //12
    DARK_BALL, //13
    HOOPS, //14
    POOP, //15
    FIREBALL_THREE_TAIL, //16
    FLAME, //17
    WHIRLPOOL, //18
    SWORD, //19 NEVER USED
    BIG_CURVED_TUSK, //20
    PULSE, //21
    SPARKLES, //22
    PINCER, //23
    BIG_CHEVRON, //24
    KNIFE, //25
    CLAW, //26
    SKULL, //27
    MUSIC, //28
    BALL_BOW, //29
    HOLLOW_BULLET, //30
    CURVED_TUSK, //31
    CHEVRON, //32
    CROSS, //33
    GRID, //34
    BUBBLES, //35
    MINI_ROCKET, //36
    HOT_FIREBALL, //37
    STRIPY_BALL, //38
    FLOWER, //39
    SNAIL_SHELL, //40
    LIGHTNING_TRIPLE, //41
    SCYTHE, //42
    PAW, //43
    EVIL_CLAW, //44 NEVER_USED
    WHITE_FIRE, //45
    CRITICAL_WAVE; //46

    public static DM20Attack getAttackById(Integer ordinal) {
        if(ordinal == null || ordinal > DM20Attack.values().length) {
            return null;
        }
        return DM20Attack.values()[ordinal];
    }
}
