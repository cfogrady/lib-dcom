package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.digimon.Attribute;
import com.github.cfogrady.dcom.serial.EventDrivenSerialPort;

import java.util.List;

public class DM20RomReader {

    public DM20Rom read(List<Integer> packets) {
        DM20Rom.DM20RomBuilder builder = DM20Rom.builder();
        builder = builder.name(readNamePackets(packets.get(0), packets.get(1)));
        builder = readOperationPacket(packets.get(2), builder);
        builder = builder.firstDigimon(readDigimonPackets(packets.get(3), packets.get(4), packets.get(5)));
        builder = builder.secondDigimon(readDigimonPackets(packets.get(6), packets.get(7), packets.get(8)));
        builder = readFinalPacket(packets.get(9), builder);
        return builder.build();
    }

    char[] readNamePackets(int packet1, int packet2) {
        char[] characters = new char[4];
        int byte1 = (packet1 & EventDrivenSerialPort.BYTE1_MASK) >> 8;
        int byte2 = packet1 & EventDrivenSerialPort.BYTE2_MASK;
        if(byte1 == 0) {
            characters[1] = ' ';
        } else {
            characters[1] = (char) (byte1 + 64);
        }
        if(byte2 == 0) {
            characters[0] = ' ';
        } else {
            characters[0] = (char) (byte2 + 64);
        }
        byte1 = (packet2 & EventDrivenSerialPort.BYTE1_MASK) >> 8;
        byte2 = packet2 & EventDrivenSerialPort.BYTE2_MASK;
        if(byte1 == 0) {
            characters[3] = ' ';
        } else {
            characters[3] = (char) (byte1 + 64);
        }
        if(byte2 == 0) {
            characters[2] = ' ';
        } else {
            characters[2] = (char) (byte2 + 64);
        }
        return characters;
    }

    DM20Rom.DM20RomBuilder readOperationPacket(int packet, DM20Rom.DM20RomBuilder builder) {
        return builder.initiator((packet & DM20PacketMasks.ORDER_MASK) == DM20PacketMasks.ORDER_MASK)
                .attack((packet & DM20PacketMasks.ATTACK_MASK) >> DM20PacketMasks.ATTACK_SHIFT)
                .operation(DM20Operation.getByOrdinal((packet & DM20PacketMasks.OPERATION_MASK) >>DM20PacketMasks.OPERATION_SHIFT))
                .version((packet & DM20PacketMasks.VERSION_MASK) >> DM20PacketMasks.VERSION_SHIFT);
    }

    DM20Rom.DigiStats readDigimonPackets(int packet1, int packet2, int packet3) {
        return DM20Rom.DigiStats.builder()
                .index((packet1 & DM20PacketMasks.INDEX_MASK) >> DM20PacketMasks.INDEX_SHIFT)
                .attribute(Attribute.values()[(packet1 & DM20PacketMasks.ATTRIBUTE_MASK) >> DM20PacketMasks.ATTRIBUTE_SHIFT])
                .strongShot((packet2 & DM20PacketMasks.STRONG_SHOT_MASK) >> DM20PacketMasks.STRONG_SHOT_SHIFT)
                .weakShot((packet2 & DM20PacketMasks.WEAK_SHOT_MASK) >> DM20PacketMasks.WEAK_SHOT_SHIFT)
                .tagMeter((packet3 & DM20PacketMasks.TAG_MASK) >> DM20PacketMasks.TAG_SHIFT)
                .power((packet3 & DM20PacketMasks.POWER_MASK) >> DM20PacketMasks.POWER_SHIFT)
                .build();
    }

    DM20Rom.DM20RomBuilder readFinalPacket(int packet, DM20Rom.DM20RomBuilder builder) {
        return builder.check((packet & DM20PacketMasks.CHECK_MASK) >> DM20PacketMasks.CHECK_SHIFT)
                .dodges((packet & DM20PacketMasks.DODGES_MASK) >> DM20PacketMasks.DODGES_SHIFT)
                .hits((packet & DM20PacketMasks.HITS_MASK) >> DM20PacketMasks.HITS_SHIFT);
    }
}
