package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.serial.EventDrivenSerialPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class DM20RomWriterTest {
    @Test
    void testReader() {
        String received = "s:0101 r:011A s:0101 r:0C05 s:800E r:382E s:008E r:171E s:05AE r:49FE s:01EE r:032E s:000E r:000E s:000E " +
                "r:000E s:000E r:000E s:FF0E r:D0FE\r\n";
        List<Integer> packetList = EventDrivenSerialPort.convertTextToReceivedPacket(received);
        log.info("Packets: {}", packetList.size());
        DM20RomReader romReader = new DM20RomReader();
        DM20Rom rom = romReader.read(packetList);
        DM20RomWriter writer = new DM20RomWriter();
        String dcommROM = writer.getReceivingDCommRom(rom, false);
        log.info("ROM: {}", dcommROM);
    }
}
