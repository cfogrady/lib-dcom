package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.digimon.ChecksumCalculator;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DM20Translator {
    private final DM20RomReader reader;
    private final DM20RomWriter writer;
    private final DM20Rom localCombatant;
    private final ChecksumCalculator checksumCalculator;
    private final List<Consumer<Integer>> packetConsumers;
    private final List<Supplier<Integer>> packetSuppliers;
    @Getter
    private int currentReceivedPacketNumber;
    @Getter
    private int currentSentPacketNumber;
    private List<Integer> receivedPacketBuffer;
    private DM20Rom.DM20RomBuilder remoteCombatantBuilder;

    public DM20Translator(DM20RomReader reader, DM20RomWriter writer,
                          DM20Rom localCombatant, ChecksumCalculator checksumCalculator) {
        this(reader, writer, localCombatant, checksumCalculator, 0, 0,
                Collections.emptyList(), DM20Rom.builder());
    }

    DM20Translator(DM20RomReader reader, DM20RomWriter writer, DM20Rom localCombatant,
                   ChecksumCalculator checksumCalculator, int currentReceivedPacketNumber,
                   int currentSentPacketNumber, List<Integer> receivedPacketBuffer,
                   DM20Rom.DM20RomBuilder remoteCombatantBuilder) {
        this.reader = reader;
        this.writer = writer;
        this.localCombatant = localCombatant;
        this.checksumCalculator = checksumCalculator;
        this.currentReceivedPacketNumber = currentReceivedPacketNumber;
        this.currentSentPacketNumber = currentSentPacketNumber;
        this.receivedPacketBuffer = receivedPacketBuffer;
        this.remoteCombatantBuilder = remoteCombatantBuilder;
        this.packetConsumers = List.of(
                packet -> receivedPacketBuffer.add(packet),
                packet -> {
                    remoteCombatantBuilder.name(reader.readNamePackets(receivedPacketBuffer.get(0), packet));
                    receivedPacketBuffer.clear();
                },
                packet -> reader.readOperationPacket(packet, remoteCombatantBuilder),
                packet -> receivedPacketBuffer.add(packet),
                packet -> receivedPacketBuffer.add(packet),
                packet -> {
                    DM20Rom.DigiStats stats = reader.readDigimonPackets(receivedPacketBuffer.get(0),
                            receivedPacketBuffer.get(1),
                            packet);
                    remoteCombatantBuilder.firstDigimon(stats);
                    receivedPacketBuffer.clear();
                },
                packet -> receivedPacketBuffer.add(packet),
                packet -> receivedPacketBuffer.add(packet),
                packet -> {
                    DM20Rom.DigiStats stats = reader.readDigimonPackets(receivedPacketBuffer.get(0),
                            receivedPacketBuffer.get(1),
                            packet);
                    remoteCombatantBuilder.firstDigimon(stats);
                    receivedPacketBuffer.clear();
                },
                packet -> reader.readFinalPacket(packet, remoteCombatantBuilder)
        );
        this.packetSuppliers = List.of(
                () -> writer.getPacketForCharacters(localCombatant.getName()[0], localCombatant.getName()[1]),
                () -> writer.getPacketForCharacters(localCombatant.getName()[2], localCombatant.getName()[3]),
                () -> {
                    localCombatant.setInitiator(currentReceivedPacketNumber == currentSentPacketNumber);
                    return writer.getOperationPacket(localCombatant);
                },
                () -> writer.getDigimonIndexPacket(localCombatant.getFirstDigimon()),
                () -> writer.getDigimonShotsPacket(localCombatant.getFirstDigimon()),
                () -> writer.getDigimonPowerPacket(localCombatant.getFirstDigimon()),
                () -> writer.getDigimonIndexPacket(localCombatant.getSecondDigimon()),
                () -> writer.getDigimonShotsPacket(localCombatant.getSecondDigimon()),
                () -> writer.getDigimonPowerPacket(localCombatant.getSecondDigimon()),
                () -> getHitsPacket()
        );
    }

    public void receiveNextPacket(int packet) {
        packetConsumers.get(currentReceivedPacketNumber).accept(packet);
        currentReceivedPacketNumber++;
    }

    public int getNextPacketToSend() {
        int packet = packetSuppliers.get(currentSentPacketNumber).get();
        checksumCalculator.addToChecksum(packet);
        currentSentPacketNumber++;
        return packet;
    }

    int getHitsPacket() {
        if(currentReceivedPacketNumber == currentSentPacketNumber) {
            //we're the initiator
            throw new UnsupportedOperationException("Battle calculations must be done on the device side at the moment");
        }
        DM20Rom remoteCombatant = remoteCombatantBuilder.build();
        int hits = (~remoteCombatant.getDodges() & 0b1111);
        int dodges = (~remoteCombatant.getHits() & 0b1111);
        checksumCalculator.addNibbleToChecksum(hits);
        checksumCalculator.addNibbleToChecksum(dodges);
        checksumCalculator.addNibbleToChecksum(DM20PacketMasks.EOL_MASK);
        int check = checksumCalculator.valueToAchieveRemainder(0);
        return (check << DM20PacketMasks.CHECK_SHIFT) | (dodges << DM20PacketMasks.DODGES_SHIFT) | (hits << DM20PacketMasks.HITS_SHIFT) | DM20PacketMasks.EOL_MASK;
    }

    public DM20Rom getRemoteCombatantRom() {
        if(currentReceivedPacketNumber < 10) {
            throw new IllegalStateException("ROM can only be read after all 10 packets have been translated");
        }
        return remoteCombatantBuilder.build();
    }
}
