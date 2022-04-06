package com.github.cfogrady.dcom.digimon;

import lombok.Getter;

public enum Stage {
    BABY(1, "Baby I", "Baby"),
    BABY_II(2, "Baby II", "In-Training"),
    CHILD(3, "Child", "Rookie"),
    ADULT(4, "Adult", "Champion"),
    PERFECT(5, "Perfect", "Ultimate"),
    ULTIMATE(6, "Ultimate", "Mega"),
    SUPER_ULTIMATE(7, "Super Ultimate", "Ultra");

    @Getter
    private final int stageNumber;
    @Getter
    private final String subName;
    @Getter
    private final String dubName;

    Stage(int stageNumber, String subName, String dubName) {
        this.stageNumber = stageNumber;
        this.subName = subName;
        this.dubName = dubName;
    }
}
