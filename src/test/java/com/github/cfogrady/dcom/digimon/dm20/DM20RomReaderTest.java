package com.github.cfogrady.dcom.digimon.dm20;

import com.github.cfogrady.dcom.serial.EventDrivenSerialPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class DM20RomReaderTest {
    @Test
    void testReader() {
        String received = "s:0101 r:011A s:0101 r:0C05 s:800E r:382E s:008E r:171E s:05AE r:49FE s:01EE r:032E s:000E r:000E s:000E " +
                "r:000E s:000E r:000E s:FF0E r:D0FE\r\n";
        List<Integer> packetList = EventDrivenSerialPort.convertTextToReceivedPacket(received);
        log.info("Packets: {}", packetList.size());
        DM20RomReader romReader = new DM20RomReader();
        DM20Rom rom = romReader.read(packetList);
        log.info("Name: {}", rom.getName());
        log.info("Initiated: {}, Attack: {}, Operation: {}, Version: {}", rom.isInitiator(), rom.getAttack(), rom.getOperation(), rom.getVersion());
        DM20Rom.DigiStats lStats = rom.getFirstDigimon();
        log.info("Index: {}, Attribute: {}, Strong Attack: {}, Weak Attack: {}, Tag Meter: {}, Power: {}", lStats.getIndex(), lStats.getAttribute(), lStats.getStrongShot(), lStats.getWeakShot(), lStats.getTagMeter(), lStats.getPower());
        DM20Rom.DigiStats rStats = rom.getSecondDigimon();
        log.info("Index: {}, Attribute: {}, Strong Attack: {}, Weak Attack: {}, Tag Meter: {}, Power: {}", rStats.getIndex(), rStats.getAttribute(), rStats.getStrongShot(), rStats.getWeakShot(), rStats.getTagMeter(), rStats.getPower());
        log.info("Check: {}, Hits: {}, Dodges: {}", rom.getCheck(), rom.getHitsBinary(), rom.getDodgesBinary());
    }
}
