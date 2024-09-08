package resourcemanager;

import growl.domain.Configuration;
import growl.domain.PerformanceDemands;
import io.fabric8.kubernetes.api.model.Quantity;
import resourcemanager.domain.ResourceLimits;
import resourcemanager.domain.TestResult;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ResultProcessor {
    /**
     * The resource limits that have been used for the corresponding service. These are updated before benchmarking is
     * run with these limits.
     */
    private Map<String, ResourceLimits> usedResourceLimits = new HashMap<>();
    /**
     * The highest tried resource limits for the corresponding service (filename) with which the benchmarking tests
     * failed.
     */
    private final Map<String, ResourceLimits> highestFailedResources = new HashMap<>();

    /**
     * Read the results in the given files and determine whether the goal is met, or to loosen or tighten resource
     * limits.
     * @param resultFileNames names of the files containing the relevant benchmark results
     * @param configuration the configuration with which the tool is running
     * @return whether the correct limits have been found
     */
    public boolean processNewResults(Set<String> resultFileNames, Configuration configuration) {
        System.out.println("Processing new results...");
        try {
            Set<File> resultFiles = resultFileNames.stream()
                    .map(Paths::get)
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
            Map<String, TestResult> testResults = new HashMap<>();
            for (File resultFile : resultFiles) {
                Scanner myReader = new Scanner(resultFile);
                StringBuilder fileContents = new StringBuilder();
                while (myReader.hasNextLine()) {
                    fileContents.append(myReader.nextLine()).append("\n");
                }
                myReader.close();
                TestResult testResult = TestResult.testResultFromCSVLines(fileContents.toString());
                testResults.put(resultFile.getName(), testResult);
            }
            boolean allPassed = true;
            for (String filename : testResults.keySet()) {
                // Determine whether the goals are met within margins, otherwise determine new constraints and run again.
                // First, get the resource limits that were used. If these do not yet exist, this is the first run. The
                //  first run is always performed with 500m CPU and 500M memory, so we can use that as default.
                final ResourceLimits usedLimits = usedResourceLimits
                        .getOrDefault(filename, new ResourceLimits(new Quantity("500m"), new Quantity("500M")));
                if (evaluatePerformance(configuration.performance(), testResults.get(filename)) > 0) {
                    // Must loosen limits
                    this.updateHighestFailedLimits(filename, usedLimits);
                    final ResourceLimits newLimits = new ResourceLimits(
                            multiplyQuantity(usedLimits.cpu(), 1 + evaluatePerformance(configuration.performance(), testResults.get(filename))),
                            multiplyQuantity(usedLimits.memory(), 1 + evaluatePerformance(configuration.performance(), testResults.get(filename)))
                    );
                    ResourceManager.setNewResourceLimits(newLimits, extractId(filename), configuration);
                    System.out.printf("Undershot with limits: %s%n", usedLimits); // TODO these are the same lol
                    System.out.printf("Trying again with limits: %s%n", newLimits);
                    this.usedResourceLimits.put(filename, newLimits);
                    allPassed = false;
                } else {
                    // Tests passed, now we must check whether the limits are tight enough.
                    double overshotCoefficient = checkOvershoot(usedLimits, filename);
                    if (overshotCoefficient > 0) {
                        // Must tighten memory limits
                        allPassed = false;
                        final ResourceLimits newLimits = new ResourceLimits(usedLimits.cpu(), multiplyQuantity(usedLimits.memory(), 1 - (overshotCoefficient * 0.8)));
                        ResourceManager.setNewResourceLimits(newLimits, extractId(filename), configuration);
                        this.usedResourceLimits.put(filename, newLimits);
                    } else if (overshotCoefficient < 0) {
                        // Must tighten CPU limits
                        allPassed = false;
                        final ResourceLimits newLimits = new ResourceLimits(multiplyQuantity(usedLimits.cpu(), 1 - (overshotCoefficient * -0.8)), usedLimits.memory());
                        ResourceManager.setNewResourceLimits(newLimits, extractId(filename), configuration);
                        this.usedResourceLimits.put(filename, newLimits);
                    }
                }
            }
            return allPassed;
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
        if (testResult.getLastLatency() > demands.latency() || failureRate > demands.failureRate()) {
            return testResult.getLastLatency() / demands.latency();
        }
        return 0;
    }

    private double checkOvershoot(ResourceLimits recentResourceLimits, String fileName) throws Exception {
        if (highestFailedResources.isEmpty()) {
            // This means this was the first try, but if we get here it means the tests passed.
            // Therefore, we slice cpu in half as a default behaviour.
            return -0.5;
        }
//        if (recentResourceLimits.cpu().getNumericalAmount().compareTo(highestFailedResources.get(fileName).cpu().getNumericalAmount()) < 0
//                || recentResourceLimits.memory().getNumericalAmount().compareTo(highestFailedResources.get(fileName).memory().getNumericalAmount()) < 0) {
//            throw new Exception("Unexpected behavior: tests passed with fewer resources than previously failed tests.");
//        }
        System.out.println("Checking overshoot...");
        System.out.printf("Recent resource limits: %s%n", recentResourceLimits);
        System.out.printf("Highest failed resource limits: %s%n", highestFailedResources.get(fileName));
        double cpuCoefficient = recentResourceLimits.cpu().getNumericalAmount().divide(highestFailedResources.get(fileName).cpu().getNumericalAmount(), RoundingMode.UP).subtract(new BigDecimal("1")).doubleValue();
        if (cpuCoefficient > 0.1) {
            return -cpuCoefficient;
        }
        double memoryCoefficient = recentResourceLimits.memory().getNumericalAmount().divide(highestFailedResources.get(fileName).memory().getNumericalAmount(), RoundingMode.UP).subtract(new BigDecimal("1")).doubleValue();
        if (memoryCoefficient > 0.1) {
            return memoryCoefficient;
        }
        return 0;
    }

    private void updateHighestFailedLimits(String filename, ResourceLimits limits) {
        if (highestFailedResources.containsKey(filename)) {
            ResourceLimits currentLimits = highestFailedResources.get(filename);
            if (currentLimits.cpu().compareTo(limits.cpu()) < 1) {
                currentLimits = new ResourceLimits(limits.cpu(), currentLimits.memory());
                highestFailedResources.put(filename, currentLimits);
            }
            if (highestFailedResources.get(filename).memory().compareTo(limits.memory()) < 1) {
                currentLimits = new ResourceLimits(currentLimits.cpu(), limits.memory());
                highestFailedResources.put(filename, currentLimits);
            }
        } else {
            highestFailedResources.put(filename, limits);
        }
//        if (highestFailedResources.containsKey(filename) && (highestFailedResources.get(filename).cpu().compareTo(limits.cpu()) < 1 || highestFailedResources.get(filename).memory().compareTo(limits.memory()) < 1)) {
//            highestFailedResources.put(filename, limits);
//        }
    }

    private static String extractId(String filename) {
        return filename.substring(filename.indexOf('_') + 1, filename.indexOf('_', filename.indexOf('_') + 1));
    }

    private static Quantity multiplyQuantity(Quantity quantity, double multiplier) {
        Quantity newQuantity = new Quantity();
        newQuantity.setAmount(new BigDecimal(quantity.getAmount()).multiply(new BigDecimal(multiplier)).toString());
        newQuantity.setFormat(quantity.getFormat());
        return newQuantity;
    }

    public void printResources(Configuration configuration) throws Exception {
        if (this.usedResourceLimits.isEmpty()) {
            System.out.println("No resource limits yet");
        }
        for (String key : this.usedResourceLimits.keySet()) {
            var realResources = ResourceManager.getResourceLimits(extractId(key), configuration);
            System.out.println("For " + key);
            System.out.println("CPU: " + realResources.cpu());
            System.out.println("Memory: " + realResources.memory());
            System.out.println();
        }
    }
}
