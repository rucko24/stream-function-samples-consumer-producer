package oz.stream.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oz.stream.model.Valores;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReadFileService {

    public static final String SRC_MAIN_RESOURCES_CONFIGURATION_JSON = "stream-rabbit-producer-transaction/src/main/resources/configuration.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Valores getConfigurationMessage() {
        try {
            Objects.requireNonNull(SRC_MAIN_RESOURCES_CONFIGURATION_JSON, "path configuration null");
            final File path = Path.of(SRC_MAIN_RESOURCES_CONFIGURATION_JSON).toFile();
            return this.objectMapper.readValue(path, Valores.class);
        } catch (IOException ex) {
            throw new RuntimeException("Error al leer configuration.json");
        }
    }

    public Stream<String> getMessage() throws IOException {
        return Files.lines(Path.of("stream-rabbit-producer-transaction/src/main/resources/L.json"));
    }

}
