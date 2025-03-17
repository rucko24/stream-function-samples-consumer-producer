package oz.stream.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import oz.stream.enums.MessageSizeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RandomMessageGeneratorService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static final String MESSAGE_RANGE_100 = "100";

    private final Map<MessageSizeType, String> fileMapping = Map.of(
            MessageSizeType.S, "/S.json",
            MessageSizeType.M, "/M.json",
            MessageSizeType.L, "/L.json",
            MessageSizeType.XL, "/XL.json",
            MessageSizeType.XXL, "/XXL.json"
    );

    private MessageSizeType selectSizeTypeRange1000() {
        int rand = SECURE_RANDOM.nextInt(1000); // Valor entre 0 y 999

        if (rand < 540) {           // 54%
            return MessageSizeType.S;
        } else if (rand < 540 + 300) { // 30%
            return MessageSizeType.M;
        } else if (rand < 540 + 300 + 100) { // 10%
            return MessageSizeType.L;
        } else if (rand < 540 + 300 + 100 + 50) { // 5%
            return MessageSizeType.XL;
        } else { // El resto, 1%
            return MessageSizeType.XXL;
        }
    }

    public MessageSizeType selectSizeTypeRange100() {
        double rand = Math.random() * 100; // Valor entre 0 y 100
        if (rand < 54) {
            return MessageSizeType.S;
        } else if (rand < 54 + 30) {
            return MessageSizeType.M;
        } else if (rand < 84 + 10) {
            return MessageSizeType.L;
        } else if (rand < 94 + 5) {
            return MessageSizeType.XL;
        } else {
            return MessageSizeType.XXL;
        }
    }


    public String getMessage(String messageRange) {

        var mode = Objects.equals(messageRange, MESSAGE_RANGE_100)
                ? this.selectSizeTypeRange100()
                : this.selectSizeTypeRange1000();

        final String filePath = fileMapping.get(mode);

        //log.info("FilePath size {} mode{}", filePath, messagePercentage);

        try (final InputStream inputStream = getClass().getResourceAsStream(filePath);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader.lines()
                    .collect(Collectors.joining());

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero " + filePath, e);
        }
    }

}
