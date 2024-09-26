package resourcemanager;

import growl.domain.Configuration;
import growl.domain.PerformanceDemands;
import growl.domain.Sampler;
import growl.domain.TestSpecs;
import resourcemanager.domain.Target;
import resourcemanager.domain.TestResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static growl.XMLMaker.createTestplanXML;

public class ResultProcessor {
    private final Map<String, Target> targets;
    private final Configuration configuration;

    public ResultProcessor(Configuration configuration) {
        this.configuration = configuration;
        this.targets = configuration.tests().samplers().stream()
                .collect(Collectors.groupingBy(Sampler::targetId))
                .entrySet().stream()
                .map(e -> new Target(e.getKey(), e.getValue(), configuration))
                .collect(Collectors.toMap(Target::getTargetId, target -> target));
    }

    /**
     * Read the results in the given files and determine whether the goal is met, or to loosen or tighten resource
     * limits.
     * @param resultFileNames names of the files containing the relevant benchmark results
     * @param configuration the configuration with which the tool is running
     * @return whether the correct limits have been found
     */
    public boolean processNewResults(Set<String> resultFileNames, Configuration configuration) {
        System.out.println("Processing new results...");
        StringBuilder fileContents = new StringBuilder();
        try {
            Set<File> resultFiles = resultFileNames.stream()
                    .map(Paths::get)
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
            Map<String, TestResult> testResults = new HashMap<>();
            for (File resultFile : resultFiles) {
                Scanner myReader = new Scanner(resultFile);
                fileContents = new StringBuilder();
                while (myReader.hasNextLine()) {
                    fileContents.append(myReader.nextLine()).append("\n");
                }
                myReader.close();
                TestResult testResult = TestResult.testResultFromCSVLines(fileContents.toString());
                testResults.put(resultFile.getName(), testResult);
            }
            for (String filename : testResults.keySet()) {
                Target target = targets.get(extractId(filename));
                System.out.println("CSV lines:");
                System.out.println(fileContents);
                System.out.println("Result lines:");
                testResults.get(filename).testResultLines.forEach(System.out::println);
                System.out.println();
                boolean pass = evaluatePerformance(configuration.performance(), testResults.get(filename)) == 0;
                System.out.println("Resulting in a pass: " + (pass ? "yes" : "no"));
                target.processTestResults(evaluatePerformance(configuration.performance(), testResults.get(filename)) == 0);
                if (!target.done()) target.applyResourceLimits();
            }
            return targets.values().stream().allMatch(Target::done);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluates performance.
     * @param demands
     * @param testResult
     * @return 0 if performance demands were met, otherwise a double representing by how much they were missed. 0.5
     * indicates they were missed by 50% and resources might have to be increased by 50%.
     */
    private double evaluatePerformance(PerformanceDemands demands, TestResult testResult) {
        double failureRate = (double) testResult.getFailedRequests().size() / testResult.testResultLines.size();
        System.out.printf("Failure rate: %s%n", failureRate);
        if (testResult.getLastLatency() > demands.latency() || failureRate > demands.failureRate()) {
            System.out.println("Did not pass");
            double naiveRes = testResult.getLastLatency() / demands.latency();
            if (naiveRes == 0) {
                naiveRes = failureRate;
            }
            return naiveRes;
        }
        return 0;
    }

    private static String extractId(String filename) {
        return filename.substring(filename.indexOf('_') + 1, filename.indexOf('_', filename.indexOf('_') + 1));
    }

    public void printResources() {
        if (!this.targets.entrySet().stream().allMatch(e -> e.getValue().done())) {
            System.out.println("No resource limits yet");
        }
        for (Map.Entry entry : this.targets.entrySet()) {
            System.out.println("For " + entry.getKey());
            System.out.println("CPU: " + ((Target) entry.getValue()).getResourceLimits().cpu());
            System.out.println("Memory: " + ((Target) entry.getValue()).getResourceLimits().memory());
            System.out.println();
        }
    }

    public void buildConfig() {
        Configuration tempConfig = new Configuration(
                configuration.namespace(),
                configuration.images(),
                new TestSpecs(
                        configuration.tests().healthCheckUrl(),
                        configuration.tests().healthCheckTarget(),
                        configuration.tests().healthCheckHttps(),
                        configuration.tests().ordered(),
                        this.targets.values().stream().filter(t -> !t.done()).map(Target::getCurrentSampler).toList()
                ),
                configuration.performance());
        String xml = createTestplanXML(tempConfig, false);
        try {
            PrintWriter writer = new PrintWriter("config.jmx");
            writer.print(xml);
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not create config.jmx file");
        }
    }
}
