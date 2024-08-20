package orchestrator.resultmonitoring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;

@Configuration
public class MonitoringConfig {

    @Bean
    public WatchService watchService() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("");

            if (!Files.isDirectory(path)) {
                throw new RuntimeException("incorrect monitoring folder: " + path);
            }

            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE
            );
            return watchService;
        } catch (IOException e) {
            System.err.println("Oopsie");
        }
        return null;
    }
}
