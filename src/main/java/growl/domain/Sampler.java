package growl.domain;

import java.util.Objects;

/**
 * Record to represent samplers for benchmarking.
 * @param method HTTP method to use. Defaults to GET
 * @param path path to send request to. Required
 * @param percentage percentage of total load taken by this path. Required
 * @param requestBody body to send with the request
 */
public record Sampler(method method, String path, double percentage, String requestBody) {
    public enum method { GET, POST, PUT, DELETE }

    public Sampler {
        Objects.requireNonNull(path, "path cannot be null");
        Objects.requireNonNull(percentage, "percentage cannot be null");
        if (percentage < 0) {throw new IllegalArgumentException("Percentage cannot be negative");}
    }
}
