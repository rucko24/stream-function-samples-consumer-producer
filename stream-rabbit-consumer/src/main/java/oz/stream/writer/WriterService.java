package oz.stream.writer;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
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
public class WriterService {

    private static final DateTimeFormatter FORMATER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATER_FILE_NAME = DateTimeFormatter.ofPattern("HH_mm_ss_SSSSS");
    private static final Path OUTPUT = Path.of("/home/rubn/logs/logs.txt");
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

        var stamped = STAMPED_LOCK.writeLock();

        try (final BufferedWriter writer = Files.newBufferedWriter(OUTPUT, StandardOpenOption.APPEND)) {

            //HH:mm;latency
            final String line = FORMATER.format(LocalTime.now()) + ";" + latency;
            writer.write(line);
            writer.newLine();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            STAMPED_LOCK.unlockWrite(stamped);
        }

    }

}
