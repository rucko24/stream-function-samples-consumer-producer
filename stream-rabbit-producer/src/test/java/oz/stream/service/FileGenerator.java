package oz.stream.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

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

}
