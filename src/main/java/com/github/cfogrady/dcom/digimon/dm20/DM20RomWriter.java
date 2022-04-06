package com.github.cfogrady.dcom.digimon.dm20;

import java.util.ArrayList;
import java.util.List;

public class DM20RomWriter {

    static final String REFLECTION_PACKET = "-@0^F^FE";

    List<Integer> writePackets(DM20Rom dm20Rom) {
        List<Integer> packets = new ArrayList<>();
        int packet1 = getPacketForCharacters(dm20Rom.getName()[0], dm20Rom.getName()[1]);
        int packet2 = getPacketForCharacters(dm20Rom.getName()[2], dm20Rom.getName()[3]);
        packets.add(packet1);
        packets.add(packet2);
        packets.add(getOperationPacket(dm20Rom));
        packets.add(getDigimonIndexPacket(dm20Rom.getFirstDigimon()));
        packets.add(getDigimonShotsPacket(dm20Rom.getFirstDigimon()));
        packets.add(getDigimonPowerPacket(dm20Rom.getFirstDigimon()));
        packets.add(getDigimonIndexPacket(dm20Rom.getSecondDigimon()));
        packets.add(getDigimonShotsPacket(dm20Rom.getSecondDigimon()));
        packets.add(getDigimonPowerPacket(dm20Rom.getSecondDigimon()));
        return packets;
    }

    public String getReceivingDCommRom(DM20Rom dm20Rom, boolean dropLastPacket) {
        List<Integer> packets = writePackets(dm20Rom);
        String rom = convertToTextRom(packets, dm20Rom.isInitiator());
        if(dropLastPacket) {
            return rom;
        }
        return rom + REFLECTION_PACKET;
    }

    public byte[] getReceivingDCommRomBytes(DM20Rom dm20Rom, boolean dropLastPacket) {
        return (getReceivingDCommRom(dm20Rom, dropLastPacket) + "\n").getBytes();
    }

    public String convertToTextRom(List<Integer> packets, boolean initiator) {
        StringBuilder builder = new StringBuilder();
        builder.append("V");
        if(initiator) {
            builder.append("1");
        } else {
            builder.append("2");
        }
        for(int packet : packets) {
            builder.append("-");
            String hex = Integer.toHexString(packet).toUpperCase();
            for(int i = 0; i < 4 - hex.length(); i++) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    int getPacketForCharacters(char char1, char char2) {
        int character1;
        if(char1 == ' ') {
            character1 = 0;
        } else {
            character1 = char1 - 64;
        }
        int character2;
        if(char2 == ' ') {
            character2 = 0;
        } else {
            character2 = char2 - 64;
        }
        return (character2 << 8) | character1;
    }

    int getOperationPacket(DM20Rom rom) {
        int orderSegment = rom.isInitiator() ? DM20PacketMasks.ORDER_MASK : 0;
        int attackSegment = rom.getAttack() << DM20PacketMasks.ATTACK_SHIFT;
        int operationSegment = rom.getOperation().getOrdinal() << DM20PacketMasks.OPERATION_SHIFT;
        int versionSegment = rom.getVersion() << DM20PacketMasks.VERSION_SHIFT;
        int eolSegment = DM20PacketMasks.EOL_MASK;
        return orderSegment | attackSegment | operationSegment | versionSegment | eolSegment;
    }

    int getDigimonIndexPacket(DM20Rom.DigiStats digimon) {
        int indexSegment = digimon == null ? 0 : digimon.getIndex() << DM20PacketMasks.INDEX_SHIFT;
        int attributeSegment = digimon == null ? 0 : digimon.getAttribute().ordinal() << DM20PacketMasks.ATTRIBUTE_SHIFT;
        return indexSegment | attributeSegment | DM20PacketMasks.EOL_MASK;
    }

    int getDigimonShotsPacket(DM20Rom.DigiStats digimon) {
        int strongShotSegment = digimon == null ? 0 : digimon.getStrongShot() << DM20PacketMasks.STRONG_SHOT_SHIFT;
        int weakShotSegment = digimon == null ? 0 : digimon.getWeakShot() << DM20PacketMasks.WEAK_SHOT_SHIFT;
        return strongShotSegment | weakShotSegment | DM20PacketMasks.EOL_MASK;
    }

    int getDigimonPowerPacket(DM20Rom.DigiStats digimon) {
        int tagSegment = digimon == null ? 0 : digimon.getTagMeter() << DM20PacketMasks.TAG_SHIFT;
        int powerSegment = digimon == null ? 0 : digimon.getPower() << DM20PacketMasks.POWER_SHIFT;
        return tagSegment | powerSegment | DM20PacketMasks.EOL_MASK;
    }
}
