package growl.domain;

import java.util.List;
import java.util.Objects;

/**
 * Record for representing global test specifications.
 * @param healthCheckUrl URL that, when requested, should return 200 OK before commencing with tests/benchmarks. Not
 *                       required
 * @param ordered whether to execute all tests in the sequence they were given. Defaults to false
 * @param samplers list of samplers. Required and should not be empty
 */
public record TestSpecs(String healthCheckUrl, boolean ordered, List<Sampler> samplers) {
    public TestSpecs {
        Objects.requireNonNull(samplers, "samplers cannot be null");
        if (samplers.isEmpty()) throw new IllegalArgumentException("samplers may not be empty");
    }
}
