package oz.stream.service;

import org.springframework.stereotype.Service;
import oz.stream.enums.MessageSizeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageFileService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Map<MessageSizeType, String> fileMapping = Map.of(
        MessageSizeType.S, "/messages/message_S.json",
        MessageSizeType.M, "/messages/message_M.json",
        MessageSizeType.L, "/messages/message_L.json",
        MessageSizeType.XL, "/messages/message_XL.json",
        MessageSizeType.XXL, "/messages/message_XXL.json"
    );

    public MessageSizeType selectSizeType() {
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

    public String getMessage(MessageSizeType sizeType) {
        String filePath = fileMapping.get(sizeType);
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero " + filePath, e);
        }
    }
}
