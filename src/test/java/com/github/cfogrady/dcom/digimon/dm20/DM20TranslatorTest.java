package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.digimon.ChecksumCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class DM20TranslatorTest {
    @Test
    void testThatGetHitsPacketMirrorsOpponentPacket() {
        DM20Rom localCombatant = DM20Rom.builder().firstDigimon(DM20Rom.DigiStats.builder().build()).secondDigimon(DM20Rom.DigiStats.builder().build()).build();
        int remoteHits = 0b1010;
        int remoteDodges = 0b0011;
        DM20Rom.DM20RomBuilder remoteCombatantBuilder = DM20Rom.builder().hits(remoteHits).dodges(remoteDodges);
        DM20Translator dm20Translator = new DM20Translator(new DM20RomReader(), new DM20RomWriter(), localCombatant,
                new ChecksumCalculator(), 10, 9, Collections.emptyList(), remoteCombatantBuilder);
        int packet = dm20Translator.getHitsPacket();
        Assertions.assertEquals(0b1100, (packet & DM20PacketMasks.HITS_MASK) >> DM20PacketMasks.HITS_SHIFT, "Hits should be opposite of opponent's dodges");
        Assertions.assertEquals(0b0101, (packet & DM20PacketMasks.DODGES_MASK) >> DM20PacketMasks.DODGES_SHIFT, "Dodges should be opposite of opponent's hits");
    }
}
