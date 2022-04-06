package com.github.cfogrady.dcom.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface EventDrivenSerialPort {

    int BYTE1_MASK = 0b1111111100000000;
    int BYTE2_MASK = 0b0000000011111111;

    static String bytesToString(byte[] bytes) {
        StringBuilder textBuilder = new StringBuilder();
        for(byte currentByte : bytes) {
            char c = (char) currentByte;
            textBuilder.append(c);
        }
        return textBuilder.toString();
    }

    static List<Integer> getReceivedPackets(byte[] bytes) {
        String receivedText = bytesToString(bytes);
        return  convertTextToReceivedPacket(receivedText);
    }

    static List<Integer> convertTextToReceivedPacket(String text) {
        List<Integer> convertedPackets = new ArrayList<>();
        String[] packets = text.split(" ");
        for(String packetStr : packets) {
            if(packetStr.startsWith("r:")) {
                int value = Integer.parseUnsignedInt(packetStr.substring(2, 6), 16);
                convertedPackets.add(value);
            }
        }
        return convertedPackets;
    }

    void addListener(String key, Consumer<byte[]> packetConsumer);

    void removeListener(String key);

    void writeBytes(byte[] bytes);
}
