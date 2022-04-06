package com.github.cfogrady.dcom.serial;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class DComInitializer {

    public static final String INITIALIZE_LISTENER = "InitializeListener";

    private final EventDrivenSerialPort eventDrivenSerialPort;

    public DComInitializer(EventDrivenSerialPort eventDrivenSerialPort) {
        this.eventDrivenSerialPort = eventDrivenSerialPort;
    }

    /**
     * Waits until initialization is done before returning
     * @return
     */
    public CompletableFuture<Void> initialize() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        eventDrivenSerialPort.addListener(INITIALIZE_LISTENER, getInitializationConsumer(future));
        tryInitializationUntilDone(future);
        return future.thenRun(() -> eventDrivenSerialPort.removeListener(INITIALIZE_LISTENER));
    }

    private void tryInitializationUntilDone(CompletableFuture<Void> initialization) {
        if(!initialization.isDone()) {
            eventDrivenSerialPort.writeBytes("V0\n".getBytes());
            CompletableFuture.runAsync(() -> tryInitializationUntilDone((initialization)), CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS));
        }
    }

    Consumer<byte[]> getInitializationConsumer(CompletableFuture<Void> future) {
        return bytes -> {
            future.complete(null);
        };
    }
}
