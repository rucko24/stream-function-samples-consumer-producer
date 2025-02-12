package oz.stream.writer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class WriterService {

    private static final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Path OUTPUT = Path.of("/home/rubn/logs/logs.txt");


    @Bean
    public CommandLineRunner createFile() {
        return args -> {
            try (final BufferedWriter writer = Files.newBufferedWriter(OUTPUT, StandardOpenOption.CREATE)) {

                writer.write("");//empty on boot

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

    }

    public void writer(long latency) {

        try (final BufferedWriter writer = Files.newBufferedWriter(OUTPUT, StandardOpenOption.APPEND)) {

            //HH:mm;latency
            final String line = FORMATER.format(LocalTime.now()) + ";" + latency;
            writer.write(line);
            writer.newLine();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
