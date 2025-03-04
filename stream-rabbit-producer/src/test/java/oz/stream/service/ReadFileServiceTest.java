package oz.stream.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import oz.stream.model.Valores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Log4j2
class ReadFileServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Path PATH = Path.of("/");

    @Test
    @DisplayName("parallel == 123ms")
    void getConfigurationMessage_case() {

        final ResponseTimeService responseTimeService = new ResponseTimeService();

        getConfigurationMessage();

        log.info("Total time {}", responseTimeService.formatResponseTime());

    }

    @Test
    @DisplayName("parallel == 42ms")
    void getMessage_case() {
        final ResponseTimeService responseTimeService = new ResponseTimeService();

        getMessage();

        log.info("Total time {}", responseTimeService.formatResponseTime());
    }


    public Valores getConfigurationMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream(PATH.toString().concat("configuration_100.json"));
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String configuration = reader
                    .lines()
                    //.parallel()
                    .collect(Collectors.joining());

            return this.objectMapper.readValue(configuration, Valores.class);

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero configuration_XXX.json");
        }
    }

    public String getMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream(PATH.toString().concat("XXL.json"));
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader
                    .lines()
                    .parallel()
                    .collect(Collectors.joining());

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero X.json");
        }

    }

}