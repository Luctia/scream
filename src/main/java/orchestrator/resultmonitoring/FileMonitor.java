package orchestrator.resultmonitoring;

import growl.ConfigurationMaker;
import growl.domain.Configuration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import resourcemanager.ResultProcessor;

import java.io.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class FileMonitor {
    private final WatchService watchService;
    private final Set<String> resultFiles;
    private final Configuration configuration;
    boolean finished = false;

    @Autowired
    public FileMonitor(WatchService watchService) {
        this.watchService = watchService;
        this.resultFiles = new HashSet<>();
        this.configuration = ConfigurationMaker.makeConfigurationFromFilename("config.json");
    }

    @Async
    @PostConstruct
    public void launchMonitoring() throws IOException {
        ResultProcessor processor = new ResultProcessor(configuration);
        processor.buildConfig();
        Runtime.getRuntime().exec(new String[]{"./bin/jmeter", "-n", "-t", "config.jmx"});
        System.out.println("START_MONITORING");
        try {
            WatchKey key;
            while ((key = watchService.take()) != null && !finished) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().endsWith(".csv")) {
                        // If a CSV has been made or changed, this is likely a results file, which means we have
                        //  something to do.
                        this.resultFiles.add(event.context().toString());
                        if (jmeterDone()) {
                            // We have now observed the creation of the finished.csv file, and it contains two lines
                            //  (one for CSV headers and a first result). This means JMeter is done.
                            resultFiles.remove("finished.csv");
                            finished = processor.processNewResults(resultFiles, this.configuration);
                            System.out.println("Processing finished\n");
                            new File("finished.csv").delete();
                            this.resultFiles.forEach(f -> new File(f).delete());
                            this.resultFiles.clear();
                            if (!finished) {
                                processor.buildConfig();
                                Runtime.getRuntime().exec(new String[]{"./bin/jmeter", "-n", "-t", "config.jmx"});
                            }
                        }
                    }
                }
                key.reset();
            }
            System.out.println("Optimal resource limits found:");
            processor.printResources();
            TimeUnit.MINUTES.sleep(60);
        } catch (InterruptedException e) {
            System.out.println("Interrupted while watching file monitoring");
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private static boolean jmeterDone() {
        if (new File("finished.csv").exists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader("finished.csv"));
                int lines = 0;
                while (reader.readLine() != null) lines++;
                reader.close();
                if (lines >= 2) {
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
