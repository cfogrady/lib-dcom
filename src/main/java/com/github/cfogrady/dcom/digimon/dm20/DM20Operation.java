package com.github.cfogrady.dcom.digimon.dm20;

import lombok.Getter;

public enum DM20Operation {
    BATTLE(0),
    SEND(1),
    TAG_BATTLE(2),
    RECEIVE(3);

    private static final DM20Operation[] operationByOrd = { BATTLE, SEND, TAG_BATTLE, RECEIVE};

    @Getter
    private final int ordinal;

    DM20Operation(int ordinal) {
        this.ordinal = ordinal;
    }

    public static DM20Operation getByOrdinal(int ordinal) {
        if(ordinal >= operationByOrd.length) {
            throw new IllegalArgumentException("Invalid operation.");
        }
        return operationByOrd[ordinal];
    }


}
