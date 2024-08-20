package orchestrator.resultmonitoring;

import growl.ConfigurationMaker;
import growl.domain.Configuration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import resourcemanager.ResultProcessor;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

@Component
public class FileMonitor {
    private final WatchService watchService;
    private final Set<String> resultFiles;
    private final int resultFilesGoal;
    private final Configuration configuration;
    boolean finished = false;

    @Autowired
    public FileMonitor(WatchService watchService) {
        this.watchService = watchService;
        this.resultFiles = new HashSet<>();
        this.configuration = ConfigurationMaker.makeConfigurationFromFilename("config.json");
        this.resultFilesGoal = this.configuration.tests().samplers().size();
    }

    @Async
    @PostConstruct
    public void launchMonitoring() {
        System.out.println("START_MONITORING");
        ResultProcessor processor = new ResultProcessor();
        try {
            WatchKey key;
            while ((key = watchService.take()) != null && !finished) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().endsWith(".csv")) {
                        // If a CSV has been made or changed, this is likely a results file, which means we have
                        //  something to do.
                        System.out.printf("Event kind: %s; File affected: %s%n", event.kind(), event.context());
                        this.resultFiles.add(event.context().toString());
                        if (this.resultFiles.size() >= resultFilesGoal) {
                            // We have now observed the creation of a number of CSV files equal to the number of CSV result
                            //  files that JMeter should output. Therefore, we assume JMeter is done and start the process
                            //  of creating new configurations and restarting.
                            finished = processor.processNewResults(resultFiles, this.configuration);
                            this.resultFiles.clear();
                        }
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while watching file monitoring");
        }
    }

    @PreDestroy
    public void stopMonitoring() {
        System.out.println("STOP_MONITORING");
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                System.out.println("exception while closing the monitoring service");
            }
        }
    }
}
