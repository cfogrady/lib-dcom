package com.github.cfogrady.dcom.digimon.dm20.battle;

import com.github.cfogrady.dcom.digimon.battle.AttackType;
import com.github.cfogrady.dcom.digimon.battle.BattleResult;
import com.github.cfogrady.dcom.digimon.battle.DigimonChoice;
import com.github.cfogrady.dcom.digimon.dm20.DM20IndexEntry;
import com.github.cfogrady.dcom.digimon.dm20.DM20Rom;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DM20BattleSimulator {
    public static final int SINGLE_BIT = 0b1;
    public static final int FULL_STRENGTH_POWER_INCREASE = 16;

    public static final Map<AttackType, Integer> damageByAttack = dm20Damages();

    private final List<DM20IndexEntry> powersByIndex;

    public BattleResult simulateBattle(DM20Rom computerRom, DM20Rom deviceRom) {
        List<DigimonChoice> gotHitByRound = new ArrayList<>();
        int deviceHits = deviceRom.getHits();
        log.info("Device Hits: {}", deviceRom.getHitsBinary());
        log.info("Device Was Hit: {}", deviceRom.getDodgesBinary());
        int deviceWasHit = deviceRom.getDodges();
        List<AttackType> deviceAttackPattern = getAttackPattern(deviceRom.getAttack());
        List<AttackType> computerAttackPattern = getAttackPattern(computerRom.getAttack());
        int round = 0;
        int deviceHp = 10;
        int computerHp = 10;
        boolean deviceDigimonAtFullStrength = isDigimonAtFullStrength(deviceRom.getFirstDigimon());
        boolean computerDigimonAtFullStrength = isDigimonAtFullStrength(computerRom.getFirstDigimon());
        while(deviceHp > 0 && computerHp > 0 && round < 5) {
            RoundResult roundResult = calculateRoundResult(deviceHits,
                    deviceDigimonAtFullStrength,
                    deviceWasHit,
                    computerDigimonAtFullStrength,
                    deviceAttackPattern,
                    computerAttackPattern,
                    round);
            deviceHp -= roundResult.lostDeviceHp;
            computerHp -= roundResult.lostComputerHp;
            gotHitByRound.add(roundResult.gotHit);
            round++;
        }
        if(deviceHp == computerHp && computerHp > 0) {
            // round 6 id still tied after 5 rounds
            RoundResult roundResult = calculateRoundResult(deviceHits,
                    false,
                    deviceWasHit,
                    false,
                    deviceAttackPattern,
                    computerAttackPattern,
                    round);
            deviceHp -= roundResult.lostDeviceHp;
            computerHp -= roundResult.lostComputerHp;
            gotHitByRound.add(roundResult.gotHit);
        }
        DigimonChoice winner;
        if(computerHp == deviceHp) {
            winner = DigimonChoice.NEITHER;
        } else if(computerHp > deviceHp) {
            winner = DigimonChoice.COMPUTER_DIGIMON;
        } else {
            winner = DigimonChoice.DIGIVICE_DIGIMON;
        }
        return BattleResult.builder()
                .digiviceMonAttackPattern(deviceAttackPattern)
                .computerMonAttackPattern(computerAttackPattern)
                .hits(gotHitByRound)
                .winner(winner)
                .deviceIndex(deviceRom.getFirstDigimon().getIndex())
                .build();
    }

    private RoundResult calculateRoundResult(int roundsDeviceHits,
                                             boolean deviceAtFullStr,
                                             int roundsDeviceWasHit,
                                             boolean computerMonAtFullStr,
                                             List<AttackType> deviceAttackPattern,
                                             List<AttackType> computerAttackPattern,
                                             int round) {
        boolean deviceHits = bitSetAtPos(roundsDeviceHits, round % 4);
        boolean deviceWasHit = bitSetAtPos(roundsDeviceWasHit, round % 4);
        AttackType deviceAttack = deviceAttackPattern.get(round % 5);
        AttackType digiQuestAttack = computerAttackPattern.get(round % 5);
        int lostDeviceHp = 0;
        int lostComputerHp = 0;
        int monstersHit = 0;
        if(deviceHits) {
            monstersHit = monstersHit | 0x1;
            lostComputerHp = damageByAttack.get(deviceAttack);
            if(deviceAtFullStr) {
                lostComputerHp++;
            }
        }
        if(deviceWasHit) {
            monstersHit = monstersHit | 0x2;
            lostDeviceHp = damageByAttack.get(digiQuestAttack);
            if(computerMonAtFullStr) {
                lostDeviceHp++;
            }
        }
        log.info("Damage to Device: {}", lostDeviceHp);
        log.info("Damage to DigiQuest: {}", lostComputerHp);
        return RoundResult.builder()
                .lostDeviceHp(lostDeviceHp)
                .lostComputerHp(lostComputerHp)
                .gotHit(DigimonChoice.values()[monstersHit])
                .build();
    }

    private boolean isDigimonAtFullStrength(DM20Rom.DigiStats digimon) {
        log.info("Checking digimon index: {}", powersByIndex.get(digimon.getIndex()));
        return digimon.getPower() >= powersByIndex.get(digimon.getIndex()).getPower() + FULL_STRENGTH_POWER_INCREASE;
    }

    private boolean bitSetAtPos(int value, int position) {
        return ((value >> position) & SINGLE_BIT) == SINGLE_BIT;
    }

    public static Map<AttackType, Integer> dm20Damages() {
        Map<AttackType, Integer> damages = new HashMap<>();
        damages.put(AttackType.WEAK, 1);
        damages.put(AttackType.WEAK_DOUBLE, 3);
        damages.put(AttackType.STRONG, 2);
        damages.put(AttackType.STRONG_DOUBLE, 4);
        return damages;
    }

    @Data
    @Builder
    public static class RoundResult {
        private final int lostDeviceHp;
        private final int lostComputerHp;
        private final DigimonChoice gotHit;
    }

    public static List<AttackType> getAttackPattern(int attack) {
        List<AttackType> attackPattern;
        if(attack < 3) {
            attackPattern = List.of(AttackType.WEAK,
                    AttackType.STRONG,
                    AttackType.WEAK_DOUBLE,
                    AttackType.WEAK,
                    AttackType.WEAK);
        } else if(attack < 5) {
            attackPattern = List.of(AttackType.STRONG,
                    AttackType.WEAK_DOUBLE,
                    AttackType.STRONG,
                    AttackType.WEAK_DOUBLE,
                    AttackType.STRONG);
        } else if(attack < 7) {
            attackPattern = List.of(AttackType.WEAK_DOUBLE,
                    AttackType.WEAK,
                    AttackType.STRONG,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK_DOUBLE);
        } else if(attack < 9) {
            attackPattern = List.of(AttackType.STRONG_DOUBLE,
                    AttackType.WEAK,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK,
                    AttackType.STRONG_DOUBLE);
        } else if(attack == 9) {
            attackPattern = List.of(AttackType.WEAK,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK);
        } else if(attack == 10) {
            attackPattern = List.of(AttackType.WEAK_DOUBLE,
                    AttackType.STRONG,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK_DOUBLE);
        } else if(attack == 11) {
            attackPattern = List.of(AttackType.STRONG_DOUBLE,
                    AttackType.WEAK,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE);
        } else if(attack == 12) {
            attackPattern = List.of(AttackType.STRONG_DOUBLE,
                    AttackType.WEAK_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE);
        } else {
            attackPattern = List.of(AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.WEAK_DOUBLE,
                    AttackType.STRONG_DOUBLE,
                    AttackType.STRONG_DOUBLE);
        }
        return attackPattern;
    }
}
