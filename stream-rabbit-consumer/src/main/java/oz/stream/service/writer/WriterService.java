package oz.stream.service.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import java.util.concurrent.locks.StampedLock;

@Log4j2
@Service
@RequiredArgsConstructor
public class WriterService {

    private static final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATER_FILE_NAME = DateTimeFormatter.ofPattern("HH_mm_ss_SSSSS");
    private static final Path OUTPUT = Path.of("/home/rubn/logs/logs-" + FORMATER_FILE_NAME.format(LocalTime.now()) + ".txt");
    private static final StampedLock STAMPED_LOCK = new StampedLock();

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

            this.writeLine(writer, latency);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    private void writeLine(BufferedWriter writer, long latency) throws IOException {

        final String line = FORMATER.format(LocalTime.now()) + ";" + latency;
        writer.write(line);
        writer.newLine();

    }

}
