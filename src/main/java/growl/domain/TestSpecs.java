package growl.domain;

import java.util.List;

public record TestSpecs(String healthCheckUrl, boolean ordered, List<Sampler> samplers) {
}
