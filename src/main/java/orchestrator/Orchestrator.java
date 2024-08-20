package orchestrator;

import growl.ConfigurationMaker;
import growl.XMLMaker;
import growl.domain.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import resourcemanager.ResourceManager;

import java.io.IOException;

/**
 * This class serves as the starting point from which the framework will do its job.
 */
@SpringBootApplication
@EnableAsync
public class Orchestrator {
     public static void main(String[] args) throws IOException {
         // We start by using the growl package to turn the JSON config into a JMeter configuration.
         Configuration configuration = ConfigurationMaker.makeConfigurationFromFilename("config.json");
         XMLMaker.exportXML(configuration, true);
         ResourceManager.deployKubernetes(configuration);
         Runtime.getRuntime().exec(new String[]{"./bin/jmeter", "-n", "-t", "config.jmx"});

         // We then start the Spring Boot application. This will serve a web page that lets us check progress and stay
         //  alive until JMeter is finished.
         SpringApplication.run(Orchestrator.class, args);
    }
}
