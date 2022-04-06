package com.github.cfogrady.dcom.digimon.battle;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BattleResult {
    private final DigimonChoice winner;
    private final List<AttackType> digiviceMonAttackPattern;
    private final List<AttackType> computerMonAttackPattern;
    private final List<DigimonChoice> hits;
    private final int deviceIndex;
}
