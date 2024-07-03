package resourcemanager;

import resourcemanager.domain.ResourceLimits;
import resourcemanager.domain.TestResult;

public class ResultProcessor {
    public static ResourceLimits determineNewLimits(ResourceLimits previousLimits, TestResult testResult) {
        double failureRate = (double) testResult.testResultLines.stream().filter(l -> !l.success()).toList().size() / testResult.testResultLines.size();
        double averageLatency =
                (double) testResult.testResultLines.stream().map(TestResult.TestResultLine::latency).reduce(0, Integer::sum) /
                         testResult.testResultLines.size();
        if (failureRate < 0.05) {
            return previousLimits;
        }
        return previousLimits;
    }
}
