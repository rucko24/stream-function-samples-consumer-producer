package oz.stream.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class FileGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("Fenerate file")
    @Test
    void generate() {
        final int size = 2 * 1024 * 1024;  // 2097152 bytes

        final StringBuilder stringBuilder = new StringBuilder();

        for (int f = 0; f < size; f++) {
            stringBuilder.append("X");
        }

        final Datos datos = new Datos();
        datos.setData(stringBuilder.toString());

        try {
            objectMapper.writeValue(Path.of("src/test/resources/XXL.json").toFile(), datos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Data
    private static class Datos {

        @JsonProperty("data")
        private String data;

    }

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    @Test
    @SneakyThrows
    void scheduler() {
        final AtomicLong atomicLong = new AtomicLong();

        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            atomicLong.incrementAndGet();
            System.out.println("Counter " + atomicLong.get());

            if(atomicLong.get() == 3) {
                this.scheduledFuture.cancel(true);
                System.out.println("isCancelled " + this.scheduledFuture.isCancelled());
            }

        }, 0, 1, TimeUnit.SECONDS);


        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

    }

}
