package com.github.cfogrady.dcom.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class DComDataListener implements SerialPortDataListener {
    private final SerialPort serialPort;

    private HashMap<String, Consumer<byte[]>> handlers = new HashMap<>();

    public void registerHandler(String key, Consumer<byte[]> handler) {
        if(handlers.containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + " already in use");
        }
        handlers.put(key, handler);
    }

    public Consumer<byte[]> unregisterHandler(String key) {
        return handlers.remove(key);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if(event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            log.warn("Received unsupported event: {}!", Integer.toBinaryString(event.getEventType()));
            return;
        }
        byte[] newData = new byte[serialPort.bytesAvailable()];
        int readBytes = serialPort.readBytes(newData , newData.length);
        if(readBytes != newData.length) {
            log.error("Only read {} bytes, when there should be {} available!", readBytes, newData.length);
        }
        for(Consumer<byte[]> consumer : handlers.values()) {
            consumer.accept(newData);
        }
    }
}
