package com.github.cfogrady.dcom;

import com.fazecast.jSerialComm.SerialPort;
import com.github.cfogrady.dcom.digimon.Attribute;
import com.github.cfogrady.dcom.digimon.dm20.DM20Operation;
import com.github.cfogrady.dcom.digimon.dm20.DM20Rom;
import com.github.cfogrady.dcom.digimon.dm20.DM20RomReader;
import com.github.cfogrady.dcom.digimon.dm20.DM20RomWriter;
import com.github.cfogrady.dcom.serial.DComInitializer;
import com.github.cfogrady.dcom.serial.EventDrivenSerialPort;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {
    public static final int SENT_POWER = 96;
    public static final Attribute SENT_ATTRIBUTE = Attribute.VIRUS;
    public static final int SENT_ATTACK = 9;

    public static void main(String[] arggs) {
        SerialPort dcomm = SerialPort.getCommPorts()[0];
        DComInitializer transmitter = setupPort(dcomm);
        DM20Rom.DigiStats digimon = DM20Rom.DigiStats.builder().index(96).attribute(SENT_ATTRIBUTE)
                .weakShot(31).strongShot(32)
                .tagMeter(0).power(SENT_POWER).build();
        DM20Rom rom = DM20Rom.builder().initiator(false).name("TEST".toCharArray())
                .version(1).operation(DM20Operation.BATTLE).attack(SENT_ATTACK).firstDigimon(digimon).build();
        DM20RomWriter romWriter = new DM20RomWriter();
        byte[] romBytes = romWriter.getReceivingDCommRomBytes(rom, false);
        int writtenBytes = dcomm.writeBytes(romBytes, romBytes.length);
        if(writtenBytes != romBytes.length) {
            log.error("Was only able to write {} bytes to port!", writtenBytes);
        }
        File file = new File("c:\\dev\\output.csv");
        DM20RomReader romReader = new DM20RomReader();
        try(OutputStream outputStream = new FileOutputStream(file)) {
            String headerLine = "Battle,ReceivedPower,ReceivedAttr,ReceivedAttack,SentPower,SentAttr,SentAttack,RcvHit1,RcvHit2,RcvHit3,RcvHit4,RcvDodge1,RcvDodge2,RcvDodge3,RcvDodge4" + System.lineSeparator();
            outputStream.write(headerLine.getBytes());
            log.info("Press enter to stop.");
            System.in.read();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        dcomm.closePort();
        log.info("Digiport Closed.");
    }

    static DComInitializer setupPort(SerialPort dcomm) {
        boolean opened = dcomm.openPort();
        if(!opened) {
            throw new IllegalStateException("Unable to open port");
        }
        log.info("Digiport Opened!");
        try {
            log.info("Resting for 2 seconds for the Arduino to turn on.");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    static List<Integer> packetsBuffer = new ArrayList<>();
    static int currentBattle = 0;
    static void receiveHandler(byte[] bytes, DM20RomReader romReader, OutputStream outputStream) {
        try {
            List<Integer> packets = EventDrivenSerialPort.getReceivedPackets(bytes);
            packetsBuffer.addAll(packets);
            log.info("Packets Registered: {}", packetsBuffer.size());
            if (packetsBuffer.size() == DM20Rom.NUMBER_OF_PACKETS) {
                DM20Rom receivedROM = romReader.read(packetsBuffer);
                packetsBuffer.clear();
                currentBattle++;
                StringBuilder builder = new StringBuilder();
                String hits = receivedROM.getHitsBinary();
                builder.append(currentBattle).append(",")
                        .append(receivedROM.getFirstDigimon().getPower()).append(",")
                        .append(receivedROM.getFirstDigimon().getAttribute()).append(",")
                        .append(receivedROM.getAttack()).append(",")
                        .append(SENT_POWER).append(",")
                        .append(SENT_ATTRIBUTE).append(",")
                        .append(SENT_ATTACK).append(",");
                for (char value : hits.toCharArray()) {
                    builder.append(value).append(",");
                }
                String dodges = receivedROM.getDodgesBinary();
                for (char value : dodges.toCharArray()) {
                    builder.append(value).append(",");
                }
                log.info("Line output: {}", builder);
                builder.append(System.lineSeparator());
                outputStream.write(builder.toString().getBytes());
            }
        } catch (Exception e) {
            log.error("Failed to decode!", e);
        }
    }
}
