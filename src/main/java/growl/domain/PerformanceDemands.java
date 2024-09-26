package growl.domain;

import java.util.Objects;

/**
 * Record for representing performance demands for the framework.
 * @param throughput the desired throughput in requests per {@link PerformanceDemands#throughputTimeUnit}
 * @param throughputTimeUnit the time unit used to quantify throughput. Defaults to minute
 * @param latency maximum latency allowed in milliseconds
 */
public record PerformanceDemands(int throughput, TimeUnit throughputTimeUnit, int latency, double failureRate, double maxOvershoot) {
    public enum TimeUnit { MINUTE }

    public PerformanceDemands {
        Objects.requireNonNull(throughput, "throughput cannot be null");
        Objects.requireNonNull(latency, "latency cannot be null");
        Objects.requireNonNull(failureRate, "failure rate cannot be null");
        if (throughput < 0) {throw new IllegalArgumentException("Throughput cannot be negative");}
        if (latency < 0) {throw new IllegalArgumentException("Latency cannot be negative");}
        if (failureRate < 0) {throw new IllegalArgumentException("Failure rate cannot be negative");}
        if (maxOvershoot <= 0) {maxOvershoot = 0.1;}
    }
}
