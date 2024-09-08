package orchestrator;

import growl.ConfigurationMaker;
import growl.XMLMaker;
import growl.domain.Configuration;
import io.fabric8.kubernetes.api.model.Quantity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import resourcemanager.ResourceManager;

import java.io.File;
import java.io.IOException;

/**
 * This class serves as the starting point from which the framework will do its job.
 */
@SpringBootApplication
@EnableAsync
public class Orchestrator {
     public static void main(String[] args) throws IOException {
         System.out.println("Starting...");
         // We start by using the growl package to turn the JSON config into a JMeter configuration.
         Configuration configuration = ConfigurationMaker.makeConfigurationFromFilename("config.json");
         XMLMaker.exportXML(configuration, true);
         System.out.println("Deploying Kubernetes...");
         ResourceManager.deployKubernetes(configuration);
         System.out.println("Removing potential old finished.csv...");
         File finished = new File("finished.csv");
         finished.delete();
         System.out.println("Running JMeter");
         Runtime.getRuntime().exec(new String[]{"./bin/jmeter", "-n", "-t", "config.jmx"});
         System.out.println("Startup complete, running spring boot");

         // We then start the Spring Boot application. This will serve a web page that lets us check progress and stay
         //  alive until JMeter is finished.
         SpringApplication.run(Orchestrator.class, args);
    }
}
