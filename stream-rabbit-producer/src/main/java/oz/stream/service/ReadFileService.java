package oz.stream.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oz.stream.model.Valores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadFileService {

    public static final String SRC_MAIN_RESOURCES_CONFIGURATION_JSON = "stream-rabbit-producer-transaction/src/main/resources/configuration.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Valores getConfigurationMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream("/configuration.json");
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String configuration = reader
                    .lines()
                    .collect(Collectors.joining());

            return this.objectMapper.readValue(configuration, Valores.class);

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero configuration.json");
        }
    }

    public String getMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream("/L.json");
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader
                    .lines()
                    .collect(Collectors.joining());

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero L.json");
        }

    }

}
