package oz.stream.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oz.stream.config.AppConfiguration;
import oz.stream.model.Valores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author rubn
 */
@Service
@RequiredArgsConstructor
public class ReadFileService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AppConfiguration appConfiguration;

    public Valores getConfigurationMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream(this.appConfiguration.getConfigFile());
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String configuration = reader
                    .lines()
                    .collect(Collectors.joining());

            return this.objectMapper.readValue(configuration, Valores.class);

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero configuration_XXX.json");
        }
    }

    public String getMessage() {
        try (var inputStream = ReadFileService.class.getResourceAsStream(this.appConfiguration.getMessageFile());
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader
                    .lines()
                    .collect(Collectors.joining());

        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el fichero X.json");
        }

    }

}
