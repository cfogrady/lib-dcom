package com.github.cfogrady.dcom.digimon.dm20.battle;

import com.github.cfogrady.dcom.digimon.battle.BattleResult;
import com.github.cfogrady.dcom.digimon.dm20.DM20Rom;
import com.github.cfogrady.dcom.digimon.dm20.DM20RomReader;
import com.github.cfogrady.dcom.digimon.dm20.DM20RomWriter;
import com.github.cfogrady.dcom.serial.EventDrivenSerialPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class DM20BattleRunner {
    public static final String BATTLE_LISTENER = "BATTLE_LISTENER";
    public static final byte[] LISTEN_BYTES = "V0\n".getBytes();

    private final DM20RomWriter dm20RomWriter;
    private final DM20RomReader dm20RomReader;
    private final DM20BattleSimulator dm20BattleSimulator;

    public CompletableFuture<BattleResult> startBattle(EventDrivenSerialPort dCom, DM20Rom rom) {
        byte[] dcomSignal = dm20RomWriter.getReceivingDCommRomBytes(rom, false);
        log.info("ROM Sent to Device: {}", dm20RomWriter.getReceivingDCommRom(rom, false));
        StringBuilder receivedDataString = new StringBuilder();
        List<Integer> packets = new ArrayList<>();
        CompletableFuture<BattleResult> battle = new CompletableFuture<BattleResult>();
        dCom.addListener(BATTLE_LISTENER, resultBytes -> {
            log.info("Bytes Received from D-COM/A-COM: {}", EventDrivenSerialPort.bytesToString(resultBytes));
            for(byte currentByte : resultBytes) {
                char c = (char) currentByte;
                receivedDataString.append(c);
                if(c == '\n') {
                    String receivedString = receivedDataString.toString();
                    packets.addAll(EventDrivenSerialPort.convertTextToReceivedPacket(receivedString));
                    receivedDataString.setLength(0); // clear
                    if(packets.size() != 10) {
                        packets.clear();
                    }
                }
            }
            if(packets.size() == 10) {
                dCom.removeListener(BATTLE_LISTENER);
                dCom.writeBytes(LISTEN_BYTES);
                DM20Rom deviceDigimon = dm20RomReader.read(packets);
                battle.complete(dm20BattleSimulator.simulateBattle(rom, deviceDigimon));
            }
        });
        dCom.writeBytes(dcomSignal);
        return battle;
    }
}
