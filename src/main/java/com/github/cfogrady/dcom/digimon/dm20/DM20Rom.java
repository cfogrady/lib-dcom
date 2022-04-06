package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.digimon.Attribute;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DM20Rom {
    public static final int NUMBER_OF_PACKETS = 10;

    private final char[] name;
    private boolean initiator;
    private final int attack;
    private final DM20Operation operation;
    private final int version;
    private final DigiStats firstDigimon;
    private final DigiStats secondDigimon;
    private int check;
    private int dodges;
    private int hits;

    @Builder
    @Data
    public static class DigiStats {
        private final int index;
        private final Attribute attribute;
        private final int strongShot;
        private final int weakShot;
        private final int power; //base power + (4 * strength hearts)
        private final int tagMeter;
    }

    public String getDodgesBinary() {
        String dodgesStr = Integer.toBinaryString(dodges);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < 4 - dodgesStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(dodgesStr);
        return stringBuilder.reverse().toString();
    }

    public String getHitsBinary() {
        String hitsStr = Integer.toBinaryString(hits);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < 4 - hitsStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hitsStr);
        return stringBuilder.reverse().toString();
    }
}
