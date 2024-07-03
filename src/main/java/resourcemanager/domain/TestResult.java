package resourcemanager.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestResult {
    public List<TestResultLine> testResultLines;

    public TestResult(List<TestResultLine> testResultLines) {
        this.testResultLines = testResultLines;
    }

    public static TestResult testResultFromCSVLines(String CSV) {
        List<String> CSVLines = Arrays.stream(CSV.split("\n")).toList();
        List<TestResultLine> testResultLines = new ArrayList<>();
        for (String line : CSVLines.subList(1, CSVLines.size())) {
            String[] splitLine = line.split(",");
            testResultLines.add(new TestResultLine(
                    Long.parseLong(splitLine[0]),
                    Integer.parseInt(splitLine[1]),
                    splitLine[2],
                    Integer.parseInt(splitLine[3]),
                    splitLine[4],
                    splitLine[5],
                    splitLine[6],
                    Boolean.parseBoolean(splitLine[7]),
                    splitLine[8],
                    Long.parseLong(splitLine[9]),
                    Long.parseLong(splitLine[10]),
                    Integer.parseInt(splitLine[11]),
                    Integer.parseInt(splitLine[12]),
                    splitLine[13],
                    Integer.parseInt(splitLine[14]),
                    Integer.parseInt(splitLine[15]),
                    Integer.parseInt(splitLine[16])
            ));
        }
        return new TestResult(testResultLines);
    }

    public List<TestResultLine> getFailedRequests() {
        return this.testResultLines.stream().filter(l -> !l.success).toList();
    }

    public double getAvgLatency() {
        return this.testResultLines.stream().mapToDouble(l -> l.latency).sum() / this.testResultLines.size();
    }

    public double getLastLatency() {
        // There are 20 phases in the test plan, and for this we only want to see the requests from the last phase.
        //  Thus, we determine the starting time of that phase and filter out the requests with a timestamp from before
        //  that time.
        long totalTime = this.testResultLines.getLast().timeStamp - this.testResultLines.getFirst().timeStamp;
        long phaseDuration = totalTime / 20;
        long startingTime = this.testResultLines.getLast().timeStamp - phaseDuration;
        return this.testResultLines.stream().filter(l -> l.timeStamp < startingTime).mapToDouble(l -> l.latency).sum() /
                (this.testResultLines.stream().filter(l -> l.timeStamp < startingTime).count());
    }

    public String toReport() {
        return String.format("""
               ========= Load testing report =========
               Average latency:             %s ms
               Latency during last phase:   %s ms
               Number of non-200 responses: %s
               =======================================
               """,
                this.getAvgLatency(),
                this.getLastLatency(),
                this.getFailedRequests().size()
                );
    }

    public record TestResultLine (long timeStamp, int elapsed, String label, int responseCode, String responseMessage,
                                  String threadName, String dataType, boolean success, String failureMessage, long bytes,
                                  long sentBytes, int grpThreads, int allThreads, String URL, int latency, int idleTime,
                                  int connect) {}
}
