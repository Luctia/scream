package growl.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Record for representing the highest configuration parameters in the framework.
 * @param platform information about the platform the deploying and benchmarking will take place on. Required
 * @param images list of images to be deployed. Required and may not be empty
 * @param tests test specifications. Required
 * @param performance performance demands. Required
 */
public record Configuration(String platform, List<Image> images, TestSpecs tests, PerformanceDemands performance) {
    public Configuration {
        Objects.requireNonNull(platform, "platform cannot be null");
        Objects.requireNonNull(images, "images cannot be null");
        if (images.isEmpty()) throw new IllegalArgumentException("images should not be empty");

        Set<String> imageIds = images.stream().map(Image::id).collect(Collectors.toSet());
        if (imageIds.size() != images.size()) throw new IllegalArgumentException("Duplicate image IDs");

        Objects.requireNonNull(tests, "tests cannot be null");
        Objects.requireNonNull(performance, "performance cannot be null");
    }
}
